import api from './axiosConfig';

// ============================================================
// BOOKING API
// ============================================================

/**
 * Create a new booking.
 *
 * Backend:
 * POST /api/bookings
 *
 * Request:
 * {
 *   flightId: number,
 *   seatClass: string
 * }
 */
export async function createBooking(payload) {
  const response = await api.post('/api/bookings', {
    flightId: payload.flightId,
    seatClass: payload.seatClass,
  });

  return response.data;
}

/**
 * Get all bookings for the currently authenticated user.
 *
 * Backend:
 * GET /api/bookings
 */
export async function getMyBookings() {
  const response = await api.get('/api/bookings');

  return response.data;
}

/**
 * Get booking details by ID.
 *
 * Backend:
 * GET /api/bookings/{id}
 */
export async function getBookingById(bookingId) {
  const response = await api.get(
    `/api/bookings/${bookingId}`
  );

  return response.data;
}

/**
 * Cancel a booking.
 *
 * Backend:
 * PATCH /api/bookings/{id}/cancel
 */
export async function cancelBooking(bookingId) {
  const response = await api.patch(
    `/api/bookings/${bookingId}/cancel`
  );

  return response.data;
}

/**
 * Get all bookings.
 *
 * Currently used for admin functionality.
 *
 * Backend:
 * GET /api/bookings
 */
export async function getAllBookings() {
  const response = await api.get('/api/bookings');

  return response.data;
}