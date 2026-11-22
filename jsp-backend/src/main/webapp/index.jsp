<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Photo Color Grading Analyzer - JSP & Python OpenCV</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <%@ include file="navbar.jspf" %>

    <main class="main-content">
        <div class="container">
            <!-- Hero Section -->
            <section style="text-align: center; padding: 3rem 1rem 4rem 1rem;">
                <div style="display: inline-block; margin-bottom: 1.25rem;">
                    <span class="badge badge-blue" style="font-size: 0.85rem; padding: 0.35rem 0.9rem;">
                        ⚡ Full-Stack JSP &bull; Jakarta Servlets 6 &bull; Python OpenCV
                    </span>
                </div>
                <h1 style="font-size: 2.75rem; font-weight: 800; line-height: 1.2; margin-bottom: 1.25rem;">
                    Professional Photo <span class="gradient-text">Color & Lighting</span> Analytics
                </h1>
                <p style="font-size: 1.15rem; color: var(--text-secondary); max-width: 720px; margin: 0 auto 2.5rem auto;">
                    Upload any photograph to extract scientific visual characteristics: RGB & luminance histograms,
                    5-color dominant palette, color harmony, focus sharpness, dynamic range EV stops, and Lightroom-style grading advice.
                </p>
                <div style="display: flex; gap: 1rem; justify-content: center; flex-wrap: wrap;">
                    <a href="${pageContext.request.contextPath}/upload.jsp" class="btn btn-primary" style="font-size: 1.05rem; padding: 0.85rem 2rem;">
                        🚀 Upload & Analyze Photo
                    </a>
                    <a href="${pageContext.request.contextPath}/history" class="btn btn-secondary" style="font-size: 1.05rem; padding: 0.85rem 1.75rem;">
                        📜 View Analysis History
                    </a>
                </div>
            </section>

            <!-- Feature Cards Grid -->
            <section style="margin-bottom: 4rem;">
                <h2 style="font-size: 1.5rem; font-weight: 700; margin-bottom: 1.5rem; text-align: center;">
                    Comprehensive Computer Vision Analytics
                </h2>
                <div class="grid-3">
                    <div class="card">
                        <div style="font-size: 2rem; margin-bottom: 0.75rem;">🎨</div>
                        <h3 class="card-title" style="margin-bottom: 0.5rem;">Color Grading & Tone</h3>
                        <p style="color: var(--text-secondary); font-size: 0.9rem;">
                            Precise ITU-R BT.709 relative luminance brightness, contrast standard deviation, HSV saturation distribution, and color temperature warmth index.
                        </p>
                    </div>

                    <div class="card">
                        <div style="font-size: 2rem; margin-bottom: 0.75rem;">🔍</div>
                        <h3 class="card-title" style="margin-bottom: 0.5rem;">Quality & Focus</h3>
                        <p style="color: var(--text-secondary); font-size: 0.9rem;">
                            Laplacian edge variance measuring camera focus sharpness, high-frequency residual noise sigma, and overall clarity index.
                        </p>
                    </div>

                    <div class="card">
                        <div style="font-size: 2rem; margin-bottom: 0.75rem;">💡</div>
                        <h3 class="card-title" style="margin-bottom: 0.5rem;">Lighting & Vignette</h3>
                        <p style="color: var(--text-secondary); font-size: 0.9rem;">
                            High-key / low-key style classification, tonal dynamic range in photographic EV stops, and center-to-corner lens vignette falloff percentage.
                        </p>
                    </div>

                    <div class="card">
                        <div style="font-size: 2rem; margin-bottom: 0.75rem;">📊</div>
                        <h3 class="card-title" style="margin-bottom: 0.5rem;">256-Bin Histograms</h3>
                        <p style="color: var(--text-secondary); font-size: 0.9rem;">
                            Direct channel-level RGB and Luminance histograms rendered using interactive HTML5 Canvas with clipping alert thresholds.
                        </p>
                    </div>

                    <div class="card">
                        <div style="font-size: 2rem; margin-bottom: 0.75rem;">🎯</div>
                        <h3 class="card-title" style="margin-bottom: 0.5rem;">K-Means Dominant Colors</h3>
                        <p style="color: var(--text-secondary); font-size: 0.9rem;">
                            Top 5 clustered dominant color swatches with precise HEX values, RGB percentages, and color wheel harmony classification.
                        </p>
                    </div>

                    <div class="card">
                        <div style="font-size: 2rem; margin-bottom: 0.75rem;">💡</div>
                        <h3 class="card-title" style="margin-bottom: 0.5rem;">Rule-Based Editing Advice</h3>
                        <p style="color: var(--text-secondary); font-size: 0.9rem;">
                            Actionable, explainable photographic recommendations for exposure, shadow recovery, white balance correction, and contrast adjustments.
                        </p>
                    </div>
                </div>
            </section>

            <!-- Architecture Section -->
            <section class="card" style="margin-bottom: 2rem;">
                <h3 class="card-title">Full-Stack Architecture Workflow</h3>
                <div class="grid-4" style="margin-top: 1rem;">
                    <div style="background: rgba(11, 15, 25, 0.5); padding: 1rem; border-radius: 8px;">
                        <span class="badge badge-purple" style="margin-bottom: 0.5rem;">Layer 1</span>
                        <h4 style="margin: 0.4rem 0; font-size: 0.95rem;">JSP & Client Views</h4>
                        <p style="font-size: 0.82rem; color: var(--text-secondary);">
                            Interactive drag-and-drop file preview, Canvas RGB charts, dynamic color swatches, and responsive UI.
                        </p>
                    </div>

                    <div style="background: rgba(11, 15, 25, 0.5); padding: 1rem; border-radius: 8px;">
                        <span class="badge badge-blue" style="margin-bottom: 0.5rem;">Layer 2</span>
                        <h4 style="margin: 0.4rem 0; font-size: 0.95rem;">Jakarta Servlets 6</h4>
                        <p style="font-size: 0.82rem; color: var(--text-secondary);">
                            Tomcat 10 servlet container processing multipart uploads, session dispatching, and REST API endpoints.
                        </p>
                    </div>

                    <div style="background: rgba(11, 15, 25, 0.5); padding: 1rem; border-radius: 8px;">
                        <span class="badge badge-amber" style="margin-bottom: 0.5rem;">Layer 3</span>
                        <h4 style="margin: 0.4rem 0; font-size: 0.95rem;">Python OpenCV Engine</h4>
                        <p style="font-size: 0.82rem; color: var(--text-secondary);">
                            FastAPI microservice executing OpenCV matrix operations, K-Means clustering, and Laplacian sharpness.
                        </p>
                    </div>

                    <div style="background: rgba(11, 15, 25, 0.5); padding: 1rem; border-radius: 8px;">
                        <span class="badge badge-green" style="margin-bottom: 0.5rem;">Layer 4</span>
                        <h4 style="margin: 0.4rem 0; font-size: 0.95rem;">DAO & Storage</h4>
                        <p style="font-size: 0.82rem; color: var(--text-secondary);">
                            Thread-safe AnalysisDAO managing persistent JSON reports, image thumbnail data URLs, and history lookups.
                        </p>
                    </div>
                </div>
            </section>
        </div>
    </main>

    <%@ include file="footer.jspf" %>

</body>
</html>
