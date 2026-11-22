package com.photoanalyzer.servlet;

import com.photoanalyzer.dao.AnalysisDAO;
import com.photoanalyzer.model.AnalysisReport;
import com.photoanalyzer.util.JsonUtil;
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
import java.io.PrintWriter;
import java.util.Base64;
import java.util.List;

@WebServlet(name = "ApiAnalysisServlet", urlPatterns = {"/api/analysis/*"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 25 * 1024 * 1024,
        maxRequestSize = 30 * 1024 * 1024
)
public class ApiAnalysisServlet extends HttpServlet {

    private final PythonAnalyzerClient analyzerClient = new PythonAnalyzerClient();
    private final AnalysisDAO dao = AnalysisDAO.getInstance();

    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) {
        setCorsHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        setCorsHeaders(response);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/") || pathInfo.isBlank()) {
            // GET /api/analysis -> return all reports
            List<AnalysisReport> list = dao.getAll();
            out.write(JsonUtil.toJson(list));
        } else {
            // GET /api/analysis/{id}
            String id = pathInfo.replaceFirst("^/", "").trim();
            AnalysisReport report = dao.getById(id);
            if (report != null) {
                out.write(JsonUtil.toJson(report));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\":\"Report not found\",\"id\":\"" + id + "\"}");
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        setCorsHeaders(response);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Part filePart = request.getPart("image");
            if (filePart == null) {
                filePart = request.getPart("file");
            }

            if (filePart == null || filePart.getSize() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"error\":\"No image file uploaded\"}");
                return;
            }

            String filename = filePart.getSubmittedFileName();
            if (filename == null || filename.isBlank()) filename = "upload.jpg";
            String contentType = filePart.getContentType();
            if (contentType == null) contentType = "image/jpeg";

            byte[] imageBytes;
            try (InputStream in = filePart.getInputStream();
                 ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                byte[] buf = new byte[8192];
                int len;
                while ((len = in.read(buf)) != -1) {
                    baos.write(buf, 0, len);
                }
                imageBytes = baos.toByteArray();
            }

            String mimeType = contentType.startsWith("image/") ? contentType : "image/jpeg";
            String base64Img = Base64.getEncoder().encodeToString(imageBytes);
            String imageDataUrl = "data:" + mimeType + ";base64," + base64Img;

            AnalysisReport report = analyzerClient.analyzeImage(imageBytes, filename, mimeType);
            report.setImageDataUrl(imageDataUrl);

            dao.save(report);

            response.setStatus(HttpServletResponse.SC_CREATED);
            out.write(JsonUtil.toJson(report));

        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"" + (e.getMessage() != null ? e.getMessage().replace("\"", "\\\"") : "Internal error") + "\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        setCorsHeaders(response);
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        String pathInfo = request.getPathInfo();
        if (pathInfo != null && !pathInfo.equals("/")) {
            String id = pathInfo.replaceFirst("^/", "").trim();
            boolean deleted = dao.delete(id);
            if (deleted) {
                out.write("{\"message\":\"Report deleted successfully\",\"id\":\"" + id + "\"}");
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"error\":\"Report not found\",\"id\":\"" + id + "\"}");
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"error\":\"Report ID required\"}");
        }
    }
}
