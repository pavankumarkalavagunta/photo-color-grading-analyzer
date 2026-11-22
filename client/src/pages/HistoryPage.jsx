import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { getHistory, deleteAnalysis } from "../services/api";
import { Trash2, ExternalLink, Loader2, FolderOpen, AlertCircle } from "lucide-react";

export default function HistoryPage() {
  const [analyses, setAnalyses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [deleting, setDeleting] = useState(null);
  
  const fetchHistory = async () => {
    setLoading(true);
    try {
      const res = await getHistory();
      setAnalyses(res.data || []);
    } catch (err) {
      setError(err.response?.data?.error || "Failed to load history");
    } finally {
      setLoading(false);
    }
  };
  
  useEffect(() => { fetchHistory(); }, []);
  
  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this analysis report?")) return;
    setDeleting(id);
    try {
      await deleteAnalysis(id);
      setAnalyses((prev) => prev.filter((a) => a._id !== id));
    } catch {
      setError("Failed to delete analysis");
    } finally {
      setDeleting(null);
    }
  };
  
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <Loader2 className="w-8 h-8 animate-spin text-violet-400" />
      </div>
    );
  }
  
  return (
    <div className="max-w-4xl mx-auto px-4 py-12">
      <h1 className="text-3xl font-bold text-gray-100 mb-2">Analysis History</h1>
      <p className="text-gray-500 mb-8">Your previously analyzed photographs saved in MongoDB</p>
      
      {error && (
        <div className="mb-6 flex items-center gap-3 bg-red-500/10 border border-red-500/30 rounded-xl p-4">
          <AlertCircle className="w-5 h-5 text-red-400" />
          <p className="text-sm text-red-300">{error}</p>
        </div>
      )}
      
      {analyses.length === 0 ? (
        <div className="text-center py-16 bg-gray-900/50 border border-gray-800 rounded-2xl">
          <FolderOpen className="w-12 h-12 text-gray-700 mx-auto mb-4" />
          <h2 className="text-lg font-semibold text-gray-400 mb-2">No analyses saved yet</h2>
          <p className="text-sm text-gray-600 mb-6">Upload a photo to see real-time color grading analytics</p>
          <Link
            to="/upload"
            className="inline-flex items-center gap-2 bg-violet-600 hover:bg-violet-500 text-white px-6 py-2.5 rounded-xl text-sm font-medium transition-colors cursor-pointer"
          >
            Upload Photo
          </Link>
        </div>
      ) : (
        <div className="space-y-3">
          {analyses.map((a) => (
            <div
              key={a._id}
              className="flex items-center gap-4 bg-gray-900/50 border border-gray-800 rounded-xl p-4 hover:border-gray-700 transition-colors"
            >
              {/* Color dots preview */}
              <div className="flex gap-1 shrink-0">
                {(a.dominant_colors || []).slice(0, 4).map((c, i) => (
                  <div key={i} className="w-4 h-4 rounded-full border border-gray-700" style={{ backgroundColor: c.hex }} />
                ))}
              </div>
              
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-200 truncate">{a.filename}</p>
                <p className="text-xs text-gray-500">
                  {new Date(a.createdAt).toLocaleDateString()} {new Date(a.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })} • {a.brightness?.label} • {a.contrast?.label} • {a.color_temperature?.label}
                </p>
              </div>
              
              <div className="flex items-center gap-2 shrink-0">
                <Link
                  to={`/report/${a._id}`}
                  className="p-2 text-gray-400 hover:text-violet-400 hover:bg-gray-800 rounded-lg cursor-pointer transition-colors"
                  title="View Report"
                >
                  <ExternalLink className="w-4 h-4" />
                </Link>
                <button
                  onClick={() => handleDelete(a._id)}
                  disabled={deleting === a._id}
                  className="p-2 text-gray-400 hover:text-red-400 hover:bg-gray-800 rounded-lg disabled:opacity-50 cursor-pointer transition-colors"
                  title="Delete Analysis"
                >
                  {deleting === a._id ? <Loader2 className="w-4 h-4 animate-spin" /> : <Trash2 className="w-4 h-4" />}
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}

