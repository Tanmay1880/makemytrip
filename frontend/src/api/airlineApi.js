import api from './axiosConfig';

// ============================================================
// AIRLINE API (admin)
// ============================================================

/**
 * Get all airlines.
 *
 * Backend:
 * GET /api/airlines
 */
export async function getAllAirlines() {
  const response = await api.get('/api/airlines');

  return response.data;
}

/**
 * Get airline by ID.
 *
 * Backend:
 * GET /api/airlines/{id}
 */
export async function getAirlineById(airlineId) {
  const response = await api.get(
    `/api/airlines/${airlineId}`
  );

  return response.data;
}

/**
 * Create an airline.
 *
 * Backend:
 * POST /api/airlines
 */
export async function createAirline(airlineData) {
  const response = await api.post(
    '/api/airlines',
    airlineData
  );

  return response.data;
}

/**
 * Update an airline.
 *
 * Backend:
 * PUT /api/airlines/{id}
 */
export async function updateAirline(
  airlineId,
  airlineData
) {
  const response = await api.put(
    `/api/airlines/${airlineId}`,
    airlineData
  );

  return response.data;
}

/**
 * Delete an airline.
 *
 * Backend:
 * DELETE /api/airlines/{id}
 */
export async function deleteAirline(airlineId) {
  await api.delete(
    `/api/airlines/${airlineId}`
  );
}