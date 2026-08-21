import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Search,
  Calendar,
  ArrowRight,
  Plane,
  PlaneTakeoff,
  PlaneLanding,
  TrendingUp,
} from 'lucide-react';

import { getAllAirports } from '@/api/airportApi';

const popularDestinations = [
  {
    code: 'CDG',
    city: 'Paris',
    country: 'France',
    image:
      'https://images.pexels.com/photos/32444545/pexels-photo-32444545.jpeg?auto=compress&cs=tinysrgb&h=650&w=940',
    price: 390,
  },
  {
    code: 'JFK',
    city: 'New York',
    country: 'USA',
    image:
      'https://images.pexels.com/photos/8569166/pexels-photo-8569166.jpeg?auto=compress&cs=tinysrgb&h=650&w=940',
    price: 450,
  },
  {
    code: 'DXB',
    city: 'Dubai',
    country: 'UAE',
    image:
      'https://images.pexels.com/photos/19664340/pexels-photo-19664340.jpeg?auto=compress&cs=tinysrgb&h=650&w=940',
    price: 620,
  },
  {
    code: 'LHR',
    city: 'London',
    country: 'UK',
    image:
      'https://images.pexels.com/photos/16771428/pexels-photo-16771428.png?auto=compress&cs=tinysrgb&h=650&w=940',
    price: 420,
  },
  {
    code: 'SIN',
    city: 'Singapore',
    country: 'Singapore',
    image:
      'https://images.pexels.com/photos/15480459/pexels-photo-15480459.jpeg?auto=compress&cs=tinysrgb&h=650&w=940',
    price: 550,
  },
  {
    code: 'HND',
    city: 'Tokyo',
    country: 'Japan',
    image:
      'https://images.pexels.com/photos/15275312/pexels-photo-15275312.jpeg?auto=compress&cs=tinysrgb&h=650&w=940',
    price: 590,
  },
];

export default function Home() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    from: '',
    to: '',
    date: '',
  });

  const [errors, setErrors] = useState({});

  // ==================== AIRPORTS ====================

  const [airports, setAirports] = useState([]);
  const [loadingAirports, setLoadingAirports] = useState(true);
  const [airportError, setAirportError] = useState('');

  useEffect(() => {
    const loadAirports = async () => {
      try {
        const data = await getAllAirports();
        setAirports(data || []);
      } catch (error) {
        console.error('Failed to load airports:', error);
        setAirportError('Unable to load airports');
      } finally {
        setLoadingAirports(false);
      }
    };

    loadAirports();
  }, []);

  // ==================== FORM ====================

  const handleChange = (e) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });

    if (errors[e.target.name]) {
      setErrors({
        ...errors,
        [e.target.name]: undefined,
      });
    }
  };

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

  // ==================== POPULAR DESTINATION ====================

  const selectDestination = (destination) => {
    const newForm = {
      ...form,
      to: destination.code,
    };

    setForm(newForm);

    if (
      newForm.from &&
      newForm.from !== destination.code &&
      newForm.date
    ) {
      const params = new URLSearchParams({
        from: newForm.from,
        to: destination.code,
        date: newForm.date,
      });

      navigate(`/flights?${params.toString()}`);
    }
  };

  // ==================== UI ====================

  return (
    <div>

      {/* ==================== HERO ==================== */}

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

          {/* ==================== SEARCH CARD ==================== */}

          <form
            onSubmit={handleSearch}
            className="max-w-4xl mx-auto bg-white rounded-2xl shadow-xl p-6 md:p-8"
          >

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4">

              {/* ==================== FROM ==================== */}

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

              {/* ==================== TO ==================== */}

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

              {/* ==================== DATE ==================== */}

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

            {/* Airport error */}

            {airportError && (
              <p className="text-sm text-error-500 mt-3">
                {airportError}
              </p>
            )}

            <button
              type="submit"
              disabled={loadingAirports || airports.length === 0}
              className="btn-primary w-full mt-6 py-3 text-base"
            >
              <Search className="w-5 h-5" />
              Search Flights
            </button>

          </form>

        </div>

      </section>

      {/* ==================== POPULAR DESTINATIONS ==================== */}

      <section className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">

        <div className="flex items-center gap-2 mb-8">

          <TrendingUp className="w-5 h-5 text-accent-500" />

          <h2 className="text-2xl font-bold text-gray-900">
            Popular Destinations
          </h2>

        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">

          {popularDestinations.map((dest) => (

            <button
              key={dest.code}
              onClick={() => selectDestination(dest)}
              className="group text-left rounded-xl overflow-hidden shadow-sm border border-gray-200 hover:shadow-lg transition-all"
            >

              <div className="relative h-52 overflow-hidden">

                <img
                  src={dest.image}
                  alt={dest.city}
                  className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500"
                />

                <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent" />

                <div className="absolute bottom-0 left-0 right-0 p-4">

                  <h3 className="text-xl font-bold text-white">
                    {dest.city}
                  </h3>

                  <p className="text-sm text-gray-200">
                    {dest.country}
                  </p>

                </div>

              </div>

              <div className="p-4 flex items-center justify-between bg-white">

                <div>
                  <p className="text-xs text-gray-400">
                    From
                  </p>

                  <p className="text-lg font-bold text-primary-700">
                    ${dest.price}
                  </p>
                </div>

                <span className="flex items-center gap-1 text-sm font-medium text-primary-600 group-hover:gap-2 transition-all">
                  Book now
                  <ArrowRight className="w-4 h-4" />
                </span>

              </div>

            </button>

          ))}

        </div>

      </section>

      {/* ==================== FEATURES ==================== */}

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