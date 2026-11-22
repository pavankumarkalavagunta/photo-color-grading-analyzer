import { Link } from "react-router-dom";
import { Upload, BarChart3, Palette, History, Zap, Shield, Sparkles, SunMedium } from "lucide-react";

const features = [
  {
    icon: BarChart3,
    title: "Exposure & Color Metrics",
    desc: "Brightness, contrast, saturation, and temperature calculated directly from pixel data using OpenCV.",
  },
  {
    icon: Sparkles,
    title: "Image Quality & Clarity",
    desc: "Evaluates focus sharpness (Laplacian variance), ISO noise/grain (residual deviation), and clarity score.",
  },
  {
    icon: SunMedium,
    title: "Lighting & Dynamic Range",
    desc: "Identifies High-Key vs Low-Key styles, EV stops dynamic range, and corner lens vignetting falloff.",
  },
  {
    icon: Palette,
    title: "Dominant Color Harmonies",
    desc: "OpenCV K-Means extracts top 5 colors and detects Complementary, Analogous, or Triadic harmonies.",
  },
  {
    icon: Zap,
    title: "Targeted Editing Advice",
    desc: "Actionable, rule-based editing suggestions explaining why sharpening, exposure, or color tweaks are needed.",
  },
  {
    icon: History,
    title: "Cloud History & JSON Export",
    desc: "Analysis reports are saved in MongoDB with instant recall, comparisons, and 1-click JSON export.",
  },
];

export default function HomePage() {
  return (
    <div className="max-w-6xl mx-auto px-4 py-12">
      {/* Hero Section */}
      <div className="text-center mb-16">
        <div className="inline-flex items-center gap-2 bg-violet-500/10 border border-violet-500/20 rounded-full px-4 py-1.5 mb-6">
          <span className="w-2 h-2 rounded-full bg-violet-400 animate-pulse" />
          <span className="text-xs text-violet-300 font-medium">Photo Color Grading Analyzer</span>
        </div>
        
        <h1 className="text-4xl sm:text-5xl md:text-6xl font-bold mb-6">
          <span className="bg-gradient-to-r from-violet-400 via-pink-400 to-amber-400 bg-clip-text text-transparent">
            Understand Your Photos
          </span>
          <br />
          <span className="text-gray-300">Like Never Before</span>
        </h1>
        
        <p className="text-lg text-gray-400 max-w-2xl mx-auto mb-8">
          Upload any photograph and receive a comprehensive analysis of its color grading —
          brightness, contrast, saturation, RGB histograms, dominant colors, and actionable
          editing recommendations.
        </p>
        
        <Link
          to="/upload"
          className="inline-flex items-center gap-2 bg-violet-600 hover:bg-violet-500 text-white font-semibold px-8 py-3 rounded-xl text-lg transition-colors shadow-lg shadow-violet-500/25 cursor-pointer"
        >
          <Upload className="w-5 h-5" />
          Upload & Analyze
        </Link>
      </div>
      
      {/* How It Works */}
      <div className="mb-16">
        <h2 className="text-center text-2xl font-bold text-gray-200 mb-2">How It Works</h2>
        <p className="text-center text-gray-500 mb-8">Three simple steps to analyze your photos</p>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {[
            { step: "1", title: "Upload", desc: "Drag & drop or select a JPEG, PNG, or WebP image." },
            { step: "2", title: "Analyze", desc: "Our Python engine processes actual pixels with OpenCV." },
            { step: "3", title: "Review", desc: "View metrics, histograms, colors, and recommendations." },
          ].map((item) => (
            <div key={item.step} className="text-center p-6 bg-gray-900/50 border border-gray-800 rounded-xl">
              <div className="w-10 h-10 mx-auto mb-3 bg-violet-600 rounded-full flex items-center justify-center text-white font-bold">
                {item.step}
              </div>
              <h3 className="font-semibold text-gray-200 mb-1">{item.title}</h3>
              <p className="text-sm text-gray-500">{item.desc}</p>
            </div>
          ))}
        </div>
      </div>
      
      {/* Features Grid */}
      <div>
        <h2 className="text-center text-2xl font-bold text-gray-200 mb-2">Features</h2>
        <p className="text-center text-gray-500 mb-8">Everything you need to understand your image's color grading</p>
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
          {features.map((f, i) => (
            <div key={i} className="p-6 bg-gray-900/50 border border-gray-800 rounded-xl hover:border-gray-700 transition-colors">
              <f.icon className="w-8 h-8 text-violet-400 mb-3" />
              <h3 className="font-semibold text-gray-200 mb-1">{f.title}</h3>
              <p className="text-sm text-gray-500">{f.desc}</p>
            </div>
          ))}
        </div>
      </div>
      
      {/* Footer */}
      <footer className="mt-16 pt-8 border-t border-gray-800 text-center text-sm text-gray-600">
        <p>Photo Color Grading Analyzer — FSD-2 Project</p>
        <p className="mt-1">Built with React + Express + Python OpenCV + MongoDB</p>
      </footer>
    </div>
  );
}

