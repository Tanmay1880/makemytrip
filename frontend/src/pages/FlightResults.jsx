import { useState, useEffect } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, SlidersHorizontal, AlertCircle } from 'lucide-react';
import { searchFlights } from '@/api/flightApi';
import FlightCard from '@/components/FlightCard';
import FlightCardSkeleton from '@/components/FlightCardSkeleton';
import EmptyState from '@/components/EmptyState';
import ErrorState from '@/components/ErrorState';
import { useBooking } from '@/context/BookingContext';
import { useToast } from '@/context/ToastContext';

export default function FlightResults() {
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const { toast } = useToast();
  const { setSelectedFlight } = useBooking();

  const [flights, setFlights] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [sortBy, setSortBy] = useState('price');

  const from = searchParams.get('from') || '';
  const to = searchParams.get('to') || '';
  const date = searchParams.get('date') || '';

  useEffect(() => {
    let cancelled = false;
    const loadFlights = async () => {
      setLoading(true);
      setError(null);
      try {
        const data = await searchFlights({ from, to, date });
        if (!cancelled) {
          setFlights(data);
        }
      } catch (err) {
        if (!cancelled) {
          setError(err);
          toast.error('Failed to load flights. Please try again.');
        }
      } finally {
        if (!cancelled) setLoading(false);
      }
    };
    loadFlights();
    return () => {
      cancelled = true;
    };
  }, [from, to, date]);

  const sortedFlights = [...flights].sort((a, b) => {
    if (sortBy === 'price') return a.economyPrice - b.economyPrice;
    if (sortBy === 'duration') return a.durationMinutes - b.durationMinutes;
    if (sortBy === 'departure') return a.departureTime.localeCompare(b.departureTime);
    return 0;
  });

  const handleSelect = (flight) => {
    setSelectedFlight(flight);
    navigate(`/flights/${flight.id}`);
  };

  return (
    <div className="max-w-5xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      {/* Back + header */}
      <div className="flex items-center gap-3 mb-6">
        <button onClick={() => navigate('/')} className="btn-ghost">
          <ArrowLeft className="w-4 h-4" /> Back
        </button>
        <div>
          <h1 className="text-2xl font-bold text-gray-900">
            {from || 'All'} <span className="text-gray-400 mx-1">&rarr;</span> {to || 'Destinations'}
          </h1>
          {date && <p className="text-sm text-gray-500">{date}</p>}
        </div>
      </div>

      {/* Sort bar */}
      <div className="flex items-center justify-between mb-4">
        <p className="text-sm text-gray-500">
          {loading ? 'Searching...' : `${sortedFlights.length} flight${sortedFlights.length !== 1 ? 's' : ''} found`}
        </p>
        <div className="flex items-center gap-2">
          <SlidersHorizontal className="w-4 h-4 text-gray-400" />
          <select
            value={sortBy}
            onChange={(e) => setSortBy(e.target.value)}
            className="text-sm border border-gray-300 rounded-lg px-3 py-1.5 focus:outline-none focus:border-primary-500"
          >
            <option value="price">Sort by Price</option>
            <option value="duration">Sort by Duration</option>
            <option value="departure">Sort by Departure</option>
          </select>
        </div>
      </div>

      {/* Results */}
      {loading && (
        <div className="space-y-4">
          {[0, 1, 2, 3].map((i) => <FlightCardSkeleton key={i} />)}
        </div>
      )}

      {!loading && error && (
        <ErrorState
          title="Something went wrong"
          message="We couldn't load flights right now. Please try your search again."
          onRetry={() => window.location.reload()}
        />
      )}

      {!loading && !error && sortedFlights.length === 0 && (
        <EmptyState
          title="No flights found"
          message="Try adjusting your search criteria or selecting different airports."
          action={
            <button onClick={() => navigate('/')} className="btn-primary">
              New Search
            </button>
          }
        />
      )}

      {!loading && !error && sortedFlights.length > 0 && (
        <div className="space-y-4 animate-fade-in">
          {sortedFlights.map((flight) => (
            <FlightCard key={flight.id} flight={flight} onSelect={handleSelect} />
          ))}
        </div>
      )}
    </div>
  );
}
