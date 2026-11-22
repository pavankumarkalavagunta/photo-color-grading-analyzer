import { Sparkles, Eye, Waves, CheckCircle2 } from "lucide-react";

export default function QualityCard({ quality }) {
  if (!quality) return null;

  const {
    sharpness_variance,
    sharpness_score = 0,
    sharpness_label = "Normal",
    noise_sigma,
    noise_score = 0,
    noise_label = "Clean",
    clarity_score = 0,
    clarity_label = "Good",
  } = quality;

  return (
    <div className="bg-gradient-to-br from-indigo-500/10 via-purple-500/5 to-gray-900/60 border border-indigo-500/20 rounded-xl p-5">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <Sparkles className="w-5 h-5 text-indigo-400" />
          <h3 className="text-sm font-medium text-gray-400 uppercase tracking-wide">
            Image Quality & Clarity
          </h3>
        </div>
        <span className="px-2.5 py-0.5 text-xs font-semibold rounded-full bg-indigo-500/20 text-indigo-300 border border-indigo-500/30">
          {clarity_label}
        </span>
      </div>

      {/* Main Score */}
      <div className="flex items-baseline gap-2 mb-4">
        <span className="text-4xl font-extrabold text-white">{clarity_score}</span>
        <span className="text-sm text-gray-500 font-medium">/ 100 Clarity Index</span>
      </div>

      <div className="space-y-4 pt-2 border-t border-gray-800/80">
        {/* Sharpness Meter */}
        <div>
          <div className="flex items-center justify-between text-xs mb-1">
            <span className="flex items-center gap-1.5 text-gray-300">
              <Eye className="w-3.5 h-3.5 text-indigo-400" />
              Focus & Sharpness: <strong className="text-white">{sharpness_label}</strong>
            </span>
            <span className="text-gray-400 font-mono">var {sharpness_variance}</span>
          </div>
          <div className="w-full bg-gray-800 rounded-full h-2 overflow-hidden">
            <div
              className="bg-gradient-to-r from-blue-500 to-indigo-500 h-2 rounded-full transition-all"
              style={{ width: `${Math.min(100, Math.max(8, sharpness_score))}%` }}
            />
          </div>
        </div>

        {/* Noise Meter */}
        <div>
          <div className="flex items-center justify-between text-xs mb-1">
            <span className="flex items-center gap-1.5 text-gray-300">
              <Waves className="w-3.5 h-3.5 text-purple-400" />
              Noise / Film Grain: <strong className="text-white">{noise_label}</strong>
            </span>
            <span className="text-gray-400 font-mono">σ {noise_sigma}</span>
          </div>
          <div className="w-full bg-gray-800 rounded-full h-2 overflow-hidden">
            <div
              className="bg-gradient-to-r from-purple-500 to-pink-500 h-2 rounded-full transition-all"
              style={{ width: `${Math.min(100, Math.max(8, noise_score))}%` }}
            />
          </div>
        </div>
      </div>
    </div>
  );
}

