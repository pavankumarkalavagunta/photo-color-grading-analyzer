package com.photoanalyzer.util;

import com.photoanalyzer.model.AnalysisReport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

/**
 * Client to communicate with Python FastAPI analyzer service.
 */
public class PythonAnalyzerClient {

    private static final String DEFAULT_URL = "http://127.0.0.1:8000";
    private final String baseUrl;
    private final HttpClient httpClient;

    public PythonAnalyzerClient() {
        this(DEFAULT_URL);
    }

    public PythonAnalyzerClient(String baseUrl) {
        this.baseUrl = baseUrl != null ? baseUrl.replaceAll("/+$", "") : DEFAULT_URL;
        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    /**
     * Checks if the Python microservice is up and responsive.
     */
    public boolean checkHealth() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/health"))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Sends multipart image bytes to /analyze and returns the parsed AnalysisReport.
     */
    public AnalysisReport analyzeImage(byte[] imageBytes, String filename, String contentType) throws IOException, InterruptedException {
        if (imageBytes == null || imageBytes.length == 0) {
            throw new IllegalArgumentException("Image data cannot be empty");
        }
        if (filename == null || filename.isBlank()) {
            filename = "upload.jpg";
        }
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        String boundary = "----PhotoAnalyzerBoundary" + System.currentTimeMillis();
        byte[] multipartBody = buildMultipartBody(boundary, imageBytes, filename, contentType);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/analyze"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .timeout(Duration.ofSeconds(30))
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new IOException("Cannot connect to Python Image Analyzer at " + baseUrl + 
                                  ". Please ensure 'python -m uvicorn app.main:app --port 8000' is running.", e);
        }

        if (response.statusCode() != 200) {
            throw new IOException("Python Analyzer error (" + response.statusCode() + "): " + response.body());
        }

        String jsonResponse = response.body();
        return JsonUtil.mapToReport(jsonResponse);
    }

    private byte[] buildMultipartBody(String boundary, byte[] fileBytes, String filename, String contentType) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String lineBreak = "\r\n";

        // --boundary\r\n
        baos.write(("--" + boundary + lineBreak).getBytes(StandardCharsets.UTF_8));
        // Content-Disposition: form-data; name="file"; filename="..."\r\n
        baos.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + filename + "\"" + lineBreak).getBytes(StandardCharsets.UTF_8));
        // Content-Type: ...\r\n\r\n
        baos.write(("Content-Type: " + contentType + lineBreak + lineBreak).getBytes(StandardCharsets.UTF_8));
        // file binary bytes
        baos.write(fileBytes);
        baos.write(lineBreak.getBytes(StandardCharsets.UTF_8));
        // --boundary--\r\n
        baos.write(("--" + boundary + "--" + lineBreak).getBytes(StandardCharsets.UTF_8));

        return baos.toByteArray();
    }
}
