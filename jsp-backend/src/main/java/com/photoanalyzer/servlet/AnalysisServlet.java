package com.photoanalyzer.servlet;

import com.photoanalyzer.dao.AnalysisDAO;
import com.photoanalyzer.model.AnalysisReport;
import com.photoanalyzer.util.PythonAnalyzerClient;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@WebServlet(name = "AnalysisServlet", urlPatterns = {"/analyze", "/report"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1 MB
        maxFileSize = 25 * 1024 * 1024,        // 25 MB
        maxRequestSize = 30 * 1024 * 1024      // 30 MB
)
public class AnalysisServlet extends HttpServlet {

    private final PythonAnalyzerClient analyzerClient = new PythonAnalyzerClient();
    private final AnalysisDAO dao = AnalysisDAO.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id != null && !id.isBlank()) {
            AnalysisReport report = dao.getById(id.trim());
            if (report != null) {
                request.setAttribute("report", report);
                request.getRequestDispatcher("/report.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("errorMessage", "Analysis report with ID '" + id + "' was not found.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/upload.jsp");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            Part filePart = request.getPart("image");
            if (filePart == null) {
                filePart = request.getPart("file");
            }

            if (filePart == null || filePart.getSize() == 0) {
                request.setAttribute("errorMessage", "Please select a photo to analyze.");
                request.getRequestDispatcher("/upload.jsp").forward(request, response);
                return;
            }

            String submittedFileName = filePart.getSubmittedFileName();
            if (submittedFileName == null || submittedFileName.isBlank()) {
                submittedFileName = "upload.jpg";
            }

            String contentType = filePart.getContentType();
            if (contentType == null || (!contentType.startsWith("image/") && !contentType.equals("application/octet-stream"))) {
                request.setAttribute("errorMessage", "Invalid file type. Only JPEG, PNG, and WebP images are supported.");
                request.getRequestDispatcher("/upload.jsp").forward(request, response);
                return;
            }

            // Read image bytes
            byte[] imageBytes;
            try (InputStream in = filePart.getInputStream();
                 ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                imageBytes = out.toByteArray();
            }

            if (imageBytes.length == 0) {
                request.setAttribute("errorMessage", "Uploaded file is empty.");
                request.getRequestDispatcher("/upload.jsp").forward(request, response);
                return;
            }

            // Generate base64 data URL for instant image display in report and history
            String mimeType = contentType.startsWith("image/") ? contentType : "image/jpeg";
            String base64Img = Base64.getEncoder().encodeToString(imageBytes);
            String imageDataUrl = "data:" + mimeType + ";base64," + base64Img;

            // Analyze via Python microservice
            AnalysisReport report = analyzerClient.analyzeImage(imageBytes, submittedFileName, mimeType);
            report.setImageDataUrl(imageDataUrl);

            // Persist to DAO
            dao.save(report);

            // Forward to presentation report view
            request.setAttribute("report", report);
            request.getRequestDispatcher("/report.jsp").forward(request, response);

        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg == null || errorMsg.isBlank()) {
                errorMsg = e.getClass().getSimpleName();
            }
            request.setAttribute("errorMessage", "Analysis failed: " + errorMsg);
            request.getRequestDispatcher("/upload.jsp").forward(request, response);
        }
    }
}
