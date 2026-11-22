import { useState, useEffect } from "react";
import { useParams, useLocation, Link } from "react-router-dom";
import { getAnalysisById } from "../services/api";
import MetricCard from "../components/MetricCard";
import QualityCard from "../components/QualityCard";
import LightingCard from "../components/LightingCard";
import RgbHistogram from "../components/RgbHistogram";
import LuminanceHistogram from "../components/LuminanceHistogram";
import DominantColors from "../components/DominantColors";
import ShadowHighlightBar from "../components/ShadowHighlightBar";
import Recommendations from "../components/Recommendations";
import {
  Sun, Contrast, Droplets, Thermometer,
  ArrowLeft, Download, Loader2, AlertCircle, Upload,
} from "lucide-react";

export default function ReportPage() {
  const { id } = useParams();
  const location = useLocation();
  const [analysis, setAnalysis] = useState(location.state?.analysis || null);
  const localPreview = location.state?.localPreview;
  const [loading, setLoading] = useState(!analysis);
  const [error, setError] = useState(null);
  
  useEffect(() => {
    if (!analysis && id) {
      setLoading(true);
      getAnalysisById(id)
        .then((res) => setAnalysis(res.data))
        .catch((err) => setError(err.response?.data?.error || "Failed to load report"))
        .finally(() => setLoading(false));
    }
  }, [id, analysis]);
  
  const downloadReport = () => {
    if (!analysis) return;
    const blob = new Blob([JSON.stringify(analysis, null, 2)], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const a = document.createElement("a");
    a.href = url;
    a.download = `analysis-${analysis.filename || "report"}.json`;
    a.click();
    URL.revokeObjectURL(url);
  };
  
  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <Loader2 className="w-8 h-8 animate-spin text-violet-400" />
      </div>
    );
  }
  
  if (error || !analysis) {
    return (
      <div className="max-w-md mx-auto px-4 py-20 text-center">
        <AlertCircle className="w-12 h-12 text-red-400 mx-auto mb-4" />
        <h2 className="text-xl font-bold text-gray-200 mb-2">Failed to Load Report</h2>
        <p className="text-gray-500 mb-6">{error || "Report not found"}</p>
        <Link to="/upload" className="text-violet-400 hover:text-violet-300">← Back to Upload</Link>
      </div>
    );
  }
  
  const { brightness, contrast, saturation, color_temperature, metadata } = analysis;
  
  return (
    <div className="max-w-7xl mx-auto px-4 py-8">
      {/* Header */}
      <div className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4 mb-8">
        <div>
          <Link to="/upload" className="inline-flex items-center gap-1 text-sm text-gray-500 hover:text-gray-300 mb-2">
            <ArrowLeft className="w-4 h-4" /> Back to Upload
          </Link>
          <h1 className="text-2xl font-bold text-gray-100">Analysis Report</h1>
          <p className="text-sm text-gray-500 mt-1">
            {metadata?.filename || analysis.filename} • {metadata?.width}×{metadata?.height} • {metadata?.format}
            {analysis.processing_time_seconds ? ` • Processed in ${analysis.processing_time_seconds}s` : ""}
          </p>
        </div>
        <div className="flex gap-3">
          <button onClick={downloadReport} className="flex items-center gap-2 bg-gray-800 hover:bg-gray-700 text-gray-300 px-4 py-2 rounded-lg text-sm cursor-pointer transition-colors">
            <Download className="w-4 h-4" /> Download JSON
          </button>
          <Link to="/upload" className="flex items-center gap-2 bg-violet-600 hover:bg-violet-500 text-white px-4 py-2 rounded-lg text-sm cursor-pointer transition-colors">
            <Upload className="w-4 h-4" /> New Analysis
          </Link>
        </div>
      </div>

      {/* Image Preview if available */}
      {localPreview && (
        <div className="mb-6 rounded-xl overflow-hidden border border-gray-800 bg-gray-900/40 p-2 flex justify-center">
          <img src={localPreview} alt="Uploaded" className="max-h-72 rounded-lg object-contain" />
        </div>
      )}
      
      {/* Top Metric Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-6">
        <MetricCard
          title="Brightness"
          value={brightness?.normalized}
          unit="/ 100"
          label={brightness?.label}
          icon={Sun}
          color="amber"
        />
        <MetricCard
          title="Contrast"
          value={contrast?.normalized}
          unit="/ 100"
          label={contrast?.label}
          icon={Contrast}
          color="blue"
        />
        <MetricCard
          title="Saturation"
          value={saturation?.normalized}
          unit="/ 100"
          label={saturation?.label}
          icon={Droplets}
          color="emerald"
        />
        <MetricCard
          title="Color Temperature"
          value={color_temperature?.warmth_index}
          label={color_temperature?.label}
          icon={Thermometer}
          color={color_temperature?.warmth_index > 0 ? "rose" : "cyan"}
        />
      </div>

      {/* Advanced Quality & Lighting Analyzers */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 mb-6">
        <QualityCard quality={analysis.quality} />
        <LightingCard lighting={analysis.lighting} />
      </div>
      
      {/* Histograms */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 mb-6">
        <RgbHistogram data={analysis.rgb_histogram} />
        <LuminanceHistogram data={analysis.luminance_histogram} />
      </div>
      
      {/* Colors & Shadows */}
      <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 mb-6">
        <DominantColors colors={analysis.dominant_colors} harmony={analysis.color_harmony} />
        <ShadowHighlightBar data={analysis.shadows_highlights} />
      </div>
      
      {/* Recommendations */}
      <Recommendations
        recommendations={analysis.recommendations}
        styleLabels={analysis.style_labels}
      />
    </div>
  );
}

