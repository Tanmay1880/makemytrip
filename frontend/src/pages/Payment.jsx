import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, CreditCard, Wallet, Landmark, CheckCircle, XCircle, Lock } from 'lucide-react';
import { useBooking } from '@/context/BookingContext';
import { useToast } from '@/context/ToastContext';
import { processPayment } from '@/api/paymentApi';
import { formatCurrency, formatTime, getSeatClassLabel } from '@/utils/formatters';

const paymentMethods = [
  { key: 'CARD', label: 'Credit / Debit Card', icon: CreditCard },
  { key: 'WALLET', label: 'Digital Wallet', icon: Wallet },
  { key: 'BANK', label: 'Net Banking', icon: Landmark },
];

export default function Payment() {
  const navigate = useNavigate();
  const { toast } = useToast();
  const { currentBooking, selectedFlight, selectedSeatClass, passengerCount, reset } = useBooking();

  const [method, setMethod] = useState('CARD');
  const [cardForm, setCardForm] = useState({ number: '', name: '', expiry: '', cvv: '' });
  const [processing, setProcessing] = useState(false);
  const [result, setResult] = useState(null);

  if (!currentBooking) {
    navigate('/');
    return null;
  }

  const totalAmount = currentBooking.totalAmount || 0;

  const handlePay = async (e) => {
    e.preventDefault();
    setProcessing(true);
    try {
      const res = await processPayment({
        bookingId: currentBooking.id,
        amount: totalAmount,
        paymentMethod: method,
        cardDetails: method === 'CARD' ? { last4: cardForm.number.slice(-4) } : undefined,
      });
      setResult(res);
      if (res.status === 'SUCCESS') {
        toast.success('Payment successful! Your booking is confirmed.');
      } else {
        toast.error('Payment failed. Please try again.');
      }
    } catch {
      toast.error('Payment processing failed. Please try again.');
      setResult({ status: 'FAILED', message: 'Payment processing error.' });
    } finally {
      setProcessing(false);
    }
  };

  const handleFinish = () => {
    reset();
    navigate('/bookings');
  };

  // Success / failure screen
  if (result) {
    const success = result.status === 'SUCCESS';
    return (
      <div className="max-w-md mx-auto px-4 py-16 text-center">
        <div className={`w-20 h-20 rounded-full flex items-center justify-center mx-auto mb-6 ${
          success ? 'bg-success-100' : 'bg-error-100'
        }`}>
          {success ? (
            <CheckCircle className="w-10 h-10 text-success-600" />
          ) : (
            <XCircle className="w-10 h-10 text-error-600" />
          )}
        </div>
        <h1 className="text-2xl font-bold text-gray-900 mb-2">
          {success ? 'Payment Successful' : 'Payment Failed'}
        </h1>
        <p className="text-sm text-gray-500 mb-6">
          {success
            ? `Your booking has been confirmed. Transaction ID: ${result.transactionId}`
            : result.message || 'Your payment could not be processed. Please try again.'}
        </p>

        {success && (
          <div className="card p-5 mb-6 text-left">
            <div className="space-y-2 text-sm">
              <div className="flex justify-between">
                <span className="text-gray-500">Booking ID</span>
                <span className="font-medium text-gray-900">#{currentBooking.id}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">Amount Paid</span>
                <span className="font-medium text-gray-900">{formatCurrency(totalAmount)}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">Method</span>
                <span className="font-medium text-gray-900">{method}</span>
              </div>
            </div>
          </div>
        )}

        <div className="flex gap-3">
          {success ? (
            <button onClick={handleFinish} className="btn-primary flex-1 py-3">
              View My Bookings
            </button>
          ) : (
            <>
              <button onClick={() => setResult(null)} className="btn-outline flex-1 py-3">
                Try Again
              </button>
              <button onClick={handleFinish} className="btn-primary flex-1 py-3">
                Go to Bookings
              </button>
            </>
          )}
        </div>
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
      <button onClick={() => navigate(-1)} className="btn-ghost mb-4">
        <ArrowLeft className="w-4 h-4" /> Back
      </button>

      <h1 className="text-2xl font-bold text-gray-900 mb-6">Payment</h1>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Payment form */}
        <div className="lg:col-span-2">
          <form onSubmit={handlePay} className="card p-6">
            <h3 className="font-semibold text-gray-900 mb-4">Select Payment Method</h3>
            <div className="grid grid-cols-3 gap-3 mb-6">
              {paymentMethods.map((pm) => {
                const Icon = pm.icon;
                const isSelected = method === pm.key;
                return (
                  <button
                    key={pm.key}
                    type="button"
                    onClick={() => setMethod(pm.key)}
                    className={`p-4 rounded-lg border-2 text-center transition-all ${
                      isSelected ? 'border-primary-500 bg-primary-50' : 'border-gray-200 hover:border-gray-300'
                    }`}
                  >
                    <Icon className={`w-6 h-6 mx-auto mb-2 ${isSelected ? 'text-primary-600' : 'text-gray-400'}`} />
                    <p className={`text-xs font-medium ${isSelected ? 'text-primary-700' : 'text-gray-600'}`}>
                      {pm.label}
                    </p>
                  </button>
                );
              })}
            </div>

            {method === 'CARD' && (
              <div className="space-y-4 animate-fade-in">
                <div>
                  <label className="label">Card Number</label>
                  <input
                    type="text"
                    value={cardForm.number}
                    onChange={(e) => setCardForm({ ...cardForm, number: e.target.value })}
                    placeholder="1234 5678 9012 3456"
                    className="input"
                    maxLength={19}
                  />
                </div>
                <div>
                  <label className="label">Cardholder Name</label>
                  <input
                    type="text"
                    value={cardForm.name}
                    onChange={(e) => setCardForm({ ...cardForm, name: e.target.value })}
                    placeholder="John Doe"
                    className="input"
                  />
                </div>
                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="label">Expiry Date</label>
                    <input
                      type="text"
                      value={cardForm.expiry}
                      onChange={(e) => setCardForm({ ...cardForm, expiry: e.target.value })}
                      placeholder="MM/YY"
                      className="input"
                      maxLength={5}
                    />
                  </div>
                  <div>
                    <label className="label">CVV</label>
                    <input
                      type="text"
                      value={cardForm.cvv}
                      onChange={(e) => setCardForm({ ...cardForm, cvv: e.target.value })}
                      placeholder="123"
                      className="input"
                      maxLength={4}
                    />
                  </div>
                </div>
              </div>
            )}

            {method === 'WALLET' && (
              <div className="p-6 bg-gray-50 rounded-lg text-center animate-fade-in">
                <Wallet className="w-10 h-10 text-gray-400 mx-auto mb-2" />
                <p className="text-sm text-gray-600">You will be redirected to your wallet to complete the payment.</p>
              </div>
            )}

            {method === 'BANK' && (
              <div className="p-6 bg-gray-50 rounded-lg text-center animate-fade-in">
                <Landmark className="w-10 h-10 text-gray-400 mx-auto mb-2" />
                <p className="text-sm text-gray-600">You will be redirected to your bank's portal to complete the payment.</p>
              </div>
            )}

            <div className="flex items-center gap-2 mt-6 text-xs text-gray-400">
              <Lock className="w-3.5 h-3.5" />
              This is a demo payment. No real transaction will occur.
            </div>

            <button type="submit" disabled={processing} className="btn-primary w-full py-3 mt-4">
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

        {/* Summary */}
        <div className="lg:col-span-1">
          <div className="card p-5 sticky top-20">
            <h3 className="font-semibold text-gray-900 mb-4">Booking Summary</h3>
            <div className="space-y-3 text-sm">
              <div className="flex justify-between">
                <span className="text-gray-500">Booking ID</span>
                <span className="font-medium text-gray-900">#{currentBooking.id}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">Flight</span>
                <span className="font-medium text-gray-900">{currentBooking.flightNumber}</span>
              </div>
              <div className="flex justify-between">
                <span className="text-gray-500">Route</span>
                <span className="font-medium text-gray-900">
                  {currentBooking.departureAirportCode} → {currentBooking.arrivalAirportCode}
                </span>
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
            <div className="mt-4 pt-4 border-t border-gray-200 flex justify-between items-center">
              <span className="font-semibold text-gray-900">Total</span>
              <span className="text-xl font-bold text-primary-700">{formatCurrency(totalAmount)}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
