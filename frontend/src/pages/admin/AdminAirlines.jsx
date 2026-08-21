import { useState, useEffect } from 'react';
import { Plus, Pencil, Trash2 } from 'lucide-react';

import {
  getAllAirlines,
  createAirline,
  updateAirline,
  deleteAirline,
} from '@/api/airlineApi';

import { useToast } from '@/context/ToastContext';

import PageHeader from '@/components/PageHeader';
import Modal from '@/components/Modal';

// ============================================================
// FORM
// ============================================================

const emptyForm = {
  name: '',
  code: '',
};

// ============================================================
// ADMIN AIRLINES
// ============================================================

export default function AdminAirlines() {
  // IMPORTANT:
  // useToast() returns the toast object directly.
  const toast = useToast();

  const [airlines, setAirlines] = useState([]);
  const [loading, setLoading] = useState(true);

  const [modalOpen, setModalOpen] = useState(false);
  const [editing, setEditing] = useState(null);

  const [form, setForm] = useState(emptyForm);
  const [saving, setSaving] = useState(false);

  // ============================================================
  // LOAD AIRLINES
  // ============================================================

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    setLoading(true);

    try {
      const data = await getAllAirlines();

      setAirlines(
        Array.isArray(data) ? data : []
      );
    } catch (error) {
      console.error(
        'Failed to load airlines:',
        error
      );

      const message =
        error?.response?.data?.message ||
        'Failed to load airlines';

      toast.error(message);
    } finally {
      setLoading(false);
    }
  };

  // ============================================================
  // CREATE
  // ============================================================

  const openCreate = () => {
    setEditing(null);
    setForm(emptyForm);
    setModalOpen(true);
  };

  // ============================================================
  // EDIT
  // ============================================================

  const openEdit = (airline) => {
    setEditing(airline);

    setForm({
      name: airline.name || '',
      code: airline.code || '',
    });

    setModalOpen(true);
  };

  // ============================================================
  // SAVE
  // ============================================================

  const handleSave = async (e) => {
    e.preventDefault();

    if (saving) {
      return;
    }

    setSaving(true);

    try {
      const airlineData = {
        name: form.name.trim(),
        code: form.code.trim().toUpperCase(),
      };

      if (!airlineData.name || !airlineData.code) {
        toast.error(
          'Airline name and code are required'
        );

        return;
      }

      if (editing) {
        await updateAirline(
          editing.id,
          airlineData
        );

        toast.success(
          'Airline updated successfully'
        );
      } else {
        await createAirline(
          airlineData
        );

        toast.success(
          'Airline created successfully'
        );
      }

      setModalOpen(false);
      setEditing(null);
      setForm(emptyForm);

      await loadData();

    } catch (error) {
      console.error(
        'Failed to save airline:',
        error
      );

      const message =
        error?.response?.data?.message ||
        'Failed to save airline';

      toast.error(message);

    } finally {
      setSaving(false);
    }
  };

  // ============================================================
  // DELETE
  // ============================================================

  const handleDelete = async (id) => {
    if (!window.confirm('Delete this airline?')) {
      return;
    }

    try {
      await deleteAirline(id);

      toast.success(
        'Airline deleted successfully'
      );

      await loadData();

    } catch (error) {
      console.error(
        'Failed to delete airline:',
        error
      );

      const message =
        error?.response?.data?.message ||
        'Failed to delete airline';

      toast.error(message);
    }
  };

  // ============================================================
  // LOADING
  // ============================================================

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">

        <div className="w-8 h-8 border-2 border-primary-600 border-t-transparent rounded-full animate-spin" />

      </div>
    );
  }

  // ============================================================
  // UI
  // ============================================================

  return (
    <div>

      <PageHeader
        title="Airline Management"
        subtitle="Manage airline carriers"
        action={
          <button
            onClick={openCreate}
            className="btn-primary"
          >
            <Plus className="w-4 h-4" />
            Add Airline
          </button>
        }
      />

      {/* ========================================================
          AIRLINE TABLE
      ========================================================= */}

      <div className="card overflow-hidden">

        <div className="overflow-x-auto">

          <table className="w-full text-sm">

            <thead className="bg-gray-50 text-gray-500">

              <tr>

                <th className="text-left px-4 py-3 font-medium">
                  Code
                </th>

                <th className="text-left px-4 py-3 font-medium">
                  Name
                </th>

                <th className="text-right px-4 py-3 font-medium">
                  Actions
                </th>

              </tr>

            </thead>

            <tbody className="divide-y divide-gray-100">

              {airlines.map((airline) => (

                <tr
                  key={airline.id}
                  className="hover:bg-gray-50"
                >

                  <td className="px-4 py-3">

                    <span className="badge bg-primary-100 text-primary-700">
                      {airline.code}
                    </span>

                  </td>

                  <td className="px-4 py-3 font-medium text-gray-900">
                    {airline.name}
                  </td>

                  <td className="px-4 py-3">

                    <div className="flex items-center justify-end gap-1">

                      <button
                        onClick={() =>
                          openEdit(airline)
                        }
                        className="p-2 rounded-lg hover:bg-gray-100 text-gray-500"
                        title="Edit airline"
                      >
                        <Pencil className="w-4 h-4" />
                      </button>

                      <button
                        onClick={() =>
                          handleDelete(airline.id)
                        }
                        className="p-2 rounded-lg hover:bg-error-50 text-error-500"
                        title="Delete airline"
                      >
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

          <div className="p-8 text-center text-sm text-gray-500">
            No airlines found
          </div>

        )}

      </div>

      {/* ========================================================
          ADD / EDIT MODAL
      ========================================================= */}

      <Modal
        open={modalOpen}
        onClose={() => {
          if (!saving) {
            setModalOpen(false);
          }
        }}
        title={
          editing
            ? 'Edit Airline'
            : 'Add Airline'
        }
      >

        <form
          onSubmit={handleSave}
          className="space-y-4"
        >

          {/* AIRLINE NAME */}

          <div>

            <label className="label">
              Airline Name
            </label>

            <input
              type="text"
              required
              value={form.name}
              onChange={(e) =>
                setForm({
                  ...form,
                  name: e.target.value,
                })
              }
              className="input"
              placeholder="Air India"
            />

          </div>

          {/* IATA CODE */}

          <div>

            <label className="label">
              IATA Code
            </label>

            <input
              type="text"
              required
              maxLength={3}
              value={form.code}
              onChange={(e) =>
                setForm({
                  ...form,
                  code: e.target.value
                    .toUpperCase(),
                })
              }
              className="input uppercase"
              placeholder="AI"
            />

          </div>

          {/* ACTIONS */}

          <div className="flex gap-3 pt-4">

            <button
              type="button"
              disabled={saving}
              onClick={() =>
                setModalOpen(false)
              }
              className="btn-outline flex-1"
            >
              Cancel
            </button>

            <button
              type="submit"
              disabled={saving}
              className="btn-primary flex-1"
            >

              {saving ? (

                <span className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />

              ) : (

                'Save'

              )}

            </button>

          </div>

        </form>

      </Modal>

    </div>
  );
}