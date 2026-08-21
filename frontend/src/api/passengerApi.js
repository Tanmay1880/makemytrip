import api from './axiosConfig';

// ============================================================
// PASSENGER API
// ------------------------------------------------------------
// NOTE: Adjust the endpoint paths and field names below to
// match your Spring Boot REST API contract.
// ============================================================

/**
 * Save passenger details for a booking.
 * @param {{ bookingId, passengers: [] }} payload
 */
export async function savePassengers(payload) {
  // TODO: Replace with your actual endpoint
  // const response = await api.post('/passengers', payload);
  // return response.data;

  // --- Placeholder ---
  await new Promise((r) => setTimeout(r, 400));
  return { success: true, ...payload };
}

/**
 * Get passengers for a booking.
 * @param {string|number} bookingId
 */
export async function getPassengersByBooking(bookingId) {
  // TODO: Replace with your actual endpoint
  // const response = await api.get(`/passengers/booking/${bookingId}`);
  // return response.data;

  // --- Placeholder ---
  await new Promise((r) => setTimeout(r, 300));
  const bookings = JSON.parse(localStorage.getItem('mmt_mock_bookings') || '[]');
  const booking = bookings.find((b) => String(b.id) === String(bookingId));
  return booking ? booking.passengers || [] : [];
}
