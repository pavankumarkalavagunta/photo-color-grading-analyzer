import { AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer } from "recharts";

export default function RgbHistogram({ data }) {
  if (!data) return null;
  
  // Prepare chart data - sample every 2nd bin for clean performance
  const chartData = [];
  for (let i = 0; i < 256; i += 2) {
    chartData.push({
      bin: i,
      red: data.red ? data.red[i] : 0,
      green: data.green ? data.green[i] : 0,
      blue: data.blue ? data.blue[i] : 0,
    });
  }
  
  return (
    <div className="bg-gray-900/50 border border-gray-800 rounded-xl p-5">
      <h3 className="text-sm font-medium text-gray-400 uppercase tracking-wide mb-4">RGB Histogram</h3>
      <div className="h-52 w-full">
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart data={chartData}>
            <XAxis dataKey="bin" tick={{ fill: '#6b7280', fontSize: 10 }} axisLine={{ stroke: '#374151' }} />
            <YAxis tick={{ fill: '#6b7280', fontSize: 10 }} axisLine={{ stroke: '#374151' }} />
            <Tooltip
              contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', borderRadius: '8px' }}
              labelStyle={{ color: '#9ca3af' }}
            />
            <Area type="monotone" dataKey="red" stroke="#ef4444" fill="#ef4444" fillOpacity={0.15} strokeWidth={1.5} dot={false} />
            <Area type="monotone" dataKey="green" stroke="#22c55e" fill="#22c55e" fillOpacity={0.15} strokeWidth={1.5} dot={false} />
            <Area type="monotone" dataKey="blue" stroke="#3b82f6" fill="#3b82f6" fillOpacity={0.15} strokeWidth={1.5} dot={false} />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}

