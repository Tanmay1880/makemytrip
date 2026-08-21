import api from './axiosConfig';

// ============================================================
// PAYMENT API
// ------------------------------------------------------------
// NOTE: Adjust the endpoint paths and field names below to
// match your Spring Boot REST API contract.
// This is a UI-only payment flow — no real payment gateway.
// ============================================================

/**
 * Process a payment for a booking.
 * @param {{ bookingId, amount, paymentMethod, cardDetails? }} payload
 */
export async function processPayment(payload) {
  // TODO: Replace with your actual endpoint
  // const response = await api.post('/payments/process', payload);
  // return response.data;

  // --- Placeholder ---
  return mockProcessPayment(payload);
}

/**
 * Get payment status for a booking.
 * @param {string|number} bookingId
 */
export async function getPaymentStatus(bookingId) {
  // TODO: Replace with your actual endpoint
  // const response = await api.get(`/payments/${bookingId}/status`);
  // return response.data;

  // --- Placeholder ---
  return mockGetPaymentStatus(bookingId);
}

// ============================================================
// PLACEHOLDER IMPLEMENTATIONS (remove when backend is connected)
// ============================================================

function delay(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

const MOCK_BOOKINGS_KEY = 'mmt_mock_bookings';

function getMockBookings() {
  const raw = localStorage.getItem(MOCK_BOOKINGS_KEY);
  return raw ? JSON.parse(raw) : [];
}

function saveMockBookings(bookings) {
  localStorage.setItem(MOCK_BOOKINGS_KEY, JSON.stringify(bookings));
}

async function mockProcessPayment(payload) {
  await delay(1200);
  // Simulate payment success (90% of the time)
  const success = Math.random() > 0.1;
  const bookings = getMockBookings();
  const idx = bookings.findIndex((b) => String(b.id) === String(payload.bookingId));

  const result = {
    paymentId: Date.now(),
    bookingId: payload.bookingId,
    amount: payload.amount,
    method: payload.paymentMethod,
    status: success ? 'SUCCESS' : 'FAILED',
    transactionId: 'TXN' + Date.now(),
    timestamp: new Date().toISOString(),
  };

  if (idx !== -1) {
    bookings[idx].paymentStatus = success ? 'PAID' : 'FAILED';
    bookings[idx].status = success ? 'CONFIRMED' : 'PENDING';
    saveMockBookings(bookings);
  }

  if (!success) {
    return { ...result, status: 'FAILED', message: 'Payment was declined. Please try again.' };
  }
  return result;
}

async function mockGetPaymentStatus(bookingId) {
  await delay(300);
  const booking = getMockBookings().find((b) => String(b.id) === String(bookingId));
  return { bookingId, status: booking ? booking.paymentStatus : 'UNKNOWN' };
}
