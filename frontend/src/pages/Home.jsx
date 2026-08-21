import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Search,
  Calendar,
  Plane,
  PlaneTakeoff,
  PlaneLanding,
  TrendingUp,
  ArrowRight,
} from 'lucide-react';

import { getAllAirports } from '@/api/airportApi';

// ============================================================
// POPULAR DESTINATIONS
// These must match airports that actually exist in the database.
// ============================================================

const popularDestinations = [
  {
    code: 'DEL',
    city: 'Delhi',
    country: 'India',
    description: 'Indira Gandhi International Airport',
    image:
      'https://commons.wikimedia.org/wiki/Special:FilePath/Delhi_India_Gate.jpg',
  },
  {
    code: 'BOM',
    city: 'Mumbai',
    country: 'India',
    description: 'Chhatrapati Shivaji Maharaj International Airport',
    image:
      'https://s1.dmcdn.net/v/K9ZjU1P5FwdYQgNEc/x1080',
  },
  {
    code: 'BLR',
    city: 'Bengaluru',
    country: 'India',
    description: 'Kempegowda International Airport',
    image:
      'https://images.ctfassets.net/bx9krvy0u3sx/3LSoyEz8WrwSg9KQ84Rgp4/655666c28639fb66310f90e677250e46/Bengaluru_aerial_shot.png?fm=webp&q=80&w=1600',
  },
  {
    code: 'HYD',
    city: 'Hyderabad',
    country: 'India',
    description: 'Rajiv Gandhi International Airport',
    image:
      'https://staybook.in/_next/image?q=100&url=https%3A%2F%2Fimages.staybook.in%2Fthings-to-do%2Fcharminar-fast-entry-pass-flexible-timings%2F5.jpg&w=1920',
  },
  {
    code: 'MAA',
    city: 'Chennai',
    country: 'India',
    description: 'Chennai International Airport',
    image:
      'https://www.agoda.com/wp-content/uploads/2024/05/Marina-Beach-Bay-of-bengal-view-from-light-house.jpg',
  },
  {
    code: 'CCU',
    city: 'Kolkata',
    country: 'India',
    description: 'Netaji Subhas Chandra Bose International Airport',
    image:
      'https://commons.wikimedia.org/wiki/Special:FilePath/The_Victoria_Memorial_Hall_Kolkata_West_Bengal_India.jpg',
  },
];

// ============================================================
// HOME
// ============================================================

