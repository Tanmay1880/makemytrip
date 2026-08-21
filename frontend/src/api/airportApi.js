import api from './axiosConfig';

// ============================================================
// AIRPORT API (admin)
// ------------------------------------------------------------
// NOTE: Adjust the endpoint paths and field names below to
// match your Spring Boot REST API contract.
// ============================================================

import { mockAirports } from './mockData';

function delay(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

/**
 * Get all airports.
 */
export async function getAllAirports() {
  // TODO: Replace with your actual endpoint
  // const response = await api.get('/airports');
  // return response.data;

  // --- Placeholder ---
  await delay(400);
  return [...mockAirports];
}

/**
 * Create an airport.
 * @param {object} airportData
 */
export async function createAirport(airportData) {
  // TODO: Replace with your actual endpoint
  // const response = await api.post('/airports', airportData);
  // return response.data;

  // --- Placeholder ---
  await delay(400);
  const newAirport = { ...airportData };
  mockAirports.push(newAirport);
  return newAirport;
}

/**
 * Update an airport.
 * @param {string} airportCode
 * @param {object} airportData
 */
export async function updateAirport(airportCode, airportData) {
  // TODO: Replace with your actual endpoint
  // const response = await api.put(`/airports/${airportCode}`, airportData);
  // return response.data;

  // --- Placeholder ---
  await delay(400);
  const idx = mockAirports.findIndex((a) => a.code === airportCode);
  if (idx !== -1) {
    mockAirports[idx] = { ...mockAirports[idx], ...airportData };
    return mockAirports[idx];
  }
  throw { response: { status: 404, data: { message: 'Airport not found' } } };
}

/**
 * Delete an airport.
 * @param {string} airportCode
 */
export async function deleteAirport(airportCode) {
  // TODO: Replace with your actual endpoint
  // await api.delete(`/airports/${airportCode}`);

  // --- Placeholder ---
  await delay(300);
  const idx = mockAirports.findIndex((a) => a.code === airportCode);
  if (idx !== -1) mockAirports.splice(idx, 1);
  return { success: true };
}
