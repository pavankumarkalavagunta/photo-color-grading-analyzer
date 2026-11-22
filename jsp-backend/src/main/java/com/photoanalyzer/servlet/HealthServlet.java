package com.photoanalyzer.servlet;

import com.photoanalyzer.dao.AnalysisDAO;
import com.photoanalyzer.util.PythonAnalyzerClient;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "HealthServlet", urlPatterns = {"/health"})
public class HealthServlet extends HttpServlet {

    private final PythonAnalyzerClient analyzerClient = new PythonAnalyzerClient();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");

        boolean pythonHealthy = analyzerClient.checkHealth();
        int savedReportsCount = AnalysisDAO.getInstance().count();

        PrintWriter out = response.getWriter();
        out.write("{"
                + "\"status\":\"ok\","
                + "\"service\":\"Photo Color Grading Analyzer - JSP/Tomcat 10 Backend\","
                + "\"python_engine_connected\":" + pythonHealthy + ","
                + "\"saved_reports_count\":" + savedReportsCount + ","
                + "\"timestamp\":\"" + java.time.Instant.now().toString() + "\""
                + "}");
    }
}
