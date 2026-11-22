<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.photoanalyzer.model.AnalysisReport"%>
<%@ page import="com.photoanalyzer.model.DominantColor"%>
<%@ page import="java.util.List"%>
<%
    @SuppressWarnings("unchecked")
    List<AnalysisReport> reports = (List<AnalysisReport>) request.getAttribute("reports");
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Analysis History - Photo Color Grading Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <%@ include file="navbar.jspf" %>

    <main class="main-content">
        <div class="container">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 2rem; flex-wrap: wrap; gap: 1rem;">
                <div>
                    <h1 style="font-size: 2rem; font-weight: 700; margin-bottom: 0.25rem;">Analysis History</h1>
                    <p style="color: var(--text-secondary); font-size: 0.95rem;">
                        Review past photo analyses, color characteristics, and editing reports.
                    </p>
                </div>
                <div>
                    <a href="${pageContext.request.contextPath}/upload.jsp" class="btn btn-primary">
                        ➕ Analyze New Photo
                    </a>
                </div>
            </div>

            <% if (reports == null || reports.isEmpty()) { %>
                <!-- Empty State -->
                <div class="card" style="text-align: center; padding: 4rem 2rem;">
                    <div style="font-size: 3.5rem; margin-bottom: 1rem; color: var(--accent-blue);">📁</div>
                    <h2 style="font-size: 1.35rem; font-weight: 700; margin-bottom: 0.5rem;">No Analysis History Yet</h2>
                    <p style="color: var(--text-secondary); max-width: 480px; margin: 0 auto 2rem auto; font-size: 0.95rem;">
                        You haven't uploaded any photographs for analysis yet. Upload an image to calculate histograms, dominant colors, and lighting metrics.
                    </p>
                    <a href="${pageContext.request.contextPath}/upload.jsp" class="btn btn-primary" style="padding: 0.75rem 2rem;">
                        🚀 Upload First Photo
                    </a>
                </div>
            <% } else { %>
                <!-- History Table / Cards -->
                <div class="card" style="padding: 0; overflow-x: auto;">
                    <table class="data-table">
                        <thead>
                            <tr>
                                <th>Photo</th>
                                <th>Filename & Details</th>
                                <th>Palette</th>
                                <th>Exposure & Tone</th>
                                <th>Focus & EV</th>
                                <th>Analyzed On</th>
                                <th style="text-align: right;">Actions</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (AnalysisReport r : reports) { %>
                                <tr>
                                    <!-- Thumbnail -->
                                    <td style="width: 80px;">
                                        <% if (r.getImageDataUrl() != null && !r.getImageDataUrl().isBlank()) { %>
                                            <img src="<%= r.getImageDataUrl() %>" alt="<%= r.getFilename() %>" 
                                                 style="width: 64px; height: 64px; object-fit: cover; border-radius: 6px; border: 1px solid var(--bg-card-border);">
                                        <% } else { %>
                                            <div style="width: 64px; height: 64px; background: rgba(255,255,255,0.05); border-radius: 6px; display: flex; align-items: center; justify-content: center; color: var(--text-muted); font-size: 1.5rem;">
                                                📷
                                            </div>
                                        <% } %>
                                    </td>

                                    <!-- Filename & Details -->
                                    <td>
                                        <div style="font-weight: 600; color: var(--text-primary); margin-bottom: 0.2rem;">
                                            <%= r.getFilename() %>
                                        </div>
                                        <div style="font-size: 0.8rem; color: var(--text-secondary);">
                                            <%= r.getWidth() %> × <%= r.getHeight() %> px &bull; <%= r.getFormat() %>
                                        </div>
                                    </td>

                                    <!-- Palette Dots -->
                                    <td>
                                        <div style="display: flex; gap: 4px; align-items: center;">
                                            <% 
                                                List<DominantColor> colors = r.getDominantColors();
                                                if (colors != null) {
                                                    for (DominantColor dc : colors) {
                                            %>
                                                <div style="width: 14px; height: 14px; border-radius: 50%; background-color: <%= dc.getHex() %>; border: 1px solid rgba(255,255,255,0.2);" title="<%= dc.getHex() %> (<%= String.format("%.1f", dc.getPercentage()) %>%)"></div>
                                            <% 
                                                    }
                                                }
                                            %>
                                        </div>
                                        <div style="font-size: 0.75rem; color: var(--text-muted); margin-top: 0.25rem;">
                                            <%= r.getHarmonyType() %>
                                        </div>
                                    </td>

                                    <!-- Exposure & Tone -->
                                    <td>
                                        <div style="font-size: 0.82rem;">
                                            <span>Bri: <strong><%= String.format("%.0f", r.getNormalizedBrightness()) %>%</strong></span> &bull;
                                            <span>Con: <strong><%= String.format("%.0f", r.getNormalizedContrast()) %>%</strong></span>
                                        </div>
                                        <div style="font-size: 0.78rem; color: var(--text-secondary); margin-top: 0.2rem;">
                                            <%= r.getTemperatureLabel() %> (<%= r.getWarmthIndex() >= 0 ? "+" : "" %><%= String.format("%.1f", r.getWarmthIndex()) %>)
                                        </div>
                                    </td>

                                    <!-- Focus & EV -->
                                    <td>
                                        <div style="font-size: 0.82rem;">
                                            <span>Sharp: <strong><%= String.format("%.0f", r.getSharpnessScore()) %>/100</strong></span>
                                        </div>
                                        <div style="font-size: 0.78rem; color: var(--text-secondary); margin-top: 0.2rem;">
                                            <%= String.format("%.1f", r.getEvStops()) %> EV &bull; <%= r.getKeyStyle() %>
                                        </div>
                                    </td>

                                    <!-- Date -->
                                    <td style="font-size: 0.8rem; color: var(--text-secondary);">
                                        <%= r.getCreatedAt() != null ? r.getCreatedAt().replace("T", " ").replaceAll("\\..*", "") : "Recently" %>
                                    </td>

                                    <!-- Actions -->
                                    <td style="text-align: right; white-space: nowrap;">
                                        <a href="${pageContext.request.contextPath}/analyze?id=<%= r.getId() %>" class="btn btn-secondary" style="font-size: 0.8rem; padding: 0.4rem 0.75rem;">
                                            👁️ View
                                        </a>
                                        <a href="${pageContext.request.contextPath}/delete?id=<%= r.getId() %>" onclick="return confirm('Are you sure you want to delete this analysis report?');" class="btn btn-danger" style="font-size: 0.8rem; padding: 0.4rem 0.65rem; margin-left: 0.35rem;">
                                            🗑️
                                        </a>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } %>

        </div>
    </main>

    <%@ include file="footer.jspf" %>

</body>
</html>
