import { useState, useEffect } from 'react';
import { Plus, Pencil, Trash2, PlaneTakeoff, Search } from 'lucide-react';
import { getAllFlights, createFlight, updateFlight, deleteFlight } from '@/api/flightApi';
import { getAllAirlines } from '@/api/airlineApi';
import { getAllAirports } from '@/api/airportApi';
import { useToast } from '@/context/ToastContext';
import PageHeader from '@/components/PageHeader';
import Modal from '@/components/Modal';
import { formatCurrency, formatTime } from '@/utils/formatters';

const emptyForm = {
  flightNumber: '',
  airlineId: '',
  departureAirportCode: '',
  arrivalAirportCode: '',
  departureTime: '',
  arrivalTime: '',
  economyPrice: '',
  premiumEconomyPrice: '',
  businessPrice: '',
  economySeats: '',
  premiumEconomySeats: '',
  businessSeats: '',
};

export default function AdminFlights() {
  const { toast } = useToast();
  const [flights, setFlights] = useState([]);
  const [airlines, setAirlines] = useState([]);
  const [airports, setAirports] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);
  const [search, setSearch] = useState('');

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [f, a, ap] = await Promise.all([getAllFlights(), getAllAirlines(), getAllAirports()]);
      setFlights(f);
      setAirlines(a);
      setAirports(ap);
    } catch {
      toast.error('Failed to load flight data');
    } finally {
      setLoading(false);
    }
  };

  const openCreate = () => {
    setEditing(null);
    setForm(emptyForm);
    setModalOpen(true);
  };

  const openEdit = (flight) => {
    setEditing(flight);
    setForm({
      flightNumber: flight.flightNumber,
      airlineId: flight.airlineId,
      departureAirportCode: flight.departureAirportCode,
      arrivalAirportCode: flight.arrivalAirportCode,
      departureTime: flight.departureTime,
      arrivalTime: flight.arrivalTime,
      economyPrice: flight.economyPrice,
      premiumEconomyPrice: flight.premiumEconomyPrice,
      businessPrice: flight.businessPrice,
      economySeats: flight.economySeats,
      premiumEconomySeats: flight.premiumEconomySeats,
      businessSeats: flight.businessSeats,
    });
    setModalOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    const payload = {
      ...form,
      airlineId: Number(form.airlineId),
      economyPrice: Number(form.economyPrice),
      premiumEconomyPrice: Number(form.premiumEconomyPrice),
      businessPrice: Number(form.businessPrice),
      economySeats: Number(form.economySeats),
      premiumEconomySeats: Number(form.premiumEconomySeats),
      businessSeats: Number(form.businessSeats),
    };
    setSaving(true);
    try {
      if (editing) {
        await updateFlight(editing.id, payload);
        toast.success('Flight updated successfully');
      } else {
        await createFlight(payload);
        toast.success('Flight created successfully');
      }
      setModalOpen(false);
      loadData();
    } catch {
      toast.error('Failed to save flight');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (flightId) => {
    if (!confirm('Are you sure you want to delete this flight?')) return;
    try {
      await deleteFlight(flightId);
      toast.success('Flight deleted');
      loadData();
    } catch {
      toast.error('Failed to delete flight');
    }
  };

  const filtered = flights.filter(
    (f) =>
      !search ||
      f.flightNumber?.toLowerCase().includes(search.toLowerCase()) ||
      f.airlineName?.toLowerCase().includes(search.toLowerCase()) ||
      f.departureAirportCode?.toLowerCase().includes(search.toLowerCase()) ||
      f.arrivalAirportCode?.toLowerCase().includes(search.toLowerCase())
  );

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <div className="w-8 h-8 border-2 border-primary-600 border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  return (
    <div>
      <PageHeader
        title="Flight Management"
        subtitle="Manage all flights in the system"
        action={
          <button onClick={openCreate} className="btn-primary">
            <Plus className="w-4 h-4" /> Add Flight
          </button>
        }
      />

      {/* Search */}
      <div className="relative mb-4 max-w-sm">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-gray-400" />
        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search flights..."
          className="input pl-10"
        />
      </div>

      {/* Table */}
      <div className="card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-500">
              <tr>
                <th className="text-left px-4 py-3 font-medium">Flight #</th>
                <th className="text-left px-4 py-3 font-medium">Airline</th>
                <th className="text-left px-4 py-3 font-medium">Route</th>
                <th className="text-left px-4 py-3 font-medium">Time</th>
                <th className="text-left px-4 py-3 font-medium">Economy Price</th>
                <th className="text-left px-4 py-3 font-medium">Seats</th>
                <th className="text-right px-4 py-3 font-medium">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {filtered.map((f) => (
                <tr key={f.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3 font-medium text-gray-900">{f.flightNumber}</td>
                  <td className="px-4 py-3 text-gray-600">{f.airlineName}</td>
                  <td className="px-4 py-3 text-gray-600">
                    {f.departureAirportCode} → {f.arrivalAirportCode}
                  </td>
                  <td className="px-4 py-3 text-gray-600">
                    {formatTime(f.departureTime)} - {formatTime(f.arrivalTime)}
                  </td>
                  <td className="px-4 py-3 font-medium text-gray-900">{formatCurrency(f.economyPrice)}</td>
                  <td className="px-4 py-3 text-gray-600">{f.economySeats}</td>
                  <td className="px-4 py-3">
                    <div className="flex items-center justify-end gap-1">
                      <button onClick={() => openEdit(f)} className="p-2 rounded-lg hover:bg-gray-100 text-gray-500">
                        <Pencil className="w-4 h-4" />
                      </button>
                      <button onClick={() => handleDelete(f.id)} className="p-2 rounded-lg hover:bg-error-50 text-error-500">
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {filtered.length === 0 && (
          <div className="p-8 text-center text-sm text-gray-500">No flights found</div>
        )}
      </div>

      {/* Modal */}
      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editing ? 'Edit Flight' : 'Add Flight'} maxWidth="max-w-2xl">
        <form onSubmit={handleSave} className="space-y-4">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="label">Flight Number</label>
              <input
                type="text"
                required
                value={form.flightNumber}
                onChange={(e) => setForm({ ...form, flightNumber: e.target.value })}
                className="input"
                placeholder="SJ201"
              />
            </div>
            <div>
              <label className="label">Airline</label>
              <select
                required
                value={form.airlineId}
                onChange={(e) => setForm({ ...form, airlineId: e.target.value })}
                className="input"
              >
                <option value="">Select airline</option>
                {airlines.map((a) => (
                  <option key={a.id} value={a.id}>{a.name}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="label">Departure Airport</label>
              <select
                required
                value={form.departureAirportCode}
                onChange={(e) => setForm({ ...form, departureAirportCode: e.target.value })}
                className="input"
              >
                <option value="">Select airport</option>
                {airports.map((a) => (
                  <option key={a.code} value={a.code}>{a.code} - {a.city}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="label">Arrival Airport</label>
              <select
                required
                value={form.arrivalAirportCode}
                onChange={(e) => setForm({ ...form, arrivalAirportCode: e.target.value })}
                className="input"
              >
                <option value="">Select airport</option>
                {airports.map((a) => (
                  <option key={a.code} value={a.code}>{a.code} - {a.city}</option>
                ))}
              </select>
            </div>
            <div>
              <label className="label">Departure Time</label>
              <input
                type="time"
                required
                value={form.departureTime}
                onChange={(e) => setForm({ ...form, departureTime: e.target.value })}
                className="input"
              />
            </div>
            <div>
              <label className="label">Arrival Time</label>
              <input
                type="time"
                required
                value={form.arrivalTime}
                onChange={(e) => setForm({ ...form, arrivalTime: e.target.value })}
                className="input"
              />
            </div>
          </div>

          <div className="pt-2 border-t border-gray-100">
            <p className="text-sm font-medium text-gray-700 mb-3">Pricing & Seats</p>
            <div className="grid grid-cols-3 gap-3">
              <div>
                <label className="label">Economy Price</label>
                <input type="number" required value={form.economyPrice} onChange={(e) => setForm({ ...form, economyPrice: e.target.value })} className="input" />
              </div>
              <div>
                <label className="label">Premium Price</label>
                <input type="number" required value={form.premiumEconomyPrice} onChange={(e) => setForm({ ...form, premiumEconomyPrice: e.target.value })} className="input" />
              </div>
              <div>
                <label className="label">Business Price</label>
                <input type="number" required value={form.businessPrice} onChange={(e) => setForm({ ...form, businessPrice: e.target.value })} className="input" />
              </div>
              <div>
                <label className="label">Economy Seats</label>
                <input type="number" required value={form.economySeats} onChange={(e) => setForm({ ...form, economySeats: e.target.value })} className="input" />
              </div>
              <div>
                <label className="label">Premium Seats</label>
                <input type="number" required value={form.premiumEconomySeats} onChange={(e) => setForm({ ...form, premiumEconomySeats: e.target.value })} className="input" />
              </div>
              <div>
                <label className="label">Business Seats</label>
                <input type="number" required value={form.businessSeats} onChange={(e) => setForm({ ...form, businessSeats: e.target.value })} className="input" />
              </div>
            </div>
          </div>

          <div className="flex gap-3 pt-4">
            <button type="button" onClick={() => setModalOpen(false)} className="btn-outline flex-1">Cancel</button>
            <button type="submit" disabled={saving} className="btn-primary flex-1">
              {saving ? <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" /> : 'Save'}
            </button>
          </div>
        </form>
      </Modal>
    </div>
  );
}
