import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  CreditCard,
  Wallet,
  Landmark,
  CheckCircle,
  XCircle,
  Lock,
} from 'lucide-react';

import { useBooking } from '@/context/BookingContext';
import { useToast } from '@/context/ToastContext';

import {
  createPayment,
  processPayment,
} from '@/api/paymentApi';

import {
  formatCurrency,
  getSeatClassLabel,
} from '@/utils/formatters';

// ============================================================
// PAYMENT METHODS
// ============================================================

const paymentMethods = [
  {
    key: 'CARD',
    label: 'Credit / Debit Card',
    icon: CreditCard,
  },
  {
    key: 'WALLET',
    label: 'Digital Wallet',
    icon: Wallet,
  },
  {
    key: 'BANK',
    label: 'Net Banking',
    icon: Landmark,
  },
];

// ============================================================
// PAYMENT PAGE
// ============================================================

export default function Payment() {
  const navigate = useNavigate();

  // IMPORTANT:
  // useToast() already returns { success, error, info, warning }
  const toast = useToast();

  const {
    currentBooking,
    selectedFlight,
    selectedSeatClass,
    passengerCount,
    reset,
  } = useBooking();

  const [method, setMethod] = useState('CARD');

  const [cardForm, setCardForm] = useState({
    number: '',
    name: '',
    expiry: '',
    cvv: '',
  });

  const [processing, setProcessing] = useState(false);
  const [result, setResult] = useState(null);

  // ============================================================
  // SAFETY CHECK
  // ============================================================

  if (!currentBooking) {
    navigate('/');
    return null;
  }

  // Backend calculates the actual booking amount.
  // This is only used for displaying the amount already stored
  // in the frontend booking context.
  const totalAmount = currentBooking.totalAmount || 0;

  // ============================================================
  // CARD FORM
  // ============================================================

  const updateCard = (field, value) => {
    setCardForm((previous) => ({
      ...previous,
      [field]: value,
    }));
  };

  // ============================================================
  // PAYMENT
  // ============================================================

  const handlePay = async (e) => {
    e.preventDefault();

    if (processing) {
      return;
    }

    setProcessing(true);

    try {
      // ========================================================
      // STEP 1
      // Create INITIATED payment in backend
      // ========================================================

      const payment = await createPayment(
        currentBooking.id
      );

      if (!payment?.id) {
        throw new Error(
          'Payment could not be created.'
        );
      }

      // ========================================================
      // STEP 2
      // Process payment through fake payment gateway
      // ========================================================

      const paymentResult = await processPayment(
        payment.id
      );

      setResult(paymentResult);

      if (paymentResult?.status === 'SUCCESS') {
        toast.success(
          'Payment successful! Your booking is confirmed.'
        );
      } else {
        toast.error(
          paymentResult?.message ||
          'Payment failed. Please try again.'
        );
      }

    } catch (err) {
      console.error(
        'Payment processing failed:',
        err
      );

      const message =
        err?.response?.data?.message ||
        err?.message ||
        'Payment processing failed. Please try again.';

      toast.error(message);

      setResult({
        status: 'FAILED',
        message,
      });

    } finally {
      setProcessing(false);
    }
  };

  // ============================================================
  // FINISH
  // ============================================================

  const handleFinish = () => {
    reset();
    navigate('/bookings');
  };

  // ============================================================
  // RESULT SCREEN
  // ============================================================

  if (result) {
    const success =
      result.status === 'SUCCESS';

    return (
      <div className="max-w-md mx-auto px-4 py-16 text-center">

        {/* RESULT ICON */}

        <div
          className={`w-20 h-20 rounded-full flex items-center justify-center mx-auto mb-6 ${
            success
              ? 'bg-success-100'
              : 'bg-error-100'
          }`}
        >
          {success ? (
            <CheckCircle className="w-10 h-10 text-success-600" />
          ) : (
            <XCircle className="w-10 h-10 text-error-600" />
          )}
        </div>

        {/* TITLE */}

        <h1 className="text-2xl font-bold text-gray-900 mb-2">
          {success
            ? 'Payment Successful'
            : 'Payment Failed'}
        </h1>

        {/* MESSAGE */}

        <p className="text-sm text-gray-500 mb-6">

          {success
            ? 'Your payment was processed successfully and your booking has been confirmed.'
            : result.message ||
              'Your payment could not be processed. Please try again.'}

        </p>

        {/* SUCCESS DETAILS */}

        {success && (
          <div className="card p-5 mb-6 text-left">

            <div className="space-y-2 text-sm">

              <div className="flex justify-between">

                <span className="text-gray-500">
                  Booking ID
                </span>

                <span className="font-medium text-gray-900">
                  #{currentBooking.id}
                </span>

              </div>

              <div className="flex justify-between">

                <span className="text-gray-500">
                  Amount Paid
                </span>

                <span className="font-medium text-gray-900">
                  {formatCurrency(totalAmount)}
                </span>

              </div>

              <div className="flex justify-between">

                <span className="text-gray-500">
                  Method
                </span>

                <span className="font-medium text-gray-900">
                  {method}
                </span>

              </div>

              {result.id && (
                <div className="flex justify-between">

                  <span className="text-gray-500">
                    Payment ID
                  </span>

                  <span className="font-medium text-gray-900">
                    #{result.id}
                  </span>

                </div>
              )}

            </div>

          </div>
        )}

        {/* ACTIONS */}

        <div className="flex gap-3">

          {success ? (

            <button
              onClick={handleFinish}
              className="btn-primary flex-1 py-3"
            >
              View My Bookings
            </button>

          ) : (

            <>
              <button
                onClick={() => setResult(null)}
                className="btn-outline flex-1 py-3"
              >
                Try Again
              </button>

              <button
                onClick={handleFinish}
                className="btn-primary flex-1 py-3"
              >
                Go to Bookings
              </button>
            </>

          )}

        </div>

      </div>
    );
  }

  // ============================================================
  // PAYMENT FORM
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
        Payment
      </h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

        {/* ======================================================
            PAYMENT FORM
        ======================================================= */}

        <div className="lg:col-span-2">

          <form
            onSubmit={handlePay}
            className="card p-6"
          >

            <h3 className="font-semibold text-gray-900 mb-4">
              Select Payment Method
            </h3>

            {/* PAYMENT METHOD BUTTONS */}

            <div className="grid grid-cols-3 gap-3 mb-6">

              {paymentMethods.map((pm) => {

                const Icon = pm.icon;

                const isSelected =
                  method === pm.key;

                return (
                  <button
                    key={pm.key}
                    type="button"
                    onClick={() =>
                      setMethod(pm.key)
                    }
                    className={`p-4 rounded-lg border-2 text-center transition-all ${
                      isSelected
                        ? 'border-primary-500 bg-primary-50'
                        : 'border-gray-200 hover:border-gray-300'
                    }`}
                  >

                    <Icon
                      className={`w-6 h-6 mx-auto mb-2 ${
                        isSelected
                          ? 'text-primary-600'
                          : 'text-gray-400'
                      }`}
                    />

                    <p
                      className={`text-xs font-medium ${
                        isSelected
                          ? 'text-primary-700'
                          : 'text-gray-600'
                      }`}
                    >
                      {pm.label}
                    </p>

                  </button>
                );
              })}

            </div>

            {/* ==================================================
                CARD
            =================================================== */}

            {method === 'CARD' && (

              <div className="space-y-4 animate-fade-in">

                <div>

                  <label className="label">
                    Card Number
                  </label>

                  <input
                    type="text"
                    value={cardForm.number}
                    onChange={(e) =>
                      updateCard(
                        'number',
                        e.target.value
                      )
                    }
                    placeholder="1234 5678 9012 3456"
                    className="input"
                    maxLength={19}
                  />

                </div>

                <div>

                  <label className="label">
                    Cardholder Name
                  </label>

                  <input
                    type="text"
                    value={cardForm.name}
                    onChange={(e) =>
                      updateCard(
                        'name',
                        e.target.value
                      )
                    }
                    placeholder="John Doe"
                    className="input"
                  />

                </div>

                <div className="grid grid-cols-2 gap-3">

                  <div>

                    <label className="label">
                      Expiry Date
                    </label>

                    <input
                      type="text"
                      value={cardForm.expiry}
                      onChange={(e) =>
                        updateCard(
                          'expiry',
                          e.target.value
                        )
                      }
                      placeholder="MM/YY"
                      className="input"
                      maxLength={5}
                    />

                  </div>

                  <div>

                    <label className="label">
                      CVV
                    </label>

                    <input
                      type="password"
                      value={cardForm.cvv}
                      onChange={(e) =>
                        updateCard(
                          'cvv',
                          e.target.value
                        )
                      }
                      placeholder="123"
                      className="input"
                      maxLength={4}
                    />

                  </div>

                </div>

              </div>

            )}

            {/* ==================================================
                WALLET
            =================================================== */}

            {method === 'WALLET' && (

              <div className="p-6 bg-gray-50 rounded-lg text-center animate-fade-in">

                <Wallet className="w-10 h-10 text-gray-400 mx-auto mb-2" />

                <p className="text-sm text-gray-600">
                  This is a simulated wallet payment.
                  No real transaction will occur.
                </p>

              </div>

            )}

            {/* ==================================================
                BANK
            =================================================== */}

            {method === 'BANK' && (

              <div className="p-6 bg-gray-50 rounded-lg text-center animate-fade-in">

                <Landmark className="w-10 h-10 text-gray-400 mx-auto mb-2" />

                <p className="text-sm text-gray-600">
                  This is a simulated net-banking payment.
                  No real transaction will occur.
                </p>

              </div>

            )}

            {/* DEMO NOTICE */}

            <div className="flex items-center gap-2 mt-6 text-xs text-gray-400">

              <Lock className="w-3.5 h-3.5" />

              This is a simulated payment.
              No real transaction will occur.

            </div>

            {/* PAY BUTTON */}

            <button
              type="submit"
              disabled={processing}
              className="btn-primary w-full py-3 mt-4"
            >

              {processing ? (

                <>
                  <span className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin" />

                  Processing Payment...
                </>

              ) : (

                `Pay ${formatCurrency(totalAmount)}`

              )}

            </button>

          </form>

        </div>

        {/* ======================================================
            BOOKING SUMMARY
        ======================================================= */}

        <div className="lg:col-span-1">

          <div className="card p-5 sticky top-20">

            <h3 className="font-semibold text-gray-900 mb-4">
              Booking Summary
            </h3>

            <div className="space-y-3 text-sm">

              <div className="flex justify-between">

                <span className="text-gray-500">
                  Booking ID
                </span>

                <span className="font-medium text-gray-900">
                  #{currentBooking.id}
                </span>

              </div>

              <div className="flex justify-between">

                <span className="text-gray-500">
                  Flight
                </span>

                <span className="font-medium text-gray-900">
                  {currentBooking.flightNumber ||
                    selectedFlight?.flightNumber ||
                    '—'}
                </span>

              </div>

              <div className="flex justify-between">

                <span className="text-gray-500">
                  Route
                </span>

                <span className="font-medium text-gray-900">
                  {currentBooking.departureAirportCode ||
                    selectedFlight?.departureAirportCode ||
                    '—'}
                  {' → '}
                  {currentBooking.arrivalAirportCode ||
                    selectedFlight?.arrivalAirportCode ||
                    '—'}
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

            <div className="mt-4 pt-4 border-t border-gray-200 flex justify-between items-center">

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
  );
}