package com.photoanalyzer.servlet;

import com.photoanalyzer.dao.AnalysisDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(name = "DeleteServlet", urlPatterns = {"/delete"})
public class DeleteServlet extends HttpServlet {

    private final AnalysisDAO dao = AnalysisDAO.getInstance();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doDeleteAction(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doDeleteAction(request, response);
    }

    private void doDeleteAction(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String id = request.getParameter("id");
        if (id != null && !id.isBlank()) {
            dao.delete(id.trim());
        }
        response.sendRedirect(request.getContextPath() + "/history");
    }
}
