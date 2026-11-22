export default function MetricCard({ title, value, unit, label, icon: Icon, color = "violet", children }) {
  const colorClasses = {
    violet: "from-violet-500/20 to-violet-600/5 border-violet-500/30",
    blue: "from-blue-500/20 to-blue-600/5 border-blue-500/30",
    amber: "from-amber-500/20 to-amber-600/5 border-amber-500/30",
    emerald: "from-emerald-500/20 to-emerald-600/5 border-emerald-500/30",
    rose: "from-rose-500/20 to-rose-600/5 border-rose-500/30",
    cyan: "from-cyan-500/20 to-cyan-600/5 border-cyan-500/30",
  };
  
  return (
    <div className={`bg-gradient-to-br ${colorClasses[color] || colorClasses.violet} border rounded-xl p-5`}>
      <div className="flex items-center gap-2 mb-3">
        {Icon && <Icon className="w-5 h-5 text-gray-400" />}
        <h3 className="text-sm font-medium text-gray-400 uppercase tracking-wide">{title}</h3>
      </div>
      {value !== undefined && (
        <div className="flex items-baseline gap-1 mb-1">
          <span className="text-3xl font-bold text-white">{value}</span>
          {unit && <span className="text-sm text-gray-500">{unit}</span>}
        </div>
      )}
      {label && (
        <p className="text-sm text-gray-300 font-medium">{label}</p>
      )}
      {children}
    </div>
  );
}

