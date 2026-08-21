import api from './axiosConfig';

// ============================================================
// FLIGHT API
// ------------------------------------------------------------
// NOTE: Adjust the endpoint paths and field names below to
// match your Spring Boot REST API contract.
// ============================================================

/**
 * Search for flights.
 * @param {{ from: string, to: string, date: string }} params
 * @returns {Promise<Array>}
 */
export async function searchFlights(params) {
  // TODO: Replace with your actual endpoint
  // const response = await api.get('/flights/search', { params });
  // return response.data;

  // --- Placeholder ---
  return mockSearchFlights(params);
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

async function mockSearchFlights({ from, to, date }) {
  await delay(800);
  let results = mockFlights.filter(
    (f) =>
      (!from || f.departureAirportCode === from) &&
      (!to || f.arrivalAirportCode === to)
  );
  // If no exact match, return all flights so the UI is demonstrable
  if (results.length === 0) {
    results = [...mockFlights];
  }
  return results.map(enrichFlight);
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
