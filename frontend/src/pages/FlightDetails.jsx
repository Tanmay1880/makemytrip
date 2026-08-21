import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { ArrowLeft, Plane, Clock, MapPin, Users, Check } from 'lucide-react';
import { getFlightById } from '@/api/flightApi';
import { useBooking } from '@/context/BookingContext';
import { useAuth } from '@/context/AuthContext';
import { formatCurrency, formatTime, getSeatClassLabel } from '@/utils/formatters';

const seatClasses = [
  { key: 'ECONOMY', label: 'Economy', priceKey: 'economyPrice', seatsKey: 'economySeats' },
  { key: 'PREMIUM_ECONOMY', label: 'Premium Economy', priceKey: 'premiumEconomyPrice', seatsKey: 'premiumEconomySeats' },
  { key: 'BUSINESS', label: 'Business', priceKey: 'businessPrice', seatsKey: 'businessSeats' },
];

export default function FlightDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { selectedFlight, setSelectedFlight, selectedSeatClass, setSelectedSeatClass } = useBooking();
  const { isAuthenticated } = useAuth();

  const [flight, setFlight] = useState(selectedFlight);
  const [loading, setLoading] = useState(!selectedFlight);

  useEffect(() => {
    if (selectedFlight) {
      setFlight(selectedFlight);
      return;
    }
    let cancelled = false;
    const load = async () => {
      try {
        const data = await getFlightById(id);
        if (!cancelled) {
          setFlight(data);
          setSelectedFlight(data);
        }
      } catch {
        if (!cancelled) navigate('/flights');
      } finally {
        if (!cancelled) setLoading(false);
      }
    };
    load();
    return () => { cancelled = true; };
  }, [id]);

  if (loading || !flight) {
    return (
      <div className="max-w-4xl mx-auto px-4 py-8">
        <div className="w-8 h-8 border-2 border-primary-600 border-t-transparent rounded-full animate-spin mx-auto" />
      </div>
    );
  }

  const handleContinue = () => {
    if (!isAuthenticated) {
      navigate('/login', { state: { from: `/flights/${id}` } });
      return;
    }
    navigate('/booking');
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <button onClick={() => navigate(-1)} className="btn-ghost mb-4">
        <ArrowLeft className="w-4 h-4" /> Back to results
      </button>

      {/* Flight info card */}
      <div className="card overflow-hidden mb-6">
        <div className="bg-gradient-to-r from-primary-600 to-primary-800 px-6 py-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-lg bg-white/20 flex items-center justify-center">
              <Plane className="w-5 h-5 text-white -rotate-45" />
            </div>
            <div>
              <p className="text-white font-semibold">{flight.airlineName}</p>
              <p className="text-primary-200 text-sm">{flight.flightNumber}</p>
            </div>
          </div>
        </div>

        <div className="p-6">
          <div className="flex flex-col md:flex-row items-center gap-4 md:gap-8">
            {/* Departure */}
            <div className="text-center md:text-left">
              <p className="text-3xl font-bold text-gray-900">{formatTime(flight.departureTime)}</p>
              <p className="text-lg font-semibold text-gray-700">{flight.departureAirportCode}</p>
              <p className="text-sm text-gray-500">{flight.departureCity}</p>
              <p className="text-xs text-gray-400">{flight.departureAirportName}</p>
            </div>

            {/* Route visual */}
            <div className="flex-1 flex flex-col items-center">
              <div className="flex items-center w-full">
                <div className="w-3 h-3 rounded-full bg-primary-500" />
                <div className="flex-1 h-0.5 bg-gradient-to-r from-primary-500 to-primary-300 relative">
                  <Plane className="w-5 h-5 text-primary-600 absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 -rotate-45 bg-white rounded-full p-0.5" />
                </div>
                <div className="w-3 h-3 rounded-full bg-primary-500" />
              </div>
              <div className="flex items-center gap-1 mt-2 text-sm text-gray-500">
                <Clock className="w-4 h-4" />
                {flight.duration}
              </div>
            </div>

            {/* Arrival */}
            <div className="text-center md:text-right">
              <p className="text-3xl font-bold text-gray-900">{formatTime(flight.arrivalTime)}</p>
              <p className="text-lg font-semibold text-gray-700">{flight.arrivalAirportCode}</p>
              <p className="text-sm text-gray-500">{flight.arrivalCity}</p>
              <p className="text-xs text-gray-400">{flight.arrivalAirportName}</p>
            </div>
          </div>
        </div>
      </div>

      {/* Seat class selection */}
      <h2 className="text-lg font-bold text-gray-900 mb-4">Select Cabin Class</h2>
      <div className="space-y-3 mb-6">
        {seatClasses.map((sc) => {
          const price = flight[sc.priceKey];
          const seats = flight[sc.seatsKey];
          const isSelected = selectedSeatClass === sc.key;
          const isAvailable = seats > 0;

          return (
            <button
              key={sc.key}
              onClick={() => isAvailable && setSelectedSeatClass(sc.key)}
              disabled={!isAvailable}
              className={`w-full text-left card p-4 transition-all ${
                isSelected ? 'border-primary-500 ring-2 ring-primary-500/20' : ''
              } ${!isAvailable ? 'opacity-50 cursor-not-allowed' : 'hover:border-primary-300'}`}
            >
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-3">
                  <div className={`w-5 h-5 rounded-full border-2 flex items-center justify-center ${
                    isSelected ? 'border-primary-600 bg-primary-600' : 'border-gray-300'
                  }`}>
                    {isSelected && <Check className="w-3 h-3 text-white" />}
                  </div>
                  <div>
                    <p className="font-semibold text-gray-900">{sc.label}</p>
                    <p className="text-sm text-gray-500 flex items-center gap-1">
                      <Users className="w-3.5 h-3.5" />
                      {seats} seats available
                    </p>
                  </div>
                </div>
                <div className="text-right">
                  <p className="text-xl font-bold text-primary-700">{formatCurrency(price)}</p>
                  <p className="text-xs text-gray-400">per passenger</p>
                </div>
              </div>
            </button>
          );
        })}
      </div>

      {/* Continue button */}
      <div className="card p-5 flex items-center justify-between">
        <div>
          <p className="text-sm text-gray-500">Selected class</p>
          <p className="text-lg font-semibold text-gray-900">
            {getSeatClassLabel(selectedSeatClass)}
          </p>
        </div>
        <button onClick={handleContinue} className="btn-primary px-8 py-3">
          Continue to Booking
        </button>
      </div>
    </div>
  );
}
