package com.photoanalyzer.servlet;

import com.photoanalyzer.dao.AnalysisDAO;
import com.photoanalyzer.model.AnalysisReport;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "HistoryServlet", urlPatterns = {"/history"})
public class HistoryServlet extends HttpServlet {

    private final AnalysisDAO dao = AnalysisDAO.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<AnalysisReport> reports = dao.getAll();
        request.setAttribute("reports", reports);
        request.getRequestDispatcher("/history.jsp").forward(request, response);
    }
}
