<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload Photo - Photo Color Grading Analyzer</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

    <%@ include file="navbar.jspf" %>

    <main class="main-content">
        <div class="container" style="max-width: 760px;">
            <div style="text-align: center; margin-bottom: 2rem;">
                <h1 style="font-size: 2rem; font-weight: 700; margin-bottom: 0.5rem;">
                    Upload Photo for Analysis
                </h1>
                <p style="color: var(--text-secondary);">
                    Select or drag-and-drop a photograph in JPEG, PNG, or WebP format (up to 25MB).
                </p>
            </div>

            <%-- Error Notification if any --%>
            <%
                String errorMessage = (String) request.getAttribute("errorMessage");
                if (errorMessage != null && !errorMessage.isBlank()) {
            %>
                <div class="alert alert-danger">
                    <span>⚠️</span>
                    <span><strong>Error:</strong> <%= errorMessage %></span>
                </div>
            <%
                }
            %>

            <div class="card">
                <form id="uploadForm" action="${pageContext.request.contextPath}/analyze" method="post" enctype="multipart/form-data">
                    <input type="file" id="imageInput" name="image" accept="image/jpeg,image/png,image/webp" style="display: none;" required>

                    <!-- Dropzone -->
                    <div id="dropzone" class="upload-dropzone" onclick="document.getElementById('imageInput').click()">
                        <div class="upload-icon">📸</div>
                        <h3 style="font-size: 1.2rem; font-weight: 600; margin-bottom: 0.5rem;">
                            Click to browse or drag and drop your photo here
                        </h3>
                        <p style="color: var(--text-secondary); font-size: 0.9rem;">
                            Supports JPEG, PNG, and WebP (Max 25MB)
                        </p>
                    </div>

                    <!-- Selected File Preview Container (hidden by default) -->
                    <div id="previewCard" class="file-preview-card" style="display: none;">
                        <img id="previewImg" class="file-preview-img" src="" alt="Photo preview">
                        <div style="flex: 1;">
                            <h4 id="previewName" style="font-size: 1rem; font-weight: 600; margin-bottom: 0.25rem;">filename.jpg</h4>
                            <div style="display: flex; gap: 1rem; color: var(--text-secondary); font-size: 0.85rem; margin-bottom: 0.5rem;">
                                <span id="previewDimensions">Dimensions: Measuring...</span>
                                <span id="previewSize">Size: 0 KB</span>
                            </div>
                            <button type="button" class="btn btn-secondary" style="font-size: 0.8rem; padding: 0.3rem 0.75rem;" onclick="resetFileSelection()">
                                ✕ Choose Different Photo
                            </button>
                        </div>
                    </div>

                    <!-- Submit Actions -->
                    <div style="margin-top: 1.75rem; text-align: right;">
                        <button type="submit" id="submitBtn" class="btn btn-primary" style="width: 100%; font-size: 1.05rem; padding: 0.85rem;" disabled>
                            ⚡ Start Image Analysis
                        </button>
                    </div>
                </form>
            </div>

            <!-- Features Reminder -->
            <div style="display: flex; justify-content: space-around; margin-top: 2rem; color: var(--text-secondary); font-size: 0.85rem; text-align: center;">
                <div>🎯 <strong>K-Means</strong> Dominant Colors</div>
                <div>📊 <strong>256-Bin</strong> Histograms</div>
                <div>🔍 <strong>Focus & Sharpness</strong> Metrics</div>
                <div>💡 <strong>Dynamic Range</strong> EV Stops</div>
            </div>
        </div>
    </main>

    <%@ include file="footer.jspf" %>

    <script>
        const dropzone = document.getElementById('dropzone');
        const input = document.getElementById('imageInput');
        const previewCard = document.getElementById('previewCard');
        const previewImg = document.getElementById('previewImg');
        const previewName = document.getElementById('previewName');
        const previewDimensions = document.getElementById('previewDimensions');
        const previewSize = document.getElementById('previewSize');
        const submitBtn = document.getElementById('submitBtn');
        const uploadForm = document.getElementById('uploadForm');

        // Drag and drop event listeners
        ['dragenter', 'dragover'].forEach(name => {
            dropzone.addEventListener(name, (e) => {
                e.preventDefault();
                e.stopPropagation();
                dropzone.classList.add('dragover');
            });
        });

        ['dragleave', 'drop'].forEach(name => {
            dropzone.addEventListener(name, (e) => {
                e.preventDefault();
                e.stopPropagation();
                dropzone.classList.remove('dragover');
            });
        });

        dropzone.addEventListener('drop', (e) => {
            if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
                input.files = e.dataTransfer.files;
                handleFile(input.files[0]);
            }
        });

        input.addEventListener('change', () => {
            if (input.files && input.files.length > 0) {
                handleFile(input.files[0]);
            }
        });

        function handleFile(file) {
            if (!file) return;

            // Validate file type
            const validTypes = ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'];
            if (!validTypes.includes(file.type.toLowerCase())) {
                alert('Invalid file format. Please upload JPEG, PNG, or WebP.');
                resetFileSelection();
                return;
            }

            // Validate size (25MB)
            if (file.size > 25 * 1024 * 1024) {
                alert('File is too large. Maximum supported size is 25MB.');
                resetFileSelection();
                return;
            }

            previewName.textContent = file.name;
            previewSize.textContent = 'Size: ' + formatBytes(file.size);

            const reader = new FileReader();
            reader.onload = (e) => {
                previewImg.src = e.target.result;
                const tempImg = new Image();
                tempImg.onload = () => {
                    previewDimensions.textContent = 'Resolution: ' + tempImg.naturalWidth + ' × ' + tempImg.naturalHeight + ' px';
                };
                tempImg.src = e.target.result;

                previewCard.style.display = 'flex';
                dropzone.style.display = 'none';
                submitBtn.disabled = false;
            };
            reader.readAsDataURL(file);
        }

        function resetFileSelection() {
            input.value = '';
            previewCard.style.display = 'none';
            dropzone.style.display = 'block';
            submitBtn.disabled = true;
        }

        function formatBytes(bytes) {
            if (bytes === 0) return '0 Bytes';
            const k = 1024;
            const sizes = ['Bytes', 'KB', 'MB', 'GB'];
            const i = Math.floor(Math.log(bytes) / Math.log(k));
            return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
        }

        uploadForm.addEventListener('submit', () => {
            submitBtn.disabled = true;
            submitBtn.innerHTML = '⏳ Analyzing Image with OpenCV Engine...';
        });
    </script>

</body>
</html>
