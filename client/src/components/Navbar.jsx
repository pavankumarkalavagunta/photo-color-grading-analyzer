import { Link, useLocation } from "react-router-dom";
import { Camera, Upload, History, Home } from "lucide-react";

export default function Navbar() {
  const location = useLocation();
  
  const isActive = (path) =>
    location.pathname === path
      ? "text-violet-400 border-b-2 border-violet-400"
      : "text-gray-400 hover:text-gray-200";
  
  return (
    <nav className="bg-gray-900/80 backdrop-blur-md border-b border-gray-800 sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex items-center justify-between h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center gap-2 group">
            <div className="p-2 bg-violet-600 rounded-lg group-hover:bg-violet-500 transition-colors">
              <Camera className="w-5 h-5 text-white" />
            </div>
            <span className="text-lg font-bold bg-gradient-to-r from-violet-400 to-pink-400 bg-clip-text text-transparent">
              PhotoGrade
            </span>
          </Link>
          
          {/* Nav Links */}
          <div className="flex items-center gap-6">
            <Link to="/" className={`flex items-center gap-1.5 pb-0.5 text-sm font-medium ${isActive("/")}`}>
              <Home className="w-4 h-4" /> Home
            </Link>
            <Link to="/upload" className={`flex items-center gap-1.5 pb-0.5 text-sm font-medium ${isActive("/upload")}`}>
              <Upload className="w-4 h-4" /> Upload
            </Link>
            <Link to="/history" className={`flex items-center gap-1.5 pb-0.5 text-sm font-medium ${isActive("/history")}`}>
              <History className="w-4 h-4" /> History
            </Link>
          </div>
        </div>
      </div>
    </nav>
  );
}

