export default function ShadowHighlightBar({ data }) {
  if (!data) return null;
  
  const deepShadows = Number(data.deep_shadows_pct || 0);
  const shadows = Math.max(0, Number(data.shadows_pct || 0) - deepShadows);
  const midtones = Number(data.midtones_pct || 0);
  const brightHighlights = Number(data.bright_highlights_pct || 0);
  const highlights = Math.max(0, Number(data.highlights_pct || 0) - brightHighlights);
  
  const segments = [
    { label: "Deep Shadows", value: deepShadows, color: "#1e1b4b" },
    { label: "Shadows", value: shadows, color: "#312e81" },
    { label: "Midtones", value: midtones, color: "#6366f1" },
    { label: "Highlights", value: highlights, color: "#a5b4fc" },
    { label: "Bright Highlights", value: brightHighlights, color: "#e0e7ff" },
  ].filter(s => s.value > 0);
  
  return (
    <div className="bg-gray-900/50 border border-gray-800 rounded-xl p-5">
      <h3 className="text-sm font-medium text-gray-400 uppercase tracking-wide mb-4">
        Shadow & Highlight Distribution
      </h3>
      
      {/* Stacked bar */}
      <div className="flex rounded-lg overflow-hidden h-8 mb-4 border border-gray-800 bg-gray-950">
        {segments.map((seg, i) => (
          <div
            key={i}
            className="relative group h-full"
            style={{ width: `${seg.value}%`, backgroundColor: seg.color, minWidth: seg.value > 0 ? '4px' : 0 }}
          >
            <div className="absolute bottom-full left-1/2 -translate-x-1/2 mb-2 hidden group-hover:block bg-gray-850 text-xs text-white px-2 py-1 rounded shadow-lg whitespace-nowrap z-10 border border-gray-700">
              {seg.label}: {seg.value.toFixed(1)}%
            </div>
          </div>
        ))}
      </div>
      
      {/* Legend */}
      <div className="flex flex-wrap gap-3 text-xs text-gray-400">
        {segments.map((seg, i) => (
          <div key={i} className="flex items-center gap-1.5">
            <div className="w-3 h-3 rounded" style={{ backgroundColor: seg.color }} />
            <span>{seg.label} ({seg.value.toFixed(1)}%)</span>
          </div>
        ))}
      </div>
      
      {/* Clipping info */}
      <div className="mt-4 pt-3 border-t border-gray-800 flex gap-4 text-xs">
        <div className="flex items-center gap-1.5">
          <div className="w-2 h-2 rounded-full bg-red-500" />
          <span className="text-gray-400">Black clipping: <strong className="text-gray-200">{data.black_clipping_pct || 0}%</strong></span>
        </div>
        <div className="flex items-center gap-1.5">
          <div className="w-2 h-2 rounded-full bg-yellow-400" />
          <span className="text-gray-400">White clipping: <strong className="text-gray-200">{data.white_clipping_pct || 0}%</strong></span>
        </div>
      </div>
    </div>
  );
}

