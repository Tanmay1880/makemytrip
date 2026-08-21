import { useState, useEffect } from 'react';
import { Plus, Pencil, Trash2 } from 'lucide-react';
import { getAllAirlines, createAirline, updateAirline, deleteAirline } from '@/api/airlineApi';
import { useToast } from '@/context/ToastContext';
import PageHeader from '@/components/PageHeader';
import Modal from '@/components/Modal';

const emptyForm = { name: '', code: '', country: '' };

export default function AdminAirlines() {
  const { toast } = useToast();
  const [airlines, setAirlines] = useState([]);
  const [loading, setLoading] = useState(true);
  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);
  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const data = await getAllAirlines();
      setAirlines(data);
    } catch {
      toast.error('Failed to load airlines');
    } finally {
      setLoading(false);
    }
  };

  const openCreate = () => {
    setEditing(null);
    setForm(emptyForm);
    setModalOpen(true);
  };

  const openEdit = (airline) => {
    setEditing(airline);
    setForm({ name: airline.name, code: airline.code, country: airline.country });
    setModalOpen(true);
  };

  const handleSave = async (e) => {
    e.preventDefault();
    setSaving(true);
    try {
      if (editing) {
        await updateAirline(editing.id, form);
        toast.success('Airline updated');
      } else {
        await createAirline(form);
        toast.success('Airline created');
      }
      setModalOpen(false);
      loadData();
    } catch {
      toast.error('Failed to save airline');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id) => {
    if (!confirm('Delete this airline?')) return;
    try {
      await deleteAirline(id);
      toast.success('Airline deleted');
      loadData();
    } catch {
      toast.error('Failed to delete airline');
    }
  };

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
        title="Airline Management"
        subtitle="Manage airline carriers"
        action={
          <button onClick={openCreate} className="btn-primary">
            <Plus className="w-4 h-4" /> Add Airline
          </button>
        }
      />

      <div className="card overflow-hidden">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-500">
              <tr>
                <th className="text-left px-4 py-3 font-medium">Code</th>
                <th className="text-left px-4 py-3 font-medium">Name</th>
                <th className="text-left px-4 py-3 font-medium">Country</th>
                <th className="text-right px-4 py-3 font-medium">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
              {airlines.map((a) => (
                <tr key={a.id} className="hover:bg-gray-50">
                  <td className="px-4 py-3">
                    <span className="badge bg-primary-100 text-primary-700">{a.code}</span>
                  </td>
                  <td className="px-4 py-3 font-medium text-gray-900">{a.name}</td>
                  <td className="px-4 py-3 text-gray-600">{a.country}</td>
                  <td className="px-4 py-3">
                    <div className="flex items-center justify-end gap-1">
                      <button onClick={() => openEdit(a)} className="p-2 rounded-lg hover:bg-gray-100 text-gray-500">
                        <Pencil className="w-4 h-4" />
                      </button>
                      <button onClick={() => handleDelete(a.id)} className="p-2 rounded-lg hover:bg-error-50 text-error-500">
                        <Trash2 className="w-4 h-4" />
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        {airlines.length === 0 && (
          <div className="p-8 text-center text-sm text-gray-500">No airlines found</div>
        )}
      </div>

      <Modal open={modalOpen} onClose={() => setModalOpen(false)} title={editing ? 'Edit Airline' : 'Add Airline'}>
        <form onSubmit={handleSave} className="space-y-4">
          <div>
            <label className="label">Airline Name</label>
            <input
              type="text"
              required
              value={form.name}
              onChange={(e) => setForm({ ...form, name: e.target.value })}
              className="input"
              placeholder="SkyJet Airways"
            />
          </div>
          <div>
            <label className="label">IATA Code</label>
            <input
              type="text"
              required
              maxLength={3}
              value={form.code}
              onChange={(e) => setForm({ ...form, code: e.target.value.toUpperCase() })}
              className="input uppercase"
              placeholder="SJ"
            />
          </div>
          <div>
            <label className="label">Country</label>
            <input
              type="text"
              required
              value={form.country}
              onChange={(e) => setForm({ ...form, country: e.target.value })}
              className="input"
              placeholder="United States"
            />
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
