import api from './axiosConfig';

// ============================================================
// FLIGHT API
// ------------------------------------------------------------
// NOTE: Adjust the endpoint paths and field names below to
// match your Spring Boot REST API contract.
// ============================================================

/**
 * Search for flights.
 * Frontend passes airport codes (`from`/`to`) and `date` from the URL.
 * Backend expects: departureAirportId, arrivalAirportId, departureDate.
 * @param {{ from: string, to: string, date: string }} params
 * @returns {Promise<Array>}
 */
export async function searchFlights({ from, to, date }) {
  const airportsResponse = await api.get('/api/airports');
  const airports = airportsResponse.data || [];

  const departureAirportId = resolveAirportId(from, airports);
  const arrivalAirportId = resolveAirportId(to, airports);

  if (departureAirportId == null || arrivalAirportId == null) {
    const error = new Error('One or both airports could not be resolved');
    error.response = {
      status: 400,
      data: { message: 'Unknown airport code. Ensure airports exist in the backend.' },
    };
    throw error;
  }

  const response = await api.get('/api/flights/search', {
    params: {
      departureAirportId,
      arrivalAirportId,
      departureDate: date,
    },
  });

  return (response.data || []).map((flight) => mapFlightResponse(flight, airports));
}

/**
 * Get flight details by ID.
 * @param {string|number} flightId
 */
export async function getFlightById(flightId) {
  // TODO: Replace with your actual endpoint
  // const response = await api.get(`/flights/${flightId}`);
  // return response.data;

  // --- Placeholder ---
  return mockGetFlightById(flightId);
}

/**
 * Get all flights (admin).
 */
export async function getAllFlights() {
  // TODO: Replace with your actual endpoint
  // const response = await api.get('/flights');
  // return response.data;

  // --- Placeholder ---
  return mockGetAllFlights();
}

/**
 * Create a flight (admin).
 * @param {object} flightData
 */
export async function createFlight(flightData) {
  // TODO: Replace with your actual endpoint
  // const response = await api.post('/flights', flightData);
  // return response.data;

  // --- Placeholder ---
  return mockCreateFlight(flightData);
}

/**
 * Update a flight (admin).
 * @param {string|number} flightId
 * @param {object} flightData
 */
export async function updateFlight(flightId, flightData) {
  // TODO: Replace with your actual endpoint
  // const response = await api.put(`/flights/${flightId}`, flightData);
  // return response.data;

  // --- Placeholder ---
  return mockUpdateFlight(flightId, flightData);
}

/**
 * Delete a flight (admin).
 * @param {string|number} flightId
 */
export async function deleteFlight(flightId) {
  // TODO: Replace with your actual endpoint
  // await api.delete(`/flights/${flightId}`);

  // --- Placeholder ---
  return mockDeleteFlight(flightId);
}

// ============================================================
// PLACEHOLDER IMPLEMENTATIONS (remove when backend is connected)
// ============================================================

import { mockFlights, mockAirlines, mockAirports } from './mockData';

function delay(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function formatDuration(minutes) {
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  return `${h}h ${m.toString().padStart(2, '0')}m`;
}

/**
 * Resolve a Home/URL airport value (code or numeric id) to a backend airport id.
 */
function resolveAirportId(value, airports) {
  if (value == null || value === '') return null;
  const raw = String(value).trim();
  if (/^\d+$/.test(raw)) {
    const byId = airports.find((a) => String(a.id) === raw);
    if (byId) return byId.id;
  }
  const byCode = airports.find(
    (a) => a.code && a.code.toUpperCase() === raw.toUpperCase()
  );
  return byCode ? byCode.id : null;
}

/**
 * Map Spring Boot FlightResponse to the shape FlightCard / FlightResults expect.
 */
function mapFlightResponse(f, airports = []) {
  const depAirport =
    airports.find((a) => a.id === f.departureAirportId) ||
    airports.find((a) => a.code === f.departureAirportCode);
  const arrAirport =
    airports.find((a) => a.id === f.arrivalAirportId) ||
    airports.find((a) => a.code === f.arrivalAirportCode);

  const depTime = new Date(f.departureTime);
  const arrTime = new Date(f.arrivalTime);
  let durationMin = (arrTime - depTime) / 60000;
  if (Number.isNaN(durationMin)) {
    durationMin = 0;
  } else if (durationMin < 0) {
    durationMin += 24 * 60;
  }

  return {
    ...f,
    economySeats: f.economySeatsAvailable ?? f.economySeats ?? 0,
    premiumEconomySeats: f.premiumEconomySeatsAvailable ?? f.premiumEconomySeats ?? 0,
    businessSeats: f.businessSeatsAvailable ?? f.businessSeats ?? 0,
    departureCity: depAirport?.city || '',
    arrivalCity: arrAirport?.city || '',
    duration: formatDuration(Math.round(durationMin)),
    durationMinutes: durationMin,
  };
}

async function mockGetFlightById(flightId) {
  await delay(400);
  const flight = mockFlights.find((f) => String(f.id) === String(flightId));
  if (!flight) throw { response: { status: 404, data: { message: 'Flight not found' } } };
  return enrichFlight(flight);
}

async function mockGetAllFlights() {
  await delay(500);
  return mockFlights.map(enrichFlight);
}

async function mockCreateFlight(data) {
  await delay(400);
  const newFlight = {
    id: Date.now(),
    ...data,
    economySeats: data.economySeats || 100,
    premiumEconomySeats: data.premiumEconomySeats || 50,
    businessSeats: data.businessSeats || 20,
    economyPrice: data.economyPrice || 200,
    premiumEconomyPrice: data.premiumEconomyPrice || 400,
    businessPrice: data.businessPrice || 800,
  };
  mockFlights.push(newFlight);
  return enrichFlight(newFlight);
}

async function mockUpdateFlight(flightId, data) {
  await delay(400);
  const idx = mockFlights.findIndex((f) => String(f.id) === String(flightId));
  if (idx === -1) throw { response: { status: 404, data: { message: 'Flight not found' } } };
  mockFlights[idx] = { ...mockFlights[idx], ...data, id: mockFlights[idx].id };
  return enrichFlight(mockFlights[idx]);
}

async function mockDeleteFlight(flightId) {
  await delay(300);
  const idx = mockFlights.findIndex((f) => String(f.id) === String(flightId));
  if (idx !== -1) mockFlights.splice(idx, 1);
  return { success: true };
}

function enrichFlight(f) {
  const airline = mockAirlines.find((a) => a.id === f.airlineId);
  const depAirport = mockAirports.find((a) => a.code === f.departureAirportCode);
  const arrAirport = mockAirports.find((a) => a.code === f.arrivalAirportCode);
  const depTime = new Date(`2026-08-21T${f.departureTime}`);
  const arrTime = new Date(`2026-08-21T${f.arrivalTime}`);
  let durationMin = (arrTime - depTime) / 60000;
  if (durationMin < 0) durationMin += 24 * 60; // overnight
  return {
    ...f,
    airlineName: airline ? airline.name : 'Unknown Airline',
    airlineCode: airline ? airline.code : 'XX',
    airlineLogo: airline ? airline.logo : null,
    departureAirportName: depAirport ? depAirport.name : f.departureAirportCode,
    departureCity: depAirport ? depAirport.city : '',
    arrivalAirportName: arrAirport ? arrAirport.name : f.arrivalAirportCode,
    arrivalCity: arrAirport ? arrAirport.city : '',
    duration: formatDuration(durationMin),
    durationMinutes: durationMin,
  };
}
