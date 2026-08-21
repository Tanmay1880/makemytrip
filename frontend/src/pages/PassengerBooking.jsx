import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Plus, Minus, User, Plane, Ticket } from 'lucide-react';

import { useBooking } from '@/context/BookingContext';
import { useToast } from '@/context/ToastContext';

import { createBooking } from '@/api/bookingApi';
import { savePassengers } from '@/api/passengerApi';

import {
  formatCurrency,
  formatTime,
  getSeatClassLabel,
} from '@/utils/formatters';

const priceKeyMap = {
  ECONOMY: 'economyPrice',
  PREMIUM_ECONOMY: 'premiumEconomyPrice',
  BUSINESS: 'businessPrice',
};

export default function PassengerBooking() {
  const navigate = useNavigate();

  // IMPORTANT:
  // useToast() already returns { success, error, info, warning }
  const toast = useToast();

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

  // ============================================================
  // SAFETY CHECK
  // ============================================================

  if (!selectedFlight) {
    navigate('/');
    return null;
  }

  // ============================================================
  // PRICE
  // ============================================================

  const pricePerPerson =
    selectedFlight[priceKeyMap[selectedSeatClass]] ||
    selectedFlight.economyPrice ||
    0;

  const totalAmount = pricePerPerson * passengerCount;

  // ============================================================
  // PASSENGER UPDATE
  // ============================================================

  const updatePassenger = (index, field, value) => {
    const updated = [...passengers];

    if (!updated[index]) {
      updated[index] = {
        firstName: '',
        lastName: '',
        dateOfBirth: '',
        gender: '',
        passengerType: 'ADULT',
      };
    }

    updated[index] = {
      ...updated[index],
      [field]: value,
    };

    setPassengers(updated);

    if (errors[`passenger_${index}_${field}`]) {
      setErrors({
        ...errors,
        [`passenger_${index}_${field}`]: undefined,
      });
    }
  };

  // ============================================================
  // PASSENGER COUNT
  // ============================================================

  const adjustCount = (delta) => {
    const newCount = Math.max(
      1,
      Math.min(9, passengerCount + delta)
    );

    setPassengerCount(newCount);

    const updated = [...passengers];

    updated.length = newCount;

    for (let i = 0; i < newCount; i++) {
      if (!updated[i]) {
        updated[i] = {
          firstName: '',
          lastName: '',
          dateOfBirth: '',
          gender: '',
          passengerType: 'ADULT',
        };
      }
    }

    setPassengers(updated);
  };

  // ============================================================
  // VALIDATION
  // ============================================================

  const validate = () => {
    const errs = {};

    passengers
      .slice(0, passengerCount)
      .forEach((passenger, index) => {
        if (!passenger?.firstName?.trim()) {
          errs[`passenger_${index}_firstName`] = 'Required';
        }

        if (!passenger?.lastName?.trim()) {
          errs[`passenger_${index}_lastName`] = 'Required';
        }

        if (!passenger?.dateOfBirth) {
          errs[`passenger_${index}_dateOfBirth`] = 'Required';
        }

        if (!passenger?.gender) {
          errs[`passenger_${index}_gender`] = 'Required';
        }

        if (!passenger?.passengerType) {
          errs[`passenger_${index}_passengerType`] = 'Required';
        }
      });

    return errs;
  };

  // ============================================================
  // SUBMIT
  // ============================================================

  const handleSubmit = async (e) => {
    e.preventDefault();

    const validationErrors = validate();

    setErrors(validationErrors);

    if (Object.keys(validationErrors).length > 0) {
      toast.error('Please fill in all passenger details');
      return;
    }

    setSubmitting(true);

    try {
      // ========================================================
      // STEP 1: CREATE BOOKING
      // ========================================================

      const booking = await createBooking({
        flightId: selectedFlight.id,
        seatClass: selectedSeatClass,
      });

      // ========================================================
      // STEP 2: SAVE PASSENGERS
      // ========================================================

      const passengerData =
        passengers.slice(0, passengerCount);

      await savePassengers({
        bookingId: booking.id,
        passengers: passengerData,
      });

      // ========================================================
      // STEP 3: STORE BOOKING FOR PAYMENT
      // ========================================================

      setCurrentBooking({
        ...booking,

        passengers: passengerData,

        totalAmount,

        flightSummary: {
          flightNumber: selectedFlight.flightNumber,
          airlineName: selectedFlight.airlineName,

          departureAirportCode:
            selectedFlight.departureAirportCode,

          arrivalAirportCode:
            selectedFlight.arrivalAirportCode,

          departureTime:
            selectedFlight.departureTime,

          arrivalTime:
            selectedFlight.arrivalTime,

          departureCity:
            selectedFlight.departureCity,

          arrivalCity:
            selectedFlight.arrivalCity,
        },
      });

      toast.success(
        'Booking created. Proceed to payment.'
      );

      navigate('/payment');

    } catch (err) {
      console.error(
        'Booking creation failed:',
        err
      );

      const message =
        err?.response?.data?.message ||
        'Failed to create booking. Please try again.';

      toast.error(message);

    } finally {
      setSubmitting(false);
    }
  };

  // ============================================================
  // UI
  // ============================================================

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">

      {/* BACK */}

      <button
        onClick={() => navigate(-1)}
        className="btn-ghost mb-4"
      >
        <ArrowLeft className="w-4 h-4" />
        Back
      </button>

      <h1 className="text-2xl font-bold text-gray-900 mb-6">
        Passenger Details
      </h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

        {/* =====================================================
            FORM
        ====================================================== */}

        <div className="lg:col-span-2">

          {/* PASSENGER COUNT */}

          <div className="card p-5 mb-4">

            <label className="label">
              Number of Passengers
            </label>

            <div className="flex items-center gap-3">

              <button
                type="button"
                onClick={() => adjustCount(-1)}
                disabled={passengerCount <= 1}
                className="w-10 h-10 rounded-lg border border-gray-300 flex items-center justify-center hover:bg-gray-50 disabled:opacity-40"
              >
                <Minus className="w-4 h-4" />
              </button>

              <span className="text-xl font-bold w-12 text-center">
                {passengerCount}
              </span>

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

          {/* PASSENGER FORMS */}

          <form
            onSubmit={handleSubmit}
            className="space-y-4"
          >

            {Array.from({
              length: passengerCount,
            }).map((_, i) => (

              <div
                key={i}
                className="card p-5"
              >

                <div className="flex items-center gap-2 mb-4">

                  <div className="w-8 h-8 rounded-full bg-primary-100 flex items-center justify-center">
                    <User className="w-4 h-4 text-primary-700" />
                  </div>

                  <h3 className="font-semibold text-gray-900">
                    Passenger {i + 1}
                  </h3>

                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-3">

                  {/* FIRST NAME */}

                  <div>

                    <label className="label">
                      First Name
                    </label>

                    <input
                      type="text"
                      value={
                        passengers[i]?.firstName || ''
                      }
                      onChange={(e) =>
                        updatePassenger(
                          i,
                          'firstName',
                          e.target.value
                        )
                      }
                      className={`input ${
                        errors[
                          `passenger_${i}_firstName`
                        ]
                          ? 'border-error-400'
                          : ''
                      }`}
                      placeholder="John"
                    />

                    {errors[
                      `passenger_${i}_firstName`
                    ] && (
                      <p className="text-xs text-error-500 mt-1">
                        {
                          errors[
                            `passenger_${i}_firstName`
                          ]
                        }
                      </p>
                    )}

                  </div>

                  {/* LAST NAME */}

                  <div>

                    <label className="label">
                      Last Name
                    </label>

                    <input
                      type="text"
                      value={
                        passengers[i]?.lastName || ''
                      }
                      onChange={(e) =>
                        updatePassenger(
                          i,
                          'lastName',
                          e.target.value
                        )
                      }
                      className={`input ${
                        errors[
                          `passenger_${i}_lastName`
                        ]
                          ? 'border-error-400'
                          : ''
                      }`}
                      placeholder="Doe"
                    />

                    {errors[
                      `passenger_${i}_lastName`
                    ] && (
                      <p className="text-xs text-error-500 mt-1">
                        {
                          errors[
                            `passenger_${i}_lastName`
                          ]
                        }
                      </p>
                    )}

                  </div>

                  {/* DATE OF BIRTH */}

                  <div>

                    <label className="label">
                      Date of Birth
                    </label>

                    <input
                      type="date"
                      value={
                        passengers[i]?.dateOfBirth || ''
                      }
                      onChange={(e) =>
                        updatePassenger(
                          i,
                          'dateOfBirth',
                          e.target.value
                        )
                      }
                      className={`input ${
                        errors[
                          `passenger_${i}_dateOfBirth`
                        ]
                          ? 'border-error-400'
                          : ''
                      }`}
                    />

                    {errors[
                      `passenger_${i}_dateOfBirth`
                    ] && (
                      <p className="text-xs text-error-500 mt-1">
                        {
                          errors[
                            `passenger_${i}_dateOfBirth`
                          ]
                        }
                      </p>
                    )}

                  </div>

                  {/* GENDER */}

                  <div>

                    <label className="label">
                      Gender
                    </label>

                    <select
                      value={
                        passengers[i]?.gender || ''
                      }
                      onChange={(e) =>
                        updatePassenger(
                          i,
                          'gender',
                          e.target.value
                        )
                      }
                      className={`input ${
                        errors[
                          `passenger_${i}_gender`
                        ]
                          ? 'border-error-400'
                          : ''
                      }`}
                    >

                      <option value="">
                        Select
                      </option>

                      <option value="MALE">
                        Male
                      </option>

                      <option value="FEMALE">
                        Female
                      </option>

                      <option value="OTHER">
                        Other
                      </option>

                    </select>

                    {errors[
                      `passenger_${i}_gender`
                    ] && (
                      <p className="text-xs text-error-500 mt-1">
                        {
                          errors[
                            `passenger_${i}_gender`
                          ]
                        }
                      </p>
                    )}

                  </div>

                  {/* PASSENGER TYPE */}

                  <div>

                    <label className="label">
                      Passenger Type
                    </label>

                    <select
                      value={
                        passengers[i]?.passengerType ||
                        'ADULT'
                      }
                      onChange={(e) =>
                        updatePassenger(
                          i,
                          'passengerType',
                          e.target.value
                        )
                      }
                      className={`input ${
                        errors[
                          `passenger_${i}_passengerType`
                        ]
                          ? 'border-error-400'
                          : ''
                      }`}
                    >

                      <option value="ADULT">
                        Adult
                      </option>

                      <option value="CHILD">
                        Child
                      </option>

                      <option value="INFANT">
                        Infant
                      </option>

                    </select>

                    {errors[
                      `passenger_${i}_passengerType`
                    ] && (
                      <p className="text-xs text-error-500 mt-1">
                        {
                          errors[
                            `passenger_${i}_passengerType`
                          ]
                        }
                      </p>
                    )}

                  </div>

                </div>
              </div>
            ))}

            {/* SUBMIT */}

            <button
              type="submit"
              disabled={submitting}
              className="btn-primary w-full py-3"
            >

              {submitting ? (

                <span className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />

              ) : (

                'Create Booking & Continue to Payment'

              )}

            </button>

          </form>

        </div>

        {/* =====================================================
            SUMMARY
        ====================================================== */}

        <div className="lg:col-span-1">

          <div className="card p-5 sticky top-20">

            <h3 className="font-semibold text-gray-900 mb-4 flex items-center gap-2">

              <Ticket className="w-4 h-4 text-primary-600" />

              Booking Summary

            </h3>

            <div className="space-y-3 text-sm">

              <div className="flex items-center gap-2 pb-3 border-b border-gray-100">

                <Plane className="w-4 h-4 text-gray-400 -rotate-45" />

                <div>

                  <p className="font-medium text-gray-900">
                    {selectedFlight.airlineName}
                  </p>

                  <p className="text-xs text-gray-500">
                    {selectedFlight.flightNumber}
                  </p>

                </div>

              </div>

              <div className="flex justify-between">

                <span className="text-gray-500">
                  Route
                </span>

                <span className="font-medium text-gray-900">
                  {selectedFlight.departureAirportCode}
                  {' → '}
                  {selectedFlight.arrivalAirportCode}
                </span>

              </div>

              <div className="flex justify-between">

                <span className="text-gray-500">
                  Departure
                </span>

                <span className="font-medium text-gray-900">
                  {formatTime(
                    selectedFlight.departureTime
                  )}
                </span>

              </div>

              <div className="flex justify-between">

                <span className="text-gray-500">
                  Arrival
                </span>

                <span className="font-medium text-gray-900">
                  {formatTime(
                    selectedFlight.arrivalTime
                  )}
                </span>

              </div>

              <div className="flex justify-between">

                <span className="text-gray-500">
                  Cabin
                </span>

                <span className="font-medium text-gray-900">
                  {getSeatClassLabel(
                    selectedSeatClass
                  )}
                </span>

              </div>

              <div className="flex justify-between">

                <span className="text-gray-500">
                  Passengers
                </span>

                <span className="font-medium text-gray-900">
                  {passengerCount}
                </span>

              </div>

            </div>

            <div className="mt-4 pt-4 border-t border-gray-200 space-y-2">

              <div className="flex justify-between text-sm">

                <span className="text-gray-500">
                  Price per person
                </span>

                <span className="font-medium text-gray-900">
                  {formatCurrency(pricePerPerson)}
                </span>

              </div>

              <div className="flex justify-between items-center pt-2 border-t border-gray-100">

                <span className="font-semibold text-gray-900">
                  Total
                </span>

                <span className="text-xl font-bold text-primary-700">
                  {formatCurrency(totalAmount)}
                </span>

              </div>

            </div>

          </div>

        </div>

      </div>
    </div>
  );
}