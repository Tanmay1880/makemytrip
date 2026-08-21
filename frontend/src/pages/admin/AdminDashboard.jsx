import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { PlaneTakeoff, Plane, MapPin, Ticket, TrendingUp, Clock } from 'lucide-react';
import { getAllFlights } from '@/api/flightApi';
import { getAllAirlines } from '@/api/airlineApi';
import { getAllAirports } from '@/api/airportApi';
import { getAllBookings } from '@/api/bookingApi';
import { formatCurrency, formatTime } from '@/utils/formatters';

export default function AdminDashboard() {
  const [stats, setStats] = useState({ flights: 0, airlines: 0, airports: 0, bookings: 0, revenue: 0 });
  const [recentBookings, setRecentBookings] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const [flights, airlines, airports, bookings] = await Promise.all([
          getAllFlights(),
          getAllAirlines(),
          getAllAirports(),
          getAllBookings(),
        ]);
        const revenue = bookings
          .filter((b) => b.paymentStatus === 'PAID')
          .reduce((sum, b) => sum + (b.totalAmount || 0), 0);
        setStats({
          flights: flights.length,
          airlines: airlines.length,
          airports: airports.length,
          bookings: bookings.length,
          revenue,
        });
        setRecentBookings(bookings.slice(0, 5));
      } catch {
        // ignore
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const statCards = [
    { label: 'Total Flights', value: stats.flights, icon: PlaneTakeoff, color: 'primary' },
    { label: 'Airlines', value: stats.airlines, icon: Plane, color: 'accent' },
    { label: 'Airports', value: stats.airports, icon: MapPin, color: 'success' },
    { label: 'Bookings', value: stats.bookings, icon: Ticket, color: 'warning' },
    { label: 'Revenue', value: formatCurrency(stats.revenue), icon: TrendingUp, color: 'primary' },
  ];

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <div className="w-8 h-8 border-2 border-primary-600 border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Dashboard Overview</h1>

      {/* Stat cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-5 gap-4 mb-8">
        {statCards.map((stat) => {
          const Icon = stat.icon;
          return (
            <div key={stat.label} className="card p-5">
              <div className={`w-10 h-10 rounded-lg bg-${stat.color}-100 flex items-center justify-center mb-3`}>
                <Icon className={`w-5 h-5 text-${stat.color}-600`} />
              </div>
              <p className="text-2xl font-bold text-gray-900">{stat.value}</p>
              <p className="text-sm text-gray-500">{stat.label}</p>
            </div>
          );
        })}
      </div>

      {/* Quick links */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">
        <Link to="/admin/flights" className="card p-5 hover:shadow-md transition-shadow group">
          <PlaneTakeoff className="w-8 h-8 text-primary-600 mb-3" />
          <h3 className="font-semibold text-gray-900 group-hover:text-primary-700">Flight Management</h3>
          <p className="text-sm text-gray-500">Add, edit, and manage flight schedules</p>
        </Link>
        <Link to="/admin/airlines" className="card p-5 hover:shadow-md transition-shadow group">
          <Plane className="w-8 h-8 text-accent-500 mb-3" />
          <h3 className="font-semibold text-gray-900 group-hover:text-primary-700">Airline Management</h3>
          <p className="text-sm text-gray-500">Manage airline partners and carriers</p>
        </Link>
        <Link to="/admin/airports" className="card p-5 hover:shadow-md transition-shadow group">
          <MapPin className="w-8 h-8 text-success-500 mb-3" />
          <h3 className="font-semibold text-gray-900 group-hover:text-primary-700">Airport Management</h3>
          <p className="text-sm text-gray-500">Manage airport listings and codes</p>
        </Link>
      </div>

      {/* Recent bookings */}
      <div className="card overflow-hidden">
        <div className="px-5 py-4 border-b border-gray-100">
          <h3 className="font-semibold text-gray-900">Recent Bookings</h3>
        </div>
        {recentBookings.length === 0 ? (
          <div className="p-8 text-center text-sm text-gray-500">No bookings yet</div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-sm">
              <thead className="bg-gray-50 text-gray-500">
                <tr>
                  <th className="text-left px-5 py-3 font-medium">Booking ID</th>
                  <th className="text-left px-5 py-3 font-medium">Flight</th>
                  <th className="text-left px-5 py-3 font-medium">Route</th>
                  <th className="text-left px-5 py-3 font-medium">Amount</th>
                  <th className="text-left px-5 py-3 font-medium">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {recentBookings.map((b) => (
                  <tr key={b.id} className="hover:bg-gray-50">
                    <td className="px-5 py-3 font-medium text-gray-900">#{b.id}</td>
                    <td className="px-5 py-3 text-gray-600">{b.flightNumber}</td>
                    <td className="px-5 py-3 text-gray-600">
                      {b.departureAirportCode} → {b.arrivalAirportCode}
                    </td>
                    <td className="px-5 py-3 font-medium text-gray-900">{formatCurrency(b.totalAmount)}</td>
                    <td className="px-5 py-3">
                      <span className={`badge ${
                        b.status === 'CONFIRMED' ? 'bg-success-100 text-success-700' :
                        b.status === 'CANCELLED' ? 'bg-error-100 text-error-700' :
                        'bg-warning-100 text-warning-700'
                      }`}>
                        {b.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
}
