import { Outlet, Link, useLocation } from 'react-router-dom';
import { Plane, LayoutDashboard, PlaneTakeoff, Building2, MapPin, ArrowLeft } from 'lucide-react';
import { useAuth } from '@/context/AuthContext';

const adminLinks = [
  { to: '/admin', label: 'Dashboard', icon: LayoutDashboard, exact: true },
  { to: '/admin/flights', label: 'Flights', icon: PlaneTakeoff },
  { to: '/admin/airlines', label: 'Airlines', icon: Plane },
  { to: '/admin/airports', label: 'Airports', icon: MapPin },
];

export default function AdminLayout() {
  const { user } = useAuth();
  const location = useLocation();

  const isActive = (link) => {
    if (link.exact) return location.pathname === link.to;
    return location.pathname.startsWith(link.to);
  };

  return (
    <div className="min-h-screen flex bg-gray-50">
      {/* Sidebar */}
      <aside className="w-64 bg-gray-900 text-gray-300 flex flex-col fixed inset-y-0 left-0 z-40">
        <div className="px-6 py-5 border-b border-gray-800">
          <Link to="/" className="flex items-center gap-2">
            <div className="w-9 h-9 rounded-lg bg-primary-600 flex items-center justify-center">
              <Plane className="w-5 h-5 text-white -rotate-45" />
            </div>
            <span className="text-lg font-bold text-white">
              Make<span className="text-primary-400">MyTrip</span>
            </span>
          </Link>
        </div>

        <div className="px-4 py-3 border-b border-gray-800">
          <p className="text-xs text-gray-500 uppercase tracking-wide">Admin Panel</p>
          <p className="text-sm text-white font-medium mt-1">{user?.firstName} {user?.lastName}</p>
        </div>

        <nav className="flex-1 px-3 py-4 space-y-1">
          {adminLinks.map((link) => {
            const Icon = link.icon;
            return (
              <Link
                key={link.to}
                to={link.to}
                className={`flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors ${
                  isActive(link)
                    ? 'bg-primary-600 text-white'
                    : 'text-gray-400 hover:bg-gray-800 hover:text-white'
                }`}
              >
                <Icon className="w-4 h-4" />
                {link.label}
              </Link>
            );
          })}
        </nav>

        <div className="px-3 py-4 border-t border-gray-800">
          <Link
            to="/"
            className="flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium text-gray-400 hover:bg-gray-800 hover:text-white transition-colors"
          >
            <ArrowLeft className="w-4 h-4" />
            Back to Site
          </Link>
        </div>
      </aside>

      {/* Main content */}
      <div className="flex-1 ml-64">
        <div className="px-8 py-6">
          <Outlet />
        </div>
      </div>
    </div>
  );
}
