import api from './axiosConfig';

// ============================================================
// AIRPORT API
// ============================================================

/**
 * Get all airports.
 *
 * Used by:
 * - Home airport dropdowns
 * - Flight search
 */
export async function getAllAirports() {
  const response = await api.get('/api/airports');
  return response.data;
}

/**
 * Create an airport (admin).
 */
export async function createAirport(airportData) {
  const response = await api.post('/api/airports', airportData);
  return response.data;
}

/**
 * Update an airport (admin).
 */
export async function updateAirport(airportId, airportData) {
  const response = await api.put(`/api/airports/${airportId}`, airportData);
  return response.data;
}

/**
 * Delete an airport (admin).
 */
export async function deleteAirport(airportId) {
  await api.delete(`/api/airports/${airportId}`);
}