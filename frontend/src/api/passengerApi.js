import api from './axiosConfig';

// ============================================================
// PASSENGER API
// ============================================================

/**
 * Save passengers for a booking.
 *
 * Backend expects one passenger per request:
 * POST /api/bookings/{bookingId}/passengers
 */
export async function savePassengers({ bookingId, passengers }) {
  const responses = await Promise.all(
    passengers.map((passenger) =>
      api.post(`/api/bookings/${bookingId}/passengers`, {
        firstName: passenger.firstName,
        lastName: passenger.lastName,
        dateOfBirth: passenger.dateOfBirth,
        gender: passenger.gender,
        passengerType: passenger.passengerType,
      })
    )
  );

  return responses.map((response) => response.data);
}

/**
 * Get passengers belonging to a booking.
 */
export async function getPassengersByBooking(bookingId) {
  const response = await api.get(
    `/api/bookings/${bookingId}/passengers`
  );

  return response.data;
}