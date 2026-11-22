import { useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import { useDropzone } from "react-dropzone";
import { Upload, Image as ImageIcon, X, Loader2, AlertCircle, BarChart3 } from "lucide-react";
import { uploadAndAnalyze } from "../services/api";

const ACCEPTED_TYPES = { "image/jpeg": [".jpg", ".jpeg"], "image/png": [".png"], "image/webp": [".webp"] };
const MAX_SIZE = 20 * 1024 * 1024;

function formatFileSize(bytes) {
  if (bytes < 1024) return bytes + " B";
  if (bytes < 1048576) return (bytes / 1024).toFixed(1) + " KB";
  return (bytes / 1048576).toFixed(1) + " MB";
}

export default function UploadPage() {
  const navigate = useNavigate();
  const [file, setFile] = useState(null);
  const [preview, setPreview] = useState(null);
  const [dimensions, setDimensions] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [progress, setProgress] = useState(0);
  const [error, setError] = useState(null);
  const [stage, setStage] = useState(""); // "uploading" | "analyzing"
  
  const onDrop = useCallback((acceptedFiles, rejectedFiles) => {
    setError(null);
    if (rejectedFiles && rejectedFiles.length > 0) {
      const err = rejectedFiles[0].errors[0];
      setError(err.code === "file-too-large" ? "File is too large (max 20MB)" : err.message);
      return;
    }
    if (acceptedFiles && acceptedFiles.length > 0) {
      const f = acceptedFiles[0];
      setFile(f);
      const url = URL.createObjectURL(f);
      setPreview(url);
      
      const img = new window.Image();
      img.onload = () => setDimensions({ width: img.width, height: img.height });
      img.src = url;
    }
  }, []);
  
  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: ACCEPTED_TYPES,
    maxSize: MAX_SIZE,
    multiple: false,
  });
  
  const clearFile = () => {
    setFile(null);
    if (preview) URL.revokeObjectURL(preview);
    setPreview(null);
    setDimensions(null);
    setError(null);
  };
  
  const handleAnalyze = async () => {
    if (!file) return;
    setUploading(true);
    setError(null);
    setProgress(0);
    setStage("uploading");
    
    try {
      const result = await uploadAndAnalyze(file, (pct) => {
        setProgress(pct);
        if (pct >= 100) setStage("analyzing");
      });
      
      navigate(`/report/${result.data._id}`, { state: { analysis: result.data, localPreview: preview } });
    } catch (err) {
      const msg = err.response?.data?.error || err.message || "Analysis failed. Please try again.";
      setError(msg);
    } finally {
      setUploading(false);
      setStage("");
    }
  };
  
  return (
    <div className="max-w-3xl mx-auto px-4 py-12">
      <div className="text-center mb-8">
        <h1 className="text-3xl font-bold text-gray-100 mb-2">Upload Your Photo</h1>
        <p className="text-gray-500">Select a JPEG, PNG, or WebP image to analyze its color grading</p>
      </div>
      
      {/* Error Banner */}
      {error && (
        <div className="mb-6 flex items-center gap-3 bg-red-500/10 border border-red-500/30 rounded-xl p-4">
          <AlertCircle className="w-5 h-5 text-red-400 shrink-0" />
          <p className="text-sm text-red-300">{error}</p>
          <button onClick={() => setError(null)} className="ml-auto text-red-400 hover:text-red-300 cursor-pointer">
            <X className="w-4 h-4" />
          </button>
        </div>
      )}
      
      {!file ? (
        /* Drop Zone */
        <div
          {...getRootProps()}
          className={`border-2 border-dashed rounded-2xl p-12 text-center cursor-pointer transition-colors ${
            isDragActive
              ? "border-violet-400 bg-violet-500/10"
              : "border-gray-700 hover:border-gray-600 bg-gray-900/50"
          }`}
        >
          <input {...getInputProps()} />
          <Upload className={`w-12 h-12 mx-auto mb-4 ${isDragActive ? "text-violet-400" : "text-gray-600"}`} />
          {isDragActive ? (
            <p className="text-violet-300 font-medium">Drop your image here...</p>
          ) : (
            <>
              <p className="text-gray-300 font-medium mb-1">Drag & drop your image here</p>
              <p className="text-sm text-gray-500 mb-4">or click to browse files</p>
              <p className="text-xs text-gray-600">JPEG, PNG, WebP • Max 20MB</p>
            </>
          )}
        </div>
      ) : (
        /* Preview */
        <div className="bg-gray-900/50 border border-gray-800 rounded-2xl overflow-hidden">
          <div className="relative">
            <img src={preview} alt="Preview" className="w-full max-h-96 object-contain bg-gray-950" />
            {!uploading && (
              <button
                onClick={clearFile}
                className="absolute top-3 right-3 bg-gray-900/80 hover:bg-gray-800 p-1.5 rounded-lg cursor-pointer"
              >
                <X className="w-4 h-4 text-gray-400" />
              </button>
            )}
          </div>
          
          <div className="p-5 border-t border-gray-800">
            <div className="flex items-center gap-3 mb-4">
              <ImageIcon className="w-5 h-5 text-gray-500" />
              <div className="min-w-0 flex-1">
                <p className="text-sm font-medium text-gray-300 truncate">{file.name}</p>
                <p className="text-xs text-gray-500">
                  {formatFileSize(file.size)}
                  {dimensions && ` • ${dimensions.width} × ${dimensions.height}px`}
                </p>
              </div>
            </div>
            
            {/* Progress Bar */}
            {uploading && (
              <div className="mb-4">
                <div className="flex items-center justify-between text-xs text-gray-500 mb-1">
                  <span>{stage === "analyzing" ? "Analyzing image with OpenCV..." : "Uploading..."}</span>
                  <span>{stage === "analyzing" ? "Processing" : `${progress}%`}</span>
                </div>
                <div className="w-full bg-gray-800 rounded-full h-2 overflow-hidden">
                  <div
                    className={`h-2 rounded-full transition-all duration-300 ${stage === "analyzing" ? "bg-amber-500 animate-pulse w-full" : "bg-violet-500"}`}
                    style={stage !== "analyzing" ? { width: `${progress}%` } : {}}
                  />
                </div>
              </div>
            )}
            
            <button
              onClick={handleAnalyze}
              disabled={uploading}
              className="w-full flex items-center justify-center gap-2 bg-violet-600 hover:bg-violet-500 disabled:bg-gray-700 disabled:cursor-not-allowed text-white font-semibold py-3 rounded-xl transition-colors cursor-pointer"
            >
              {uploading ? (
                <>
                  <Loader2 className="w-5 h-5 animate-spin" />
                  {stage === "analyzing" ? "Running Color Grading Analysis..." : "Uploading..."}
                </>
              ) : (
                <>
                  <BarChart3 className="w-5 h-5" />
                  Analyze Photo
                </>
              )}
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

