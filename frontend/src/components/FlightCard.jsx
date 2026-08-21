import { Plane, Clock } from 'lucide-react';
import { formatCurrency, formatTime, getSeatClassLabel } from '@/utils/formatters';

export default function FlightCard({ flight, onSelect, selectLabel = 'Select Flight' }) {
  return (
    <div className="card p-5 hover:shadow-md transition-shadow group">
      <div className="flex flex-col lg:flex-row lg:items-center gap-4">
        {/* Airline info */}
        <div className="flex items-center gap-3 lg:w-48 flex-shrink-0">
          <div className="w-11 h-11 rounded-lg bg-primary-100 flex items-center justify-center flex-shrink-0">
            <Plane className="w-5 h-5 text-primary-700 -rotate-45" />
          </div>
          <div>
            <p className="font-semibold text-gray-900 text-sm">{flight.airlineName}</p>
            <p className="text-xs text-gray-500">{flight.flightNumber}</p>
          </div>
        </div>

        {/* Route */}
        <div className="flex items-center gap-3 flex-1 min-w-0">
          <div className="text-center">
            <p className="text-lg font-bold text-gray-900">{formatTime(flight.departureTime)}</p>
            <p className="text-xs text-gray-500 font-medium">{flight.departureAirportCode}</p>
            <p className="text-xs text-gray-400 truncate">{flight.departureCity}</p>
          </div>

          <div className="flex-1 flex flex-col items-center px-2">
            <div className="flex items-center w-full">
              <div className="flex-1 h-px bg-gray-300" />
              <Plane className="w-4 h-4 text-gray-400 mx-1 -rotate-45" />
              <div className="flex-1 h-px bg-gray-300" />
            </div>
            <div className="flex items-center gap-1 mt-1 text-xs text-gray-500">
              <Clock className="w-3 h-3" />
              {flight.duration}
            </div>
          </div>

          <div className="text-center">
            <p className="text-lg font-bold text-gray-900">{formatTime(flight.arrivalTime)}</p>
            <p className="text-xs text-gray-500 font-medium">{flight.arrivalAirportCode}</p>
            <p className="text-xs text-gray-400 truncate">{flight.arrivalCity}</p>
          </div>
        </div>

        {/* Price + action */}
        <div className="flex items-center justify-between gap-4 lg:w-auto lg:flex-col lg:items-end">
          <div className="text-left lg:text-right">
            <p className="text-xs text-gray-400">From</p>
            <p className="text-xl font-bold text-primary-700">{formatCurrency(flight.economyPrice)}</p>
            <p className="text-xs text-gray-500">per person</p>
          </div>
          <button
            onClick={() => onSelect(flight)}
            className="btn-primary whitespace-nowrap"
          >
            {selectLabel}
          </button>
        </div>
      </div>

      {/* Seat class prices */}
      <div className="mt-4 pt-4 border-t border-gray-100 grid grid-cols-3 gap-2">
        <SeatClassBadge label="Economy" price={flight.economyPrice} seats={flight.economySeats} />
        <SeatClassBadge label="Premium" price={flight.premiumEconomyPrice} seats={flight.premiumEconomySeats} />
        <SeatClassBadge label="Business" price={flight.businessPrice} seats={flight.businessSeats} />
      </div>
    </div>
  );
}

function SeatClassBadge({ label, price, seats }) {
  return (
    <div className="text-center px-2 py-1.5 rounded-lg bg-gray-50">
      <p className="text-xs font-medium text-gray-600">{label}</p>
      <p className="text-sm font-semibold text-gray-900">{formatCurrency(price)}</p>
      <p className={`text-xs ${seats > 10 ? 'text-success-600' : seats > 0 ? 'text-warning-600' : 'text-error-600'}`}>
        {seats} seats
      </p>
    </div>
  );
}
