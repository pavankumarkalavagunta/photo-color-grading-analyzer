<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.photoanalyzer.model.AnalysisReport"%>
<%@ page import="com.photoanalyzer.model.DominantColor"%>
<%@ page import="com.photoanalyzer.model.Recommendation"%>
<%@ page import="com.photoanalyzer.util.JsonUtil"%>
<%@ page import="java.util.List"%>
<%
    AnalysisReport report = (AnalysisReport) request.getAttribute("report");
    if (report == null) {
        response.sendRedirect(request.getContextPath() + "/upload.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Analysis Report - <%= report.getFilename() %></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .channel-btn {
            background: rgba(255, 255, 255, 0.05);
            border: 1px solid var(--bg-card-border);
            color: var(--text-secondary);
            padding: 0.35rem 0.75rem;
            border-radius: 6px;
            font-size: 0.8rem;
            cursor: pointer;
            transition: var(--transition);
        }
        .channel-btn.active {
            background: #2563eb;
            color: #ffffff;
            border-color: #3b82f6;
        }
        .clipping-pill {
            display: inline-flex;
            align-items: center;
            gap: 0.4rem;
            padding: 0.35rem 0.75rem;
            border-radius: 6px;
            font-size: 0.82rem;
            font-weight: 500;
        }
    </style>
</head>
<body>

    <%@ include file="navbar.jspf" %>

    <main class="main-content">
        <div class="container">

            <!-- Top Header & Action Bar -->
            <div style="display: flex; justify-content: space-between; align-items: flex-start; flex-wrap: wrap; gap: 1rem; margin-bottom: 2rem;">
                <div>
                    <div style="display: flex; align-items: center; gap: 0.75rem; margin-bottom: 0.4rem;">
                        <h1 style="font-size: 1.85rem; font-weight: 700;"><%= report.getFilename() %></h1>
                        <span class="badge badge-blue"><%= report.getFormat() %></span>
                        <% if (report.isGrayscale()) { %>
                            <span class="badge badge-amber">Grayscale</span>
                        <% } %>
                        <% if (report.isHasAlpha()) { %>
                            <span class="badge badge-purple">Alpha Channel</span>
                        <% } %>
                    </div>
                    <p style="color: var(--text-secondary); font-size: 0.9rem;">
                        Resolution: <strong><%= report.getWidth() %> × <%= report.getHeight() %> px</strong> &bull;
                        Size: <strong><%= String.format("%.2f", report.getFileSize() / (1024.0 * 1024.0)) %> MB</strong> &bull;
                        Processed in <strong><%= report.getProcessingTimeSeconds() %>s</strong>
                    </p>
                </div>

                <div style="display: flex; gap: 0.75rem;">
                    <button onclick="downloadJsonReport()" class="btn btn-secondary" style="font-size: 0.9rem;">
                        📥 Download JSON
                    </button>
                    <a href="${pageContext.request.contextPath}/upload.jsp" class="btn btn-primary" style="font-size: 0.9rem;">
                        ➕ Analyze Another
                    </a>
                </div>
            </div>

            <!-- Image & Style Tags Section -->
            <div class="grid-2" style="margin-bottom: 2rem; align-items: stretch;">
                <!-- Image Preview Card -->
                <div class="card" style="display: flex; flex-direction: column; justify-content: center; align-items: center; overflow: hidden;">
                    <% if (report.getImageDataUrl() != null && !report.getImageDataUrl().isBlank()) { %>
                        <img src="<%= report.getImageDataUrl() %>" alt="<%= report.getFilename() %>" 
                             style="max-width: 100%; max-height: 380px; object-fit: contain; border-radius: var(--radius-sm); border: 1px solid var(--bg-card-border);">
                    <% } else { %>
                        <div style="height: 250px; display: flex; align-items: center; justify-content: center; color: var(--text-muted);">
                            <span>No image preview stored</span>
                        </div>
                    <% } %>
                </div>

                <!-- Visual Characteristics Overview -->
                <div class="card" style="display: flex; flex-direction: column; justify-content: space-between;">
                    <div>
                        <h3 class="card-title">Photo Characteristics</h3>
                        <div style="display: flex; flex-wrap: wrap; gap: 0.5rem; margin-bottom: 1.25rem;">
                            <% 
                                List<String> labels = report.getStyleLabels();
                                if (labels != null && !labels.isEmpty()) {
                                    for (String label : labels) {
                            %>
                                <span class="badge badge-indigo"><%= label %></span>
                            <% 
                                    }
                                } 
                            %>
                            <span class="badge badge-purple"><%= report.getKeyStyle() %></span>
                            <span class="badge badge-green"><%= report.getHarmonyType() %></span>
                        </div>

                        <!-- Key Highlights Summary -->
                        <div style="display: flex; flex-direction: column; gap: 0.85rem; font-size: 0.9rem;">
                            <div style="display: flex; justify-content: space-between; border-bottom: 1px solid var(--bg-card-border); padding-bottom: 0.5rem;">
                                <span style="color: var(--text-secondary);">Lighting Key Style:</span>
                                <span style="font-weight: 600;"><%= report.getKeyStyle() %></span>
                            </div>
                            <div style="display: flex; justify-content: space-between; border-bottom: 1px solid var(--bg-card-border); padding-bottom: 0.5rem;">
                                <span style="color: var(--text-secondary);">Dynamic Range:</span>
                                <span style="font-weight: 600;"><%= String.format("%.1f", report.getEvStops()) %> EV Stops (<%= report.getDynamicRangeLabel() %>)</span>
                            </div>
                            <div style="display: flex; justify-content: space-between; border-bottom: 1px solid var(--bg-card-border); padding-bottom: 0.5rem;">
                                <span style="color: var(--text-secondary);">Focus Sharpness:</span>
                                <span style="font-weight: 600;"><%= String.format("%.1f", report.getSharpnessScore()) %>/100 (<%= report.getSharpnessLabel() %>)</span>
                            </div>
                            <div style="display: flex; justify-content: space-between; border-bottom: 1px solid var(--bg-card-border); padding-bottom: 0.5rem;">
                                <span style="color: var(--text-secondary);">Color Temperature:</span>
                                <span style="font-weight: 600;"><%= report.getTemperatureLabel() %> (<%= report.getWarmthIndex() >= 0 ? "+" : "" %><%= String.format("%.1f", report.getWarmthIndex()) %>)</span>
                            </div>
                            <div style="display: flex; justify-content: space-between;">
                                <span style="color: var(--text-secondary);">Color Harmony:</span>
                                <span style="font-weight: 600;"><%= report.getHarmonyType() %></span>
                            </div>
                        </div>
                    </div>

                    <div style="margin-top: 1.25rem; padding: 0.75rem 1rem; background: rgba(11, 15, 25, 0.5); border-radius: 8px; border: 1px solid var(--bg-card-border); font-size: 0.82rem; color: var(--text-secondary);">
                        💡 <em><%= report.getKeyDescription() %></em>
                    </div>
                </div>
            </div>

            <!-- Core Exposure & Color Metrics (4 Cards) -->
            <div class="grid-4" style="margin-bottom: 2rem;">
                <!-- Brightness Card -->
                <div class="card">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                        <span style="color: var(--text-secondary); font-size: 0.85rem; font-weight: 600;">BRIGHTNESS</span>
                        <span class="badge badge-blue"><%= report.getBrightnessLabel() %></span>
                    </div>
                    <div style="font-size: 1.75rem; font-weight: 700; margin-bottom: 0.25rem;">
                        <%= String.format("%.1f", report.getNormalizedBrightness()) %>%
                    </div>
                    <div style="color: var(--text-muted); font-size: 0.8rem; margin-bottom: 0.5rem;">
                        Mean Luminance: <%= String.format("%.1f", report.getMeanBrightness()) %> / 255
                    </div>
                    <div class="progress-bar-bg">
                        <div class="progress-bar-fill" style="width: <%= Math.min(100.0, Math.max(0.0, report.getNormalizedBrightness())) %>%; background: #38bdf8;"></div>
                    </div>
                </div>

                <!-- Contrast Card -->
                <div class="card">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                        <span style="color: var(--text-secondary); font-size: 0.85rem; font-weight: 600;">CONTRAST</span>
                        <span class="badge badge-indigo"><%= report.getContrastLabel() %></span>
                    </div>
                    <div style="font-size: 1.75rem; font-weight: 700; margin-bottom: 0.25rem;">
                        <%= String.format("%.1f", report.getNormalizedContrast()) %>%
                    </div>
                    <div style="color: var(--text-muted); font-size: 0.8rem; margin-bottom: 0.5rem;">
                        Std Dev (&sigma;): <%= String.format("%.1f", report.getContrastStdDev()) %>
                    </div>
                    <div class="progress-bar-bg">
                        <div class="progress-bar-fill" style="width: <%= Math.min(100.0, Math.max(0.0, report.getNormalizedContrast())) %>%; background: #818cf8;"></div>
                    </div>
                </div>

                <!-- Saturation Card -->
                <div class="card">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                        <span style="color: var(--text-secondary); font-size: 0.85rem; font-weight: 600;">SATURATION</span>
                        <span class="badge badge-purple"><%= report.getSaturationLabel() %></span>
                    </div>
                    <div style="font-size: 1.75rem; font-weight: 700; margin-bottom: 0.25rem;">
                        <%= String.format("%.1f", report.getNormalizedSaturation()) %>%
                    </div>
                    <div style="color: var(--text-muted); font-size: 0.8rem; margin-bottom: 0.5rem;">
                        Low: <%= String.format("%.0f", report.getSaturationLowPct()) %>% | High: <%= String.format("%.0f", report.getSaturationHighPct()) %>%
                    </div>
                    <div class="progress-bar-bg">
                        <div class="progress-bar-fill" style="width: <%= Math.min(100.0, Math.max(0.0, report.getNormalizedSaturation())) %>%; background: #c084fc;"></div>
                    </div>
                </div>

                <!-- Temperature Card -->
                <div class="card">
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                        <span style="color: var(--text-secondary); font-size: 0.85rem; font-weight: 600;">TEMPERATURE</span>
                        <span class="badge <%= report.getWarmthIndex() > 5 ? "badge-amber" : (report.getWarmthIndex() < -5 ? "badge-blue" : "badge-green") %>">
                            <%= report.getTemperatureLabel() %>
                        </span>
                    </div>
                    <div style="font-size: 1.75rem; font-weight: 700; margin-bottom: 0.25rem;">
                        <%= report.getWarmthIndex() >= 0 ? "+" : "" %><%= String.format("%.1f", report.getWarmthIndex()) %>
                    </div>
                    <div style="color: var(--text-muted); font-size: 0.8rem; margin-bottom: 0.5rem;">
                        RGB: <span style="color: #f87171;"><%= (int)report.getMeanR() %></span>,
                             <span style="color: #4ade80;"><%= (int)report.getMeanG() %></span>,
                             <span style="color: #60a5fa;"><%= (int)report.getMeanB() %></span>
                    </div>
                    <div class="progress-bar-bg">
                        <div class="progress-bar-fill" style="width: <%= Math.min(100.0, Math.max(0.0, 50.0 + report.getWarmthIndex())) %>%; background: <%= report.getWarmthIndex() >= 0 ? "#f59e0b" : "#38bdf8" %>;"></div>
                    </div>
                </div>
            </div>

            <!-- Quality & Lighting Analytics (2 Cards) -->
            <div class="grid-2" style="margin-bottom: 2rem;">
                <!-- Quality Card -->
                <div class="card">
                    <h3 class="card-title">
                        <span>🔍 Image Quality & Sharpness</span>
                        <span class="badge badge-blue"><%= report.getSharpnessLabel() %></span>
                    </h3>
                    <div class="grid-3" style="margin-top: 1rem;">
                        <div style="background: rgba(11, 15, 25, 0.5); padding: 1rem; border-radius: 8px;">
                            <div style="color: var(--text-secondary); font-size: 0.8rem;">FOCUS / SHARPNESS</div>
                            <div style="font-size: 1.4rem; font-weight: 700; margin: 0.25rem 0;">
                                <%= String.format("%.1f", report.getSharpnessScore()) %><span style="font-size: 0.9rem; color: var(--text-muted);">/100</span>
                            </div>
                            <div style="font-size: 0.75rem; color: var(--text-muted);">
                                Var: <%= String.format("%.0f", report.getSharpnessVariance()) %>
                            </div>
                        </div>

                        <div style="background: rgba(11, 15, 25, 0.5); padding: 1rem; border-radius: 8px;">
                            <div style="color: var(--text-secondary); font-size: 0.8rem;">NOISE / GRAIN</div>
                            <div style="font-size: 1.4rem; font-weight: 700; margin: 0.25rem 0;">
                                <%= String.format("%.1f", report.getNoiseScore()) %><span style="font-size: 0.9rem; color: var(--text-muted);">/100</span>
                            </div>
                            <div style="font-size: 0.75rem; color: var(--text-muted);">
                                <%= report.getNoiseLabel() %> (&sigma; <%= String.format("%.1f", report.getNoiseSigma()) %>)
                            </div>
                        </div>

                        <div style="background: rgba(11, 15, 25, 0.5); padding: 1rem; border-radius: 8px;">
                            <div style="color: var(--text-secondary); font-size: 0.8rem;">CLARITY INDEX</div>
                            <div style="font-size: 1.4rem; font-weight: 700; margin: 0.25rem 0;">
                                <%= String.format("%.1f", report.getClarityScore()) %><span style="font-size: 0.9rem; color: var(--text-muted);">/100</span>
                            </div>
                            <div style="font-size: 0.75rem; color: var(--text-muted);">
                                <%= report.getClarityLabel() %>
                            </div>
                        </div>
                    </div>
                </div>

                <!-- Lighting & Vignette Card -->
                <div class="card">
                    <h3 class="card-title">
                        <span>💡 Lighting & Dynamic Range</span>
                        <span class="badge badge-amber"><%= report.getKeyStyle() %></span>
                    </h3>
                    <div class="grid-3" style="margin-top: 1rem;">
                        <div style="background: rgba(11, 15, 25, 0.5); padding: 1rem; border-radius: 8px;">
                            <div style="color: var(--text-secondary); font-size: 0.8rem;">DYNAMIC RANGE</div>
                            <div style="font-size: 1.4rem; font-weight: 700; margin: 0.25rem 0;">
                                <%= String.format("%.1f", report.getEvStops()) %><span style="font-size: 0.9rem; color: var(--text-muted);"> EV</span>
                            </div>
                            <div style="font-size: 0.75rem; color: var(--text-muted);">
                                Span: <%= String.format("%.0f", report.getTonalSpan()) %>
                            </div>
                        </div>

                        <div style="background: rgba(11, 15, 25, 0.5); padding: 1rem; border-radius: 8px;">
                            <div style="color: var(--text-secondary); font-size: 0.8rem;">LENS VIGNETTE</div>
                            <div style="font-size: 1.4rem; font-weight: 700; margin: 0.25rem 0;">
                                <%= String.format("%.1f", report.getVignetteFalloffPct()) %>%
                            </div>
                            <div style="font-size: 0.75rem; color: var(--text-muted);">
                                <%= report.getVignetteLabel() %>
                            </div>
                        </div>

                        <div style="background: rgba(11, 15, 25, 0.5); padding: 1rem; border-radius: 8px;">
                            <div style="color: var(--text-secondary); font-size: 0.8rem;">CENTER / CORNER</div>
                            <div style="font-size: 1.4rem; font-weight: 700; margin: 0.25rem 0;">
                                <%= (int)report.getCenterLuminance() %> / <%= (int)report.getCornerLuminance() %>
                            </div>
                            <div style="font-size: 0.75rem; color: var(--text-muted);">
                                Relative falloff
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <!-- 256-Bin Histogram Canvas Section -->
            <div class="card" style="margin-bottom: 2rem;">
                <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem; margin-bottom: 1rem;">
                    <div>
                        <h3 class="card-title" style="margin-bottom: 0.25rem;">RGB & Luminance Histograms</h3>
                        <p style="color: var(--text-secondary); font-size: 0.85rem;">
                            Direct 256-bin pixel distribution across red, green, blue channels and BT.709 luminance.
                        </p>
                    </div>

                    <!-- Channel Toggles -->
                    <div style="display: flex; gap: 0.4rem;">
                        <button type="button" class="channel-btn active" onclick="setChannel('all', this)">RGB Overlay</button>
                        <button type="button" class="channel-btn" onclick="setChannel('red', this)">Red</button>
                        <button type="button" class="channel-btn" onclick="setChannel('green', this)">Green</button>
                        <button type="button" class="channel-btn" onclick="setChannel('blue', this)">Blue</button>
                        <button type="button" class="channel-btn" onclick="setChannel('lum', this)">Luminance</button>
                    </div>
                </div>

                <!-- Canvas Display -->
                <div class="histogram-container">
                    <canvas id="histogramCanvas" width="900" height="240"></canvas>
                </div>

                <div class="histogram-legend">
                    <div class="legend-item"><span class="legend-dot" style="background: #ef4444;"></span><span>Red</span></div>
                    <div class="legend-item"><span class="legend-dot" style="background: #22c55e;"></span><span>Green</span></div>
                    <div class="legend-item"><span class="legend-dot" style="background: #3b82f6;"></span><span>Blue</span></div>
                    <div class="legend-item"><span class="legend-dot" style="background: #e2e8f0;"></span><span>Luminance</span></div>
                </div>

                <!-- Shadows & Highlights Tonal Distribution Bar -->
                <div style="margin-top: 1.5rem; border-top: 1px solid var(--bg-card-border); padding-top: 1.25rem;">
                    <div style="display: flex; justify-content: space-between; font-size: 0.85rem; margin-bottom: 0.5rem;">
                        <span><strong>Tonal Distribution:</strong> Deep Shadows (<%= String.format("%.1f", report.getDeepShadowsPct()) %>%) &bull; Midtones (<%= String.format("%.1f", report.getMidtonesPct()) %>%) &bull; Highlights (<%= String.format("%.1f", report.getHighlightsPct()) %>%)</span>
                        <div style="display: flex; gap: 0.75rem;">
                            <% if (report.getBlackClippingPct() > 0.5) { %>
                                <span class="clipping-pill" style="background: rgba(244, 63, 94, 0.15); color: #fb7185;">
                                    ⚠️ Black Clipping: <%= String.format("%.2f", report.getBlackClippingPct()) %>%
                                </span>
                            <% } else { %>
                                <span class="clipping-pill" style="background: rgba(16, 185, 129, 0.15); color: #34d399;">
                                    ✓ No Black Clipping
                                </span>
                            <% } %>

                            <% if (report.getWhiteClippingPct() > 0.5) { %>
                                <span class="clipping-pill" style="background: rgba(244, 63, 94, 0.15); color: #fb7185;">
                                    ⚠️ White Clipping: <%= String.format("%.2f", report.getWhiteClippingPct()) %>%
                                </span>
                            <% } else { %>
                                <span class="clipping-pill" style="background: rgba(16, 185, 129, 0.15); color: #34d399;">
                                    ✓ No White Clipping
                                </span>
                            <% } %>
                        </div>
                    </div>

                    <!-- Multi-segment tonal bar -->
                    <div style="display: flex; height: 10px; border-radius: 5px; overflow: hidden; background: #000;">
                        <div style="width: <%= report.getDeepShadowsPct() %>%; background: #1e1b4b;" title="Deep Shadows: <%= report.getDeepShadowsPct() %>%"></div>
                        <div style="width: <%= report.getShadowsPct() %>%; background: #3730a3;" title="Shadows: <%= report.getShadowsPct() %>%"></div>
                        <div style="width: <%= report.getMidtonesPct() %>%; background: #4f46e5;" title="Midtones: <%= report.getMidtonesPct() %>%"></div>
                        <div style="width: <%= report.getHighlightsPct() %>%; background: #818cf8;" title="Highlights: <%= report.getHighlightsPct() %>%"></div>
                        <div style="width: <%= report.getBrightHighlightsPct() %>%; background: #c7d2fe;" title="Bright Highlights: <%= report.getBrightHighlightsPct() %>%"></div>
                    </div>
                </div>
            </div>

            <!-- Dominant Colors & Color Harmony -->
            <div class="card" style="margin-bottom: 2rem;">
                <div style="display: flex; justify-content: space-between; align-items: center; flex-wrap: wrap; gap: 0.75rem; margin-bottom: 0.5rem;">
                    <div>
                        <h3 class="card-title" style="margin-bottom: 0.25rem;">Dominant Color Palette (K-Means Clustered)</h3>
                        <p style="color: var(--text-secondary); font-size: 0.85rem;">
                            Identified using OpenCV K-Means pixel clustering in CIELAB color space.
                        </p>
                    </div>
                    <div style="text-align: right;">
                        <span class="badge badge-green" style="font-size: 0.85rem;"><%= report.getHarmonyType() %></span>
                        <div style="color: var(--text-muted); font-size: 0.75rem; margin-top: 0.2rem;"><%= report.getHarmonyDescription() %></div>
                    </div>
                </div>

                <div class="color-swatch-grid">
                    <% 
                        List<DominantColor> domColors = report.getDominantColors();
                        if (domColors != null) {
                            for (DominantColor dc : domColors) {
                    %>
                        <div class="color-swatch-card">
                            <div class="swatch-box" style="background-color: <%= dc.getHex() %>;"></div>
                            <div class="swatch-details">
                                <div class="swatch-hex" onclick="copyHex('<%= dc.getHex() %>')" style="cursor: pointer;" title="Click to copy HEX">
                                    <%= dc.getHex() %>
                                </div>
                                <div class="swatch-pct">
                                    <%= String.format("%.1f", dc.getPercentage()) %>% &bull; RGB(<%= dc.getR() %>,<%= dc.getG() %>,<%= dc.getB() %>)
                                </div>
                                <div class="progress-bar-bg" style="height: 4px; margin-top: 0.35rem;">
                                    <div class="progress-bar-fill" style="width: <%= dc.getPercentage() %>%; background-color: <%= dc.getHex() %>;"></div>
                                </div>
                            </div>
                        </div>
                    <% 
                            }
                        }
                    %>
                </div>
            </div>

            <!-- Rule-Based Editing Recommendations -->
            <div class="card" style="margin-bottom: 2rem;">
                <h3 class="card-title" style="margin-bottom: 0.5rem;">
                    <span>💡 Lightroom & Color Grading Recommendations</span>
                    <span class="badge badge-purple"><%= report.getRecommendations().size() %> Suggestions</span>
                </h3>
                <p style="color: var(--text-secondary); font-size: 0.85rem; margin-bottom: 1.25rem;">
                    Objective, rule-based editing tips based on measured histogram characteristics, exposure dynamic range, and color balance.
                </p>

                <% 
                    List<Recommendation> recs = report.getRecommendations();
                    if (recs != null && !recs.isEmpty()) {
                        for (Recommendation rec : recs) {
                            String pClass = "priority-medium";
                            String badgeClass = "badge-amber";
                            if ("high".equalsIgnoreCase(rec.getPriority())) {
                                pClass = "priority-high";
                                badgeClass = "badge-rose";
                            } else if ("low".equalsIgnoreCase(rec.getPriority())) {
                                pClass = "priority-low";
                                badgeClass = "badge-green";
                            }
                %>
                    <div class="rec-item <%= pClass %>">
                        <div class="rec-header">
                            <span class="rec-suggestion"><%= rec.getSuggestion() %></span>
                            <span class="badge <%= badgeClass %>"><%= rec.getPriority().toUpperCase() %> PRIORITY</span>
                        </div>
                        <div class="rec-reason">
                            <strong style="color: var(--text-secondary);"><%= rec.getCategory() %>:</strong> <%= rec.getReason() %>
                        </div>
                    </div>
                <% 
                        }
                    } else {
                %>
                    <div style="text-align: center; color: var(--text-muted); padding: 2rem;">
                        No adjustments recommended. The photograph shows well-balanced exposure, contrast, and color fidelity.
                    </div>
                <% } %>
            </div>

        </div>
    </main>

    <%@ include file="footer.jspf" %>

    <script>
        // Raw report data for histograms and download
        const redHist = <%= JsonUtil.toJson(report.getRedHistogram()) %>;
        const greenHist = <%= JsonUtil.toJson(report.getGreenHistogram()) %>;
        const blueHist = <%= JsonUtil.toJson(report.getBlueHistogram()) %>;
        const lumHist = <%= JsonUtil.toJson(report.getLuminanceHistogram()) %>;

        let activeChannel = 'all';

        function setChannel(channel, btn) {
            activeChannel = channel;
            document.querySelectorAll('.channel-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            drawHistogram();
        }

        function drawHistogram() {
            const canvas = document.getElementById('histogramCanvas');
            if (!canvas) return;
            const ctx = canvas.getContext('2d');
            const w = canvas.width;
            const h = canvas.height;

            ctx.clearRect(0, 0, w, h);

            // Find maximum count among selected channels
            let maxVal = 1;
            for (let i = 0; i < 256; i++) {
                if (activeChannel === 'all' || activeChannel === 'red') maxVal = Math.max(maxVal, redHist[i] || 0);
                if (activeChannel === 'all' || activeChannel === 'green') maxVal = Math.max(maxVal, greenHist[i] || 0);
                if (activeChannel === 'all' || activeChannel === 'blue') maxVal = Math.max(maxVal, blueHist[i] || 0);
                if (activeChannel === 'lum') maxVal = Math.max(maxVal, lumHist[i] || 0);
            }

            // Draw grid lines
            ctx.strokeStyle = 'rgba(255, 255, 255, 0.05)';
            ctx.lineWidth = 1;
            for (let i = 1; i <= 3; i++) {
                const y = (h / 4) * i;
                ctx.beginPath();
                ctx.moveTo(0, y);
                ctx.lineTo(w, y);
                ctx.stroke();
            }

            function drawCurve(data, strokeColor, fillColor) {
                ctx.strokeStyle = strokeColor;
                ctx.lineWidth = 1.5;
                ctx.beginPath();
                ctx.moveTo(0, h);

                for (let i = 0; i < 256; i++) {
                    const x = (i / 255) * w;
                    const val = data[i] || 0;
                    const y = h - ((val / maxVal) * (h - 10));
                    ctx.lineTo(x, y);
                }

                ctx.lineTo(w, h);
                ctx.closePath();
                if (fillColor) {
                    ctx.fillStyle = fillColor;
                    ctx.fill();
                }
                ctx.stroke();
            }

            if (activeChannel === 'all') {
                ctx.globalCompositeOperation = 'screen';
                drawCurve(redHist, '#ef4444', 'rgba(239, 68, 68, 0.25)');
                drawCurve(greenHist, '#22c55e', 'rgba(34, 197, 94, 0.25)');
                drawCurve(blueHist, '#3b82f6', 'rgba(59, 130, 246, 0.25)');
                ctx.globalCompositeOperation = 'source-over';
            } else if (activeChannel === 'red') {
                drawCurve(redHist, '#ef4444', 'rgba(239, 68, 68, 0.35)');
            } else if (activeChannel === 'green') {
                drawCurve(greenHist, '#22c55e', 'rgba(34, 197, 94, 0.35)');
            } else if (activeChannel === 'blue') {
                drawCurve(blueHist, '#3b82f6', 'rgba(59, 130, 246, 0.35)');
            } else if (activeChannel === 'lum') {
                drawCurve(lumHist, '#f8fafc', 'rgba(248, 250, 252, 0.35)');
            }
        }

        window.addEventListener('load', drawHistogram);
        window.addEventListener('resize', drawHistogram);

        function copyHex(hex) {
            navigator.clipboard.writeText(hex).then(() => {
                alert('Copied ' + hex + ' to clipboard!');
            });
        }

        function downloadJsonReport() {
            const reportData = <%= JsonUtil.toJson(report) %>;
            const blob = new Blob([JSON.stringify(reportData, null, 2)], { type: 'application/json' });
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'analysis_' + '<%= report.getFilename() %>'.replace(/[^a-zA-Z0-9_-]/g, '_') + '.json';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
        }
    </script>

</body>
</html>
