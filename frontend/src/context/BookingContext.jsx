// ============================================================
// BOOKING CONTEXT — holds in-flight booking state across pages
// ============================================================

import { createContext, useContext, useState, useCallback } from 'react';

const BookingContext = createContext(null);

export function BookingProvider({ children }) {
  const [selectedFlight, setSelectedFlight] = useState(null);
  const [selectedSeatClass, setSelectedSeatClass] = useState('ECONOMY');
  const [passengerCount, setPassengerCount] = useState(1);
  const [passengers, setPassengers] = useState([]);
  const [currentBooking, setCurrentBooking] = useState(null);

  const reset = useCallback(() => {
    setSelectedFlight(null);
    setSelectedSeatClass('ECONOMY');
    setPassengerCount(1);
    setPassengers([]);
    setCurrentBooking(null);
  }, []);

  const value = {
    selectedFlight,
    setSelectedFlight,
    selectedSeatClass,
    setSelectedSeatClass,
    passengerCount,
    setPassengerCount,
    passengers,
    setPassengers,
    currentBooking,
    setCurrentBooking,
    reset,
  };

  return <BookingContext.Provider value={value}>{children}</BookingContext.Provider>;
}

export function useBooking() {
  const ctx = useContext(BookingContext);
  if (!ctx) throw new Error('useBooking must be used within BookingProvider');
  return ctx;
}
