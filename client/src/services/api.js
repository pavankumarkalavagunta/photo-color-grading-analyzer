import axios from "axios";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "http://localhost:5000";

const api = axios.create({
  baseURL: `${API_BASE}/api`,
  timeout: 120000, // 2 min timeout for large images
});

/**
 * Upload an image for analysis
 * @param {File} file - Image file to analyze
 * @param {function} onProgress - Upload progress callback
 * @returns {Promise<object>} Analysis result
 */
export const uploadAndAnalyze = async (file, onProgress) => {
  const formData = new FormData();
  formData.append("image", file);
  
  const response = await api.post("/analysis", formData, {
    headers: { "Content-Type": "multipart/form-data" },
    onUploadProgress: (event) => {
      if (onProgress && event.total) {
        const pct = Math.round((event.loaded / event.total) * 100);
        onProgress(pct);
      }
    },
  });
  
  return response.data;
};

/**
 * Get analysis history
 */
export const getHistory = async (page = 1, limit = 20) => {
  const response = await api.get(`/analysis?page=${page}&limit=${limit}`);
  return response.data;
};

/**
 * Get a single analysis by ID
 */
export const getAnalysisById = async (id) => {
  const response = await api.get(`/analysis/${id}`);
  return response.data;
};

/**
 * Delete an analysis by ID
 */
export const deleteAnalysis = async (id) => {
  const response = await api.delete(`/analysis/${id}`);
  return response.data;
};

export default api;

