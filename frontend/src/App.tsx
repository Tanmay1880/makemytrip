import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";

import MainLayout from "@/layouts/MainLayout";
import AdminLayout from "@/layouts/AdminLayout";

import { AuthProvider } from "@/context/AuthContext";
import { BookingProvider } from "@/context/BookingContext";
import { ToastProvider } from "@/context/ToastContext";

import ProtectedRoute from "@/components/ProtectedRoute";

import Home from "@/pages/Home";
import Login from "@/pages/Login";
import Register from "@/pages/Register";
import FlightResults from "@/pages/FlightResults";
import FlightDetails from "@/pages/FlightDetails";
import PassengerBooking from "@/pages/PassengerBooking";
import Payment from "@/pages/Payment";
import MyBookings from "@/pages/MyBookings";
import Profile from "@/pages/Profile";

import AdminDashboard from "@/pages/admin/AdminDashboard";
import AdminFlights from "@/pages/admin/AdminFlights";
import AdminAirlines from "@/pages/admin/AdminAirlines";
import AdminAirports from "@/pages/admin/AdminAirports";

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <BookingProvider>
          <ToastProvider>
            <Routes>
              {/* ==================== PUBLIC / USER ROUTES ==================== */}

              <Route element={<MainLayout />}>
                <Route path="/" element={<Home />} />

                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />

                <Route path="/flights" element={<FlightResults />} />
                <Route path="/flights/:id" element={<FlightDetails />} />

                <Route
                  path="/booking"
                  element={
                    <ProtectedRoute>
                      <PassengerBooking />
                    </ProtectedRoute>
                  }
                />

                <Route
                  path="/payment"
                  element={
                    <ProtectedRoute>
                      <Payment />
                    </ProtectedRoute>
                  }
                />

                <Route
                  path="/bookings"
                  element={
                    <ProtectedRoute>
                      <MyBookings />
                    </ProtectedRoute>
                  }
                />

                <Route
                  path="/profile"
                  element={
                    <ProtectedRoute>
                      <Profile />
                    </ProtectedRoute>
                  }
                />
              </Route>

              {/* ==================== ADMIN ROUTES ==================== */}

              <Route
                element={
                  <ProtectedRoute adminOnly>
                    <AdminLayout />
                  </ProtectedRoute>
                }
              >
                <Route path="/admin" element={<AdminDashboard />} />
                <Route path="/admin/flights" element={<AdminFlights />} />
                <Route path="/admin/airlines" element={<AdminAirlines />} />
                <Route path="/admin/airports" element={<AdminAirports />} />
              </Route>

              {/* ==================== FALLBACK ==================== */}

              <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </ToastProvider>
        </BookingProvider>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
