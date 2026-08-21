import api from './axiosConfig';

// ============================================================
// AIRLINE API (admin)
// ------------------------------------------------------------
// NOTE: Adjust the endpoint paths and field names below to
// match your Spring Boot REST API contract.
// ============================================================

import { mockAirlines } from './mockData';

function delay(ms) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

/**
 * Get all airlines.
 */
export async function getAllAirlines() {
  // TODO: Replace with your actual endpoint
  // const response = await api.get('/airlines');
  // return response.data;

  // --- Placeholder ---
  await delay(400);
  return [...mockAirlines];
}

/**
 * Create an airline.
 * @param {object} airlineData
 */
export async function createAirline(airlineData) {
  // TODO: Replace with your actual endpoint
  // const response = await api.post('/airlines', airlineData);
  // return response.data;

  // --- Placeholder ---
  await delay(400);
  const newAirline = { id: Date.now(), ...airlineData };
  mockAirlines.push(newAirline);
  return newAirline;
}

/**
 * Update an airline.
 * @param {string|number} airlineId
 * @param {object} airlineData
 */
export async function updateAirline(airlineId, airlineData) {
  // TODO: Replace with your actual endpoint
  // const response = await api.put(`/airlines/${airlineId}`, airlineData);
  // return response.data;

  // --- Placeholder ---
  await delay(400);
  const idx = mockAirlines.findIndex((a) => a.id === airlineId);
  if (idx !== -1) {
    mockAirlines[idx] = { ...mockAirlines[idx], ...airlineData, id: airlineId };
    return mockAirlines[idx];
  }
  throw { response: { status: 404, data: { message: 'Airline not found' } } };
}

/**
 * Delete an airline.
 * @param {string|number} airlineId
 */
export async function deleteAirline(airlineId) {
  // TODO: Replace with your actual endpoint
  // await api.delete(`/airlines/${airlineId}`);

  // --- Placeholder ---
  await delay(300);
  const idx = mockAirlines.findIndex((a) => a.id === airlineId);
  if (idx !== -1) mockAirlines.splice(idx, 1);
  return { success: true };
}