export default function Home() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    from: '',
    to: '',
    date: '',
  });

  const [errors, setErrors] = useState({});

  // ============================================================
  // AIRPORTS
  // ============================================================

  const [airports, setAirports] = useState([]);
  const [loadingAirports, setLoadingAirports] = useState(true);
  const [airportError, setAirportError] = useState('');

  useEffect(() => {
    const loadAirports = async () => {
      try {
        const data = await getAllAirports();

        setAirports(Array.isArray(data) ? data : []);
      } catch (error) {
        console.error('Failed to load airports:', error);
        setAirportError('Unable to load airports');
      } finally {
        setLoadingAirports(false);
      }
    };

    loadAirports();
  }, []);

  // ============================================================
  // FORM
  // ============================================================

  const handleChange = (e) => {
    const { name, value } = e.target;

    setForm((previous) => ({
      ...previous,
      [name]: value,
    }));

    if (errors[name]) {
      setErrors((previous) => ({
        ...previous,
        [name]: undefined,
      }));
    }
  };

  // ============================================================
  // SEARCH
  // ============================================================

  const handleSearch = (e) => {
    e.preventDefault();

    const errs = {};

    if (!form.from) {
      errs.from = 'Required';
    }

    if (!form.to) {
      errs.to = 'Required';
    }

    if (!form.date) {
      errs.date = 'Required';
    }

    if (form.from && form.to && form.from === form.to) {
      errs.to = 'Destination must differ from origin';
    }

    setErrors(errs);

    if (Object.keys(errs).length > 0) {
      return;
    }

    const params = new URLSearchParams({
      from: form.from,
      to: form.to,
      date: form.date,
    });

    navigate(`/flights?${params.toString()}`);
  };

  // ============================================================
  // POPULAR DESTINATION
  // ============================================================

  const selectDestination = (destination) => {
    // Make sure selected destination exists in our airport data.
    const exists = airports.some(
      (airport) => airport.code === destination.code
    );

    if (!exists) {
      console.error(
        `Airport ${destination.code} does not exist in the database`
      );
      return;
    }

    // Set destination in search form.
    setForm((previous) => ({
      ...previous,
      to: destination.code,
    }));

    // Clear destination validation error.
    setErrors((previous) => ({
      ...previous,
      to: undefined,
    }));

    // If origin and date are already selected,
    // immediately search for real flights.
    if (
      form.from &&
      form.from !== destination.code &&
      form.date
    ) {
      const params = new URLSearchParams({
        from: form.from,
        to: destination.code,
        date: form.date,
      });

      navigate(`/flights?${params.toString()}`);
      return;
    }

    // Otherwise bring the user back to the search form
    // so they can select origin/date.
    document
      .getElementById('flight-search')
      ?.scrollIntoView({
        behavior: 'smooth',
        block: 'center',
      });
  };

  // ============================================================
  // UI
  // ============================================================

  return (
    <div>

      {/* ========================================================
          HERO
      ======================================================== */}

      <section className="relative">

        <div className="absolute inset-0">

          <img
            src="https://images.pexels.com/photos/18136344/pexels-photo-18136344.jpeg?auto=compress&cs=tinysrgb&h=650&w=940"
            alt="Sky view from airplane"
            className="w-full h-full object-cover"
          />

          <div className="absolute inset-0 bg-gradient-to-b from-primary-900/70 via-primary-800/60 to-gray-900/80" />

        </div>

        <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 pt-20 pb-28">

          <div className="text-center mb-10">

            <h1 className="text-4xl md:text-5xl font-bold text-white tracking-tight">
              Fly anywhere, anytime
            </h1>

            <p className="text-lg text-primary-100 mt-3">
              Search and book flights from hundreds of airlines worldwide
            </p>

          </div>

          {/* ====================================================
              SEARCH CARD
          ==================================================== */}

          <form
            id="flight-search"
            onSubmit={handleSearch}
            className="max-w-4xl mx-auto bg-white rounded-2xl shadow-xl p-6 md:p-8"
          >

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">

              {/* FROM */}

              <div>

                <label className="label flex items-center gap-1.5">

                  <PlaneTakeoff className="w-4 h-4 text-primary-600" />

                  From

                </label>

                <select
                  name="from"
                  value={form.from}
                  onChange={handleChange}
                  disabled={loadingAirports}
                  className={`input ${
                    errors.from ? 'border-error-400' : ''
                  }`}
                >

                  <option value="">
                    {loadingAirports
                      ? 'Loading airports...'
                      : 'Select origin'}
                  </option>

                  {airports.map((airport) => (

                    <option
                      key={airport.id}
                      value={airport.code}
                    >
                      {airport.code} - {airport.city}
                      {airport.country
                        ? `, ${airport.country}`
                        : ''}
                    </option>

                  ))}

                </select>

                {errors.from && (
                  <p className="text-xs text-error-500 mt-1">
                    {errors.from}
                  </p>
                )}

              </div>

              {/* TO */}

              <div>

                <label className="label flex items-center gap-1.5">

                  <PlaneLanding className="w-4 h-4 text-primary-600" />

                  To

                </label>

                <select
                  name="to"
                  value={form.to}
                  onChange={handleChange}
                  disabled={loadingAirports}
                  className={`input ${
                    errors.to ? 'border-error-400' : ''
                  }`}
                >

                  <option value="">
                    {loadingAirports
                      ? 'Loading airports...'
                      : 'Select destination'}
                  </option>

                  {airports.map((airport) => (

                    <option
                      key={airport.id}
                      value={airport.code}
                    >
                      {airport.code} - {airport.city}
                      {airport.country
                        ? `, ${airport.country}`
                        : ''}
                    </option>

                  ))}

                </select>

                {errors.to && (
                  <p className="text-xs text-error-500 mt-1">
                    {errors.to}
                  </p>
                )}

              </div>

              {/* DATE */}

              <div>

                <label className="label flex items-center gap-1.5">

                  <Calendar className="w-4 h-4 text-primary-600" />

                  Departure Date

                </label>

                <input
                  type="date"
                  name="date"
                  value={form.date}
                  onChange={handleChange}
                  min={new Date().toISOString().split('T')[0]}
                  className={`input ${
                    errors.date ? 'border-error-400' : ''
                  }`}
                />

                {errors.date && (
                  <p className="text-xs text-error-500 mt-1">
                    {errors.date}
                  </p>
                )}

              </div>

            </div>

            {/* AIRPORT ERROR */}

            {airportError && (
              <p className="text-sm text-error-500 mt-3">
                {airportError}
              </p>
            )}

            {/* SEARCH BUTTON */}

            <button
              type="submit"
              disabled={
                loadingAirports ||
                airports.length === 0
              }
              className="btn-primary w-full mt-6 py-3 text-base"
            >

              <Search className="w-5 h-5" />

              Search Flights

            </button>

          </form>

        </div>

      </section>

      {/* ========================================================
          POPULAR DESTINATIONS
      ======================================================== */}

      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-14">

        <div className="flex items-center gap-2 mb-7">

          <TrendingUp className="w-5 h-5 text-accent-500" />

          <h2 className="text-2xl font-bold text-gray-900">
            Popular Destinations
          </h2>

        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-5">

          {popularDestinations.map((destination) => (

            <button
              key={destination.code}
              type="button"
              onClick={() =>
                selectDestination(destination)
              }
              className="group text-left bg-white rounded-xl overflow-hidden border border-gray-200 shadow-sm hover:shadow-md hover:border-primary-300 transition-all"
            >

              <div className="relative h-40 overflow-hidden">

                <img
                  src={destination.image}
                  alt={destination.city}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                />

                <div className="absolute inset-0 bg-gradient-to-t from-black/65 via-black/10 to-transparent" />

                <div className="absolute bottom-0 left-0 right-0 p-4">

                  <h3 className="text-xl font-bold text-white">
                    {destination.city}
                  </h3>

                  <p className="text-sm text-gray-200">
                    {destination.code} · {destination.country}
                  </p>

                </div>

              </div>

              <div className="px-4 py-3 flex items-center justify-between">

                <div>

                  <p className="text-sm text-gray-500">
                    {destination.description}
                  </p>

                  <p className="text-sm font-medium text-primary-600 mt-1">
                    Search flights
                  </p>

                </div>

                <ArrowRight className="w-5 h-5 text-primary-600 group-hover:translate-x-1 transition-transform" />

              </div>

            </button>

          ))}

        </div>

      </section>

      {/* ========================================================
          FEATURES
      ======================================================== */}

      <section className="bg-gray-900 py-16">

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">

            {[
              {
                icon: Search,
                title: 'Best Prices',
                desc: 'Compare fares across hundreds of airlines instantly.',
              },
              {
                icon: Plane,
                title: 'Global Network',
                desc: 'Fly to over 500 destinations worldwide with ease.',
              },
              {
                icon: Calendar,
                title: 'Easy Booking',
                desc: 'Book in minutes with our streamlined process.',
              },
            ].map((feature) => (

              <div
                key={feature.title}
                className="text-center"
              >

                <div className="w-14 h-14 rounded-xl bg-primary-600/20 flex items-center justify-center mx-auto mb-4">

                  <feature.icon className="w-7 h-7 text-primary-400" />

                </div>

                <h3 className="text-lg font-semibold text-white mb-2">
                  {feature.title}
                </h3>

                <p className="text-sm text-gray-400">
                  {feature.desc}
                </p>

              </div>

            ))}

          </div>

        </div>

      </section>

    </div>
  );
}