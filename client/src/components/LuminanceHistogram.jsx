import { AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer } from "recharts";

export default function LuminanceHistogram({ data }) {
  if (!data || !data.luminance) return null;
  
  const chartData = [];
  for (let i = 0; i < 256; i += 2) {
    chartData.push({
      bin: i,
      luminance: data.luminance[i] || 0,
    });
  }
  
  return (
    <div className="bg-gray-900/50 border border-gray-800 rounded-xl p-5">
      <h3 className="text-sm font-medium text-gray-400 uppercase tracking-wide mb-4">Luminance Histogram</h3>
      <div className="h-52 w-full">
        <ResponsiveContainer width="100%" height="100%">
          <AreaChart data={chartData}>
            <XAxis dataKey="bin" tick={{ fill: '#6b7280', fontSize: 10 }} axisLine={{ stroke: '#374151' }} />
            <YAxis tick={{ fill: '#6b7280', fontSize: 10 }} axisLine={{ stroke: '#374151' }} />
            <Tooltip
              contentStyle={{ backgroundColor: '#1f2937', border: '1px solid #374151', borderRadius: '8px' }}
              labelStyle={{ color: '#9ca3af' }}
            />
            <Area type="monotone" dataKey="luminance" stroke="#a78bfa" fill="#a78bfa" fillOpacity={0.2} strokeWidth={1.5} dot={false} />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}

