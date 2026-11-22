import { Lightbulb, AlertTriangle, Info } from "lucide-react";

const priorityConfig = {
  high: { icon: AlertTriangle, color: "text-red-400", bg: "bg-red-500/10 border-red-500/20" },
  medium: { icon: Lightbulb, color: "text-amber-400", bg: "bg-amber-500/10 border-amber-500/20" },
  low: { icon: Info, color: "text-blue-400", bg: "bg-blue-500/10 border-blue-500/20" },
};

export default function Recommendations({ recommendations, styleLabels }) {
  if (!recommendations || recommendations.length === 0) return null;
  
  return (
    <div className="bg-gray-900/50 border border-gray-800 rounded-xl p-5">
      <h3 className="text-sm font-medium text-gray-400 uppercase tracking-wide mb-4">
        Editing Recommendations
      </h3>
      
      {/* Style Labels */}
      {styleLabels && styleLabels.length > 0 && (
        <div className="flex flex-wrap gap-2 mb-4">
          {styleLabels.map((label, i) => (
            <span key={i} className="px-3 py-1 bg-violet-500/15 border border-violet-500/30 rounded-full text-xs text-violet-300 font-medium">
              {label}
            </span>
          ))}
        </div>
      )}
      
      <div className="space-y-3">
        {recommendations.map((rec, idx) => {
          const config = priorityConfig[rec.priority] || priorityConfig.low;
          const Icon = config.icon;
          
          return (
            <div key={idx} className={`border rounded-lg p-4 ${config.bg}`}>
              <div className="flex items-start gap-3">
                <Icon className={`w-5 h-5 mt-0.5 shrink-0 ${config.color}`} />
                <div>
                  <p className="text-sm font-medium text-gray-200">{rec.suggestion}</p>
                  <p className="text-xs text-gray-400 mt-1">{rec.reason}</p>
                  <span className="inline-block mt-2 text-xs text-gray-400 bg-gray-800/80 px-2 py-0.5 rounded font-medium">
                    {rec.category}
                  </span>
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}

