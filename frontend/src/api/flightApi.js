import api from './axiosConfig';

// ============================================================
// FLIGHT API
// ============================================================

/**
 * Search for flights.
 *
 * Frontend passes airport codes (from/to) and date.
 * Backend expects:
 * - departureAirportId
 * - arrivalAirportId
 * - departureDate
 */
export async function searchFlights({ from, to, date }) {

    const airportsResponse = await api.get('/api/airports');

    const airports = airportsResponse.data || [];

    const departureAirportId = resolveAirportId(from, airports);
    const arrivalAirportId = resolveAirportId(to, airports);

    if (departureAirportId == null || arrivalAirportId == null) {
        const error = new Error(
            'One or both airports could not be resolved'
        );

        error.response = {
            status: 400,
            data: {
                message:
                    'Unknown airport code. Ensure airports exist in the backend.'
            }
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

    return (response.data || []).map(
        (flight) => mapFlightResponse(flight, airports)
    );
}

/**
 * Get flight details by ID.
 */
export async function getFlightById(flightId) {
    const response = await api.get(`/api/flights/${flightId}`);
    return response.data;
}

/**
 * Get all flights.
 *
 * Used by admin.
 */
export async function getAllFlights() {
    const response = await api.get('/api/flights');
    return response.data;
}

/**
 * Create a flight.
 *
 * Used by admin.
 */
export async function createFlight(flightData) {
    const response = await api.post('/api/flights', flightData);
    return response.data;
}

/**
 * Update a flight.
 *
 * Used by admin.
 */
export async function updateFlight(flightId, flightData) {
    const response = await api.put(
        `/api/flights/${flightId}`,
        flightData
    );

    return response.data;
}

/**
 * Delete a flight.
 *
 * Used by admin.
 */
export async function deleteFlight(flightId) {
    await api.delete(`/api/flights/${flightId}`);
}

// ============================================================
// HELPERS
// ============================================================

/**
 * Resolve airport code or ID to backend airport ID.
 */
function resolveAirportId(value, airports) {
    if (value == null || value === '') {
        return null;
    }

    const raw = String(value).trim();

    // Numeric airport ID
    if (/^\d+$/.test(raw)) {
        const byId = airports.find(
            (airport) => String(airport.id) === raw
        );

        if (byId) {
            return byId.id;
        }
    }

    // Airport code
    const byCode = airports.find(
        (airport) =>
            airport.code &&
            airport.code.toUpperCase() === raw.toUpperCase()
    );

    return byCode ? byCode.id : null;
}

/**
 * Map backend FlightResponse to the structure expected
 * by FlightCard / FlightResults.
 */
function mapFlightResponse(flight, airports = []) {

    const departureAirport =
        airports.find(
            (airport) => airport.id === flight.departureAirportId
        ) ||
        airports.find(
            (airport) => airport.code === flight.departureAirportCode
        );

    const arrivalAirport =
        airports.find(
            (airport) => airport.id === flight.arrivalAirportId
        ) ||
        airports.find(
            (airport) => airport.code === flight.arrivalAirportCode
        );

    const departureTime = new Date(flight.departureTime);
    const arrivalTime = new Date(flight.arrivalTime);

    let durationMinutes =
        (arrivalTime - departureTime) / 60000;

    if (Number.isNaN(durationMinutes)) {
        durationMinutes = 0;
    } else if (durationMinutes < 0) {
        durationMinutes += 24 * 60;
    }

    return {
        ...flight,

        economySeats:
            flight.economySeatsAvailable ??
            flight.economySeats ??
            0,

        premiumEconomySeats:
            flight.premiumEconomySeatsAvailable ??
            flight.premiumEconomySeats ??
            0,

        businessSeats:
            flight.businessSeatsAvailable ??
            flight.businessSeats ??
            0,

        departureCity:
            departureAirport?.city || '',

        arrivalCity:
            arrivalAirport?.city || '',

        duration:
            formatDuration(Math.round(durationMinutes)),

        durationMinutes,
    };
}

/**
 * Convert minutes into human-readable duration.
 */
function formatDuration(minutes) {
    const hours = Math.floor(minutes / 60);
    const remainingMinutes = minutes % 60;

    return `${hours}h ${remainingMinutes
        .toString()
        .padStart(2, '0')}m`;
}