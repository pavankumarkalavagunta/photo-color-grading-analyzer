import { SunMedium, Compass, Maximize2 } from "lucide-react";

export default function LightingCard({ lighting }) {
  if (!lighting) return null;

  const {
    key_style = "Balanced Key",
    key_description = "Evenly distributed lighting across shadows, midtones, and highlights.",
    tonal_span = 0,
    ev_stops = 0,
    dynamic_range_label = "Standard",
    center_luminance = 0,
    corner_luminance = 0,
    vignette_falloff_pct = 0,
    vignette_label = "Uniform",
  } = lighting;

  const isVignette = vignette_falloff_pct < -8;

  return (
    <div className="bg-gradient-to-br from-amber-500/10 via-orange-500/5 to-gray-900/60 border border-amber-500/20 rounded-xl p-5">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <SunMedium className="w-5 h-5 text-amber-400" />
          <h3 className="text-sm font-medium text-gray-400 uppercase tracking-wide">
            Lighting & Exposure Analysis
          </h3>
        </div>
        <span className="px-2.5 py-0.5 text-xs font-semibold rounded-full bg-amber-500/20 text-amber-300 border border-amber-500/30">
          {key_style}
        </span>
      </div>

      {/* Description */}
      <p className="text-xs text-gray-300 mb-4 bg-gray-950/60 p-2.5 rounded-lg border border-gray-800/80">
        {key_description}
      </p>

      <div className="grid grid-cols-2 gap-3 pt-2 border-t border-gray-800/80">
        {/* Dynamic Range */}
        <div className="bg-gray-900/70 p-3 rounded-lg border border-gray-800">
          <div className="flex items-center gap-1.5 text-xs text-gray-400 mb-1">
            <Maximize2 className="w-3.5 h-3.5 text-amber-400" />
            <span>Dynamic Range</span>
          </div>
          <div className="text-xl font-bold text-white">
            {ev_stops} <span className="text-xs font-normal text-gray-400">EV Stops</span>
          </div>
          <p className="text-[11px] text-amber-300/80 mt-0.5 truncate">{dynamic_range_label}</p>
        </div>

        {/* Lens Falloff / Vignetting */}
        <div className="bg-gray-900/70 p-3 rounded-lg border border-gray-800">
          <div className="flex items-center gap-1.5 text-xs text-gray-400 mb-1">
            <Compass className="w-3.5 h-3.5 text-orange-400" />
            <span>Vignette Falloff</span>
          </div>
          <div className="text-xl font-bold text-white">
            {vignette_falloff_pct > 0 ? `+${vignette_falloff_pct}%` : `${vignette_falloff_pct}%`}
          </div>
          <p className="text-[11px] text-orange-300/80 mt-0.5 truncate">{vignette_label}</p>
        </div>
      </div>
    </div>
  );
}

