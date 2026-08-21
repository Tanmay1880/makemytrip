import api from './axiosConfig';

// ============================================================
// PAYMENT API
// ============================================================

/**
 * Create an INITIATED payment for a booking.
 *
 * Backend:
 * POST /api/payments
 *
 * Request:
 * {
 *   bookingId: number
 * }
 */
export async function createPayment(bookingId) {
  const response = await api.post('/api/payments', {
    bookingId,
  });

  return response.data;
}

/**
 * Process an initiated payment.
 *
 * Backend:
 * POST /api/payments/{paymentId}/process
 */
export async function processPayment(paymentId) {
  const response = await api.post(
    `/api/payments/${paymentId}/process`
  );

  return response.data;
}