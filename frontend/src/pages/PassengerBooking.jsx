import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Plus, Minus, User, Plane, Ticket } from 'lucide-react';
import { useBooking } from '@/context/BookingContext';
import { useToast } from '@/context/ToastContext';
import { createBooking } from '@/api/bookingApi';
import { formatCurrency, formatTime, getSeatClassLabel } from '@/utils/formatters';

const priceKeyMap = {
  ECONOMY: 'economyPrice',
  PREMIUM_ECONOMY: 'premiumEconomyPrice',
  BUSINESS: 'businessPrice',
};

export default function PassengerBooking() {
  const navigate = useNavigate();
  const { toast } = useToast();
  const {
    selectedFlight,
    selectedSeatClass,
    passengerCount,
    setPassengerCount,
    passengers,
    setPassengers,
    setCurrentBooking,
  } = useBooking();

  const [submitting, setSubmitting] = useState(false);
  const [errors, setErrors] = useState({});

  if (!selectedFlight) {
    navigate('/');
    return null;
  }

  const pricePerPerson = selectedFlight[priceKeyMap[selectedSeatClass]] || selectedFlight.economyPrice;
  const totalAmount = pricePerPerson * passengerCount;

  const updatePassenger = (index, field, value) => {
    const updated = [...passengers];
    if (!updated[index]) updated[index] = {};
    updated[index] = { ...updated[index], [field]: value };
    setPassengers(updated);
    if (errors[`passenger_${index}_${field}`]) {
      setErrors({ ...errors, [`passenger_${index}_${field}`]: undefined });
    }
  };

  const adjustCount = (delta) => {
    const newCount = Math.max(1, Math.min(9, passengerCount + delta));
    setPassengerCount(newCount);
    const updated = [...passengers];
    updated.length = newCount;
    for (let i = 0; i < newCount; i++) {
      if (!updated[i]) updated[i] = { firstName: '', lastName: '', gender: '' };
    }
    setPassengers(updated);
  };

  const validate = () => {
    const errs = {};
    passengers.slice(0, passengerCount).forEach((p, i) => {
      if (!p?.firstName) errs[`passenger_${i}_firstName`] = 'Required';
      if (!p?.lastName) errs[`passenger_${i}_lastName`] = 'Required';
      if (!p?.gender) errs[`passenger_${i}_gender`] = 'Required';
    });
    return errs;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const errs = validate();
    setErrors(errs);
    if (Object.keys(errs).length > 0) {
      toast.error('Please fill in all passenger details');
      return;
    }

    setSubmitting(true);
    try {
      const booking = await createBooking({
        flightId: selectedFlight.id,
        seatClass: selectedSeatClass,
        passengers: passengers.slice(0, passengerCount),
        totalAmount,
        flightSummary: {
          flightNumber: selectedFlight.flightNumber,
          airlineName: selectedFlight.airlineName,
          departureAirportCode: selectedFlight.departureAirportCode,
          arrivalAirportCode: selectedFlight.arrivalAirportCode,
          departureTime: selectedFlight.departureTime,
          arrivalTime: selectedFlight.arrivalTime,
          departureCity: selectedFlight.departureCity,
          arrivalCity: selectedFlight.arrivalCity,
        },
      });
      setCurrentBooking(booking);
      toast.success('Booking created. Proceed to payment.');
      navigate('/payment');
    } catch {
      toast.error('Failed to create booking. Please try again.');
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <button onClick={() => navigate(-1)} className="btn-ghost mb-4">
        <ArrowLeft className="w-4 h-4" /> Back
      </button>

      <h1 className="text-2xl font-bold text-gray-900 mb-6">Passenger Details</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Form */}
        <div className="lg:col-span-2">
          {/* Passenger count */}
          <div className="card p-5 mb-4">
            <label className="label">Number of Passengers</label>
            <div className="flex items-center gap-3">
              <button
                type="button"
                onClick={() => adjustCount(-1)}
                disabled={passengerCount <= 1}
                className="w-10 h-10 rounded-lg border border-gray-300 flex items-center justify-center hover:bg-gray-50 disabled:opacity-40"
              >
                <Minus className="w-4 h-4" />
              </button>
              <span className="text-xl font-bold w-12 text-center">{passengerCount}</span>
              <button
                type="button"
                onClick={() => adjustCount(1)}
                disabled={passengerCount >= 9}
                className="w-10 h-10 rounded-lg border border-gray-300 flex items-center justify-center hover:bg-gray-50 disabled:opacity-40"
              >
                <Plus className="w-4 h-4" />
              </button>
            </div>
          </div>

          {/* Passenger forms */}
          <form onSubmit={handleSubmit} className="space-y-4">
            {Array.from({ length: passengerCount }).map((_, i) => (
              <div key={i} className="card p-5">
                <div className="flex items-center gap-2 mb-4">
                  <div className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center">
                    <User className="w-4 h-4 text-primary-700" />
                  </div>
                  <h3 className="font-semibold text-gray-900">Passenger {i + 1}</h3>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-3">
                  <div>
                    <label className="label">First Name</label>
                    <input
                      type="text"
                      value={passengers[i]?.firstName || ''}
                      onChange={(e) => updatePassenger(i, 'firstName', e.target.value)}
                      className={`input ${errors[`passenger_${i}_firstName`] ? 'border-error-400' : ''}`}
                      placeholder="John"
                    />
                    {errors[`passenger_${i}_firstName`] && (
                      <p className="text-xs text-error-500 mt-1">{errors[`passenger_${i}_firstName`]}</p>
                    )}
                  </div>
                  <div>
                    <label className="label">Last Name</label>
                    <input
                      type="text"
                      value={passengers[i]?.lastName || ''}
                      onChange={(e) => updatePassenger(i, 'lastName', e.target.value)}
                      className={`input ${errors[`passenger_${i}_lastName`] ? 'border-error-400' : ''}`}
                      placeholder="Doe"
                    />
                    {errors[`passenger_${i}_lastName`] && (
                      <p className="text-xs text-error-500 mt-1">{errors[`passenger_${i}_lastName`]}</p>
                    )}
                  </div>
                  <div>
                    <label className="label">Gender</label>
                    <select
                      value={passengers[i]?.gender || ''}
                      onChange={(e) => updatePassenger(i, 'gender', e.target.value)}
                      className={`input ${errors[`passenger_${i}_gender`] ? 'border-error-400' : ''}`}
                    >
                      <option value="">Select</option>
                      <option value="MALE">Male</option>
                      <option value="FEMALE">Female</option>
                      <option value="OTHER">Other</option>
                    </select>
                    {errors[`passenger_${i}_gender`] && (
                      <p className="text-xs text-error-500 mt-1">{errors[`passenger_${i}_gender`]}</p>
                    )}
                  </div>
                </div>
              </div>
            ))}

            <button type="submit" disabled={submitting} className="btn-primary w-full py-3">
              {submitting ? (
                <span className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />
              ) : (
                'Create Booking & Continue to Payment'
              )}
            </button>
          </form>
        </div>

        {/* Summary sidebar */}
        <div className="lg:col-span-1">
          <div className="card p-5 sticky top-20">
            <h3 className="font-semibold text-gray-900 mb-4 flex items-center gap-2">
              <Ticket className="w-4 h-4 text-primary-600" /> Booking Summary
            </h3>

            <div className="space-y-3 text-sm">
              <div className="flex items-center gap-2 pb-3 border-b border-gray-100">
                <Plane className="w-4 h-4 text-gray-400 -rotate-45" />
                <div>
                  <p className="font-medium text-gray-900">{selectedFlight.airlineName}</p>
                  <p className="text-xs text-gray-500">{selectedFlight.flightNumber}</p>
                </div>
              </div>

              <div className="flex justify-between">
                <span className="text-gray-500">Route</span>
                <span className="font-medium text-gray-900">
                  {selectedFlight.departureAirportCode} → {selectedFlight.arrivalAirportCode}
                </span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">Departure</span>
                <span className="font-medium text-gray-900">{formatTime(selectedFlight.departureTime)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">Arrival</span>
                <span className="font-medium text-gray-900">{formatTime(selectedFlight.arrivalTime)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">Cabin</span>
                <span className="font-medium text-gray-900">{getSeatClassLabel(selectedSeatClass)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">Passengers</span>
                <span className="font-medium text-gray-900">{passengerCount}</span>
              </div>
            </div>

            <div className="mt-4 pt-4 border-t border-gray-200 space-y-2">
              <div className="flex justify-between text-sm">
                <span className="text-gray-500">Price per person</span>
                <span className="font-medium text-gray-900">{formatCurrency(pricePerPerson)}</span>
              </div>
              <div className="flex justify-between items-center pt-2 border-t border-gray-100">
                <span className="font-semibold text-gray-900">Total</span>
                <span className="text-xl font-bold text-primary-700">{formatCurrency(totalAmount)}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
