import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Plane, Clock, Users, XCircle, Loader } from 'lucide-react';
import { getMyBookings, cancelBooking } from '@/api/bookingApi';
import { useToast } from '@/context/ToastContext';
import StatusBadge from '@/components/StatusBadge';
import EmptyState from '@/components/EmptyState';
import { formatCurrency, formatTime, formatDate, getSeatClassLabel } from '@/utils/formatters';

export default function MyBookings() {
  const { toast } = useToast();
  const navigate = useNavigate();

  const [bookings, setBookings] = useState([]);
  const [loading, setLoading] = useState(true);
  const [cancellingId, setCancellingId] = useState(null);

  useEffect(() => {
    loadBookings();
  }, []);

  const loadBookings = async () => {
    setLoading(true);
    try {
      const data = await getMyBookings();
      setBookings(data);
    } catch {
      toast.error('Failed to load your bookings');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (bookingId) => {
    setCancellingId(bookingId);
    try {
      await cancelBooking(bookingId);
      toast.success('Booking cancelled successfully');
      loadBookings();
    } catch {
      toast.error('Failed to cancel booking');
    } finally {
      setCancellingId(null);
    }
  };

  if (loading) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-8">
        <div className="space-y-4">
          {[0, 1, 2].map((i) => (
            <div key={i} className="card p-6 animate-pulse">
              <div className="h-6 w-32 bg-gray-200 rounded mb-4" />
              <div className="h-4 w-full bg-gray-100 rounded mb-2" />
              <div className="h-4 w-2/3 bg-gray-100 rounded" />
            </div>
          ))}
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">My Bookings</h1>

      {bookings.length === 0 ? (
        <EmptyState
          title="No bookings yet"
          message="Start by searching for flights and book your next trip."
          action={
            <button onClick={() => navigate('/')} className="btn-primary">
              Search Flights
            </button>
          }
        />
      ) : (
        <div className="space-y-4 animate-fade-in">
          {bookings.map((booking) => (
            <div key={booking.id} className="card overflow-hidden">
              {/* Header */}
              <div className="flex items-center justify-between px-5 py-3 bg-gray-50 border-b border-gray-100">
                <div className="flex items-center gap-3">
                  <span className="text-sm font-medium text-gray-500">Booking</span>
                  <span className="font-semibold text-gray-900">#{booking.id}</span>
                </div>
                <div className="flex items-center gap-2">
                  <StatusBadge status={booking.status} />
                  <StatusBadge status={booking.paymentStatus} />
                </div>
              </div>

              {/* Body */}
              <div className="p-5">
                <div className="flex flex-col md:flex-row gap-4">
                  {/* Flight info */}
                  <div className="flex items-center gap-3 flex-1">
                    <div className="w-10 h-10 rounded-lg bg-primary-100 flex items-center justify-center">
                      <Plane className="w-5 h-5 text-primary-700 -rotate-45" />
                    </div>
                    <div>
                      <p className="font-semibold text-gray-900 text-sm">{booking.airlineName}</p>
                      <p className="text-xs text-gray-500">{booking.flightNumber}</p>
                    </div>
                  </div>

                  <div className="flex items-center gap-6 text-sm">
                    <div>
                      <p className="font-semibold text-gray-900">{formatTime(booking.departureTime)}</p>
                      <p className="text-xs text-gray-500">{booking.departureAirportCode}</p>
                    </div>
                    <div className="flex flex-col items-center">
                      <Clock className="w-3.5 h-3.5 text-gray-400 mb-1" />
                      <span className="text-xs text-gray-500">{booking.departureCity} → {booking.arrivalCity}</span>
                    </div>
                    <div>
                      <p className="font-semibold text-gray-900">{formatTime(booking.arrivalTime)}</p>
                      <p className="text-xs text-gray-500">{booking.arrivalAirportCode}</p>
                    </div>
                  </div>
                </div>

                {/* Details */}
                <div className="mt-4 pt-4 border-t border-gray-100 grid grid-cols-2 md:grid-cols-4 gap-4 text-sm">
                  <div>
                    <p className="text-xs text-gray-400">Cabin</p>
                    <p className="font-medium text-gray-900">{getSeatClassLabel(booking.seatClass)}</p>
                  </div>
                  <div>
                    <p className="text-xs text-gray-400">Passengers</p>
                    <p className="font-medium text-gray-900 flex items-center gap-1">
                      <Users className="w-3.5 h-3.5" />
                      {booking.passengers?.length || 1}
                    </p>
                  </div>
                  <div>
                    <p className="text-xs text-gray-400">Booked on</p>
                    <p className="font-medium text-gray-900">{formatDate(booking.bookingDate)}</p>
                  </div>
                  <div>
                    <p className="text-xs text-gray-400">Total</p>
                    <p className="font-bold text-primary-700">{formatCurrency(booking.totalAmount)}</p>
                  </div>
                </div>

                {/* Passengers */}
                {booking.passengers?.length > 0 && (
                  <div className="mt-4 pt-4 border-t border-gray-100">
                    <p className="text-xs text-gray-400 mb-2">Passengers</p>
                    <div className="flex flex-wrap gap-2">
                      {booking.passengers.map((p, i) => (
                        <span key={i} className="badge bg-gray-100 text-gray-700">
                          {p.firstName} {p.lastName}
                        </span>
                      ))}
                    </div>
                  </div>
                )}

                {/* Cancel button */}
                {booking.status !== 'CANCELLED' && booking.status !== 'COMPLETED' && (
                  <div className="mt-4 pt-4 border-t border-gray-100">
                    <button
                      onClick={() => handleCancel(booking.id)}
                      disabled={cancellingId === booking.id}
                      className="btn-danger text-sm"
                    >
                      {cancellingId === booking.id ? (
                        <>
                          <Loader className="w-4 h-4 animate-spin" /> Cancelling...
                        </>
                      ) : (
                        <>
                          <XCircle className="w-4 h-4" /> Cancel Booking
                        </>
                      )}
                    </button>
                  </div>
                )}
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
