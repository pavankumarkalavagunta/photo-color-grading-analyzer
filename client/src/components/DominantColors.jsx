import { Palette } from "lucide-react";

export default function DominantColors({ colors, harmony }) {
  if (!colors || colors.length === 0) return null;
  
  return (
    <div className="bg-gray-900/50 border border-gray-800 rounded-xl p-5">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-sm font-medium text-gray-400 uppercase tracking-wide">
          Top 5 Dominant Colors
        </h3>
        {harmony && (
          <span className="px-2.5 py-0.5 text-xs font-semibold rounded-full bg-violet-500/20 text-violet-300 border border-violet-500/30 flex items-center gap-1">
            <Palette className="w-3 h-3" />
            {harmony.harmony_type} Harmony
          </span>
        )}
      </div>

      {harmony?.description && (
        <p className="text-xs text-gray-400 mb-4 bg-gray-950/50 p-2.5 rounded-lg border border-gray-800">
          {harmony.description}
        </p>
      )}

      <div className="space-y-3">
        {colors.map((color, idx) => (
          <div key={idx} className="flex items-center gap-3">
            <div
              className="w-10 h-10 rounded-lg border border-gray-700 shrink-0 shadow-sm"
              style={{ backgroundColor: color.hex }}
            />
            <div className="flex-1 min-w-0">
              <div className="flex items-center justify-between">
                <span className="text-sm font-mono text-gray-300 uppercase">{color.hex}</span>
                <span className="text-sm text-gray-400 font-medium">{color.percentage}%</span>
              </div>
              <div className="mt-1 w-full bg-gray-800 rounded-full h-1.5 overflow-hidden">
                <div
                  className="h-1.5 rounded-full"
                  style={{ width: `${Math.max(color.percentage, 2)}%`, backgroundColor: color.hex }}
                />
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
