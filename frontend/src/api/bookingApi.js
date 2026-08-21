import api from './axiosConfig';

// ============================================================
// BOOKING API
// ------------------------------------------------------------
// NOTE: Adjust the endpoint paths and field names below to
// match your Spring Boot REST API contract.
// ============================================================

/**
 * Create a new booking.
 * @param {{ flightId, seatClass, passengers: [], totalAmount }} payload
 */
export async function createBooking(payload) {
  const response = await api.post('/api/bookings', {
    flightId: payload.flightId,
    seatClass: payload.seatClass,
  });

  return response.data;
}

/**
 * Get all bookings for the current user.
 */
export async function getMyBookings() {
  // TODO: Replace with your actual endpoint
  // const response = await api.get('/bookings/my');
  // return response.data;

  // --- Placeholder ---
  return mockGetMyBookings();
}

/**
 * Get booking details by ID.
 * @param {string|number} bookingId
 */
export async function getBookingById(bookingId) {
  // TODO: Replace with your actual endpoint
  // const response = await api.get(`/bookings/${bookingId}`);
  // return response.data;

  // --- Placeholder ---
  return mockGetBookingById(bookingId);
}

/**
 * Cancel a booking.
 * @param {string|number} bookingId
 */
export async function cancelBooking(bookingId) {
  // TODO: Replace with your actual endpoint
  // const response = await api.put(`/bookings/${bookingId}/cancel`);
  // return response.data;

  // --- Placeholder ---
  return mockCancelBooking(bookingId);
}

/**
 * Get all bookings (admin).
 */
export async function getAllBookings() {
  // TODO: Replace with your actual endpoint
  // const response = await api.get('/bookings');
  // return response.data;

  // --- Placeholder ---
  return mockGetAllBookings();
}

// ============================================================
// PLACEHOLDER IMPLEMENTATIONS (remove when backend is connected)
// ============================================================

const MOCK_BOOKINGS_KEY = 'mmt_mock_bookings';

function delay(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function getMockBookings() {
  const raw = localStorage.getItem(MOCK_BOOKINGS_KEY);
  return raw ? JSON.parse(raw) : [];
}

function saveMockBookings(bookings) {
  localStorage.setItem(MOCK_BOOKINGS_KEY, JSON.stringify(bookings));
}

async function mockCreateBooking(payload) {
  await delay(600);
  const user = JSON.parse(localStorage.getItem('user_data') || '{}');
  const booking = {
    id: Date.now(),
    userId: user.id,
    flightId: payload.flightId,
    seatClass: payload.seatClass,
    passengers: payload.passengers,
    totalAmount: payload.totalAmount,
    status: 'PENDING',
    paymentStatus: 'PENDING',
    bookingDate: new Date().toISOString(),
    ...payload.flightSummary,
  };
  const bookings = getMockBookings();
  bookings.unshift(booking);
  saveMockBookings(bookings);
  return booking;
}

async function mockGetMyBookings() {
  await delay(500);
  const user = JSON.parse(localStorage.getItem('user_data') || '{}');
  return getMockBookings().filter((b) => b.userId === user.id);
}

async function mockGetBookingById(bookingId) {
  await delay(300);
  const booking = getMockBookings().find((b) => String(b.id) === String(bookingId));
  if (!booking) throw { response: { status: 404, data: { message: 'Booking not found' } } };
  return booking;
}

async function mockCancelBooking(bookingId) {
  await delay(400);
  const bookings = getMockBookings();
  const idx = bookings.findIndex((b) => String(b.id) === String(bookingId));
  if (idx === -1) throw { response: { status: 404, data: { message: 'Booking not found' } } };
  bookings[idx].status = 'CANCELLED';
  saveMockBookings(bookings);
  return bookings[idx];
}

async function mockGetAllBookings() {
  await delay(500);
  return getMockBookings();
}
