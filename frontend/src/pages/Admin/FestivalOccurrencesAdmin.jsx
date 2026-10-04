import { useEffect, useState } from "react";
import {
  HiOutlinePencilSquare,
  HiOutlinePlus,
  HiOutlineTrash,
} from "react-icons/hi2";

import Container from "../../components/common/layout/Container";
import Button from "../../components/common/inputs/Button";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";

import AdminPageHeader from "../../components/admin/AdminPageHeader";
import AdminTable from "../../components/admin/AdminTable";
import AdminModal from "../../components/admin/AdminModal";

import festivalService from "../../services/festivalService";
import festivalOccurrenceService from "../../services/festivalOccurrenceService";
import stateService from "../../services/stateService";

const emptyForm = {
  festivalId: "",
  stateId: "",
  year: new Date().getFullYear(),
  startDate: "",
  endDate: "",
  confirmed: false,
  notes: "",
};

export default function FestivalOccurrencesAdmin() {
  const [occurrences, setOccurrences] = useState([]);
  const [festivals, setFestivals] = useState([]);
  const [states, setStates] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [modalOpen, setModalOpen] = useState(false);
  const [editingOccurrence, setEditingOccurrence] =
    useState(null);

  const [form, setForm] = useState(emptyForm);

  const [saving, setSaving] = useState(false);
  const [deletingId, setDeletingId] = useState(null);

  const [successMessage, setSuccessMessage] =
    useState("");
  const [formError, setFormError] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    try {
      setLoading(true);
      setError("");

      const [
        occurrenceData,
        festivalData,
        stateData,
      ] = await Promise.all([
        festivalOccurrenceService.getAll(),
        festivalService.getAll(),
        stateService.getAll(),
      ]);

      setOccurrences(occurrenceData);
      setFestivals(festivalData);
      setStates(stateData);
    } catch (err) {
      console.error(err);
      setError(
        "Unable to load festival occurrences."
      );
    } finally {
      setLoading(false);
    }
  }

  function openCreateModal() {
    setEditingOccurrence(null);

    setForm({
      ...emptyForm,
      year: new Date().getFullYear(),
    });

    setFormError("");
    setSuccessMessage("");
    setModalOpen(true);
  }

  function openEditModal(occurrence) {
    setEditingOccurrence(occurrence);

    setForm({
      festivalId: occurrence.festivalId || "",
      stateId: occurrence.stateId || "",
      year: occurrence.year || "",
      startDate: occurrence.startDate || "",
      endDate: occurrence.endDate || "",
      confirmed: occurrence.confirmed ?? false,
      notes: occurrence.notes || "",
    });

    setFormError("");
    setSuccessMessage("");
    setModalOpen(true);
  }

  function closeModal() {
    if (saving) {
      return;
    }

    setModalOpen(false);
    setEditingOccurrence(null);
    setForm(emptyForm);
    setFormError("");
  }

  function handleChange(event) {
    const { name, value, type, checked } =
      event.target;

    setForm((current) => ({
      ...current,
      [name]:
        type === "checkbox"
          ? checked
          : value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    try {
      setSaving(true);
      setFormError("");
      setSuccessMessage("");

      if (
        form.startDate &&
        form.endDate &&
        form.endDate < form.startDate
      ) {
        setFormError(
          "End date cannot be before start date."
        );
        return;
      }

      const request = {
        festivalId: Number(form.festivalId),
        stateId: Number(form.stateId),
        year: Number(form.year),
        startDate: form.startDate,
        endDate: form.endDate,
        confirmed: form.confirmed,
        notes: form.notes.trim() || null,
      };

      if (editingOccurrence) {
        await festivalOccurrenceService.update(
          editingOccurrence.id,
          request
        );

        setSuccessMessage(
          "Festival occurrence updated successfully."
        );
      } else {
        await festivalOccurrenceService.create(
          request
        );

        setSuccessMessage(
          "Festival occurrence created successfully."
        );
      }

      const updatedOccurrences =
        await festivalOccurrenceService.getAll();

      setOccurrences(updatedOccurrences);

      setTimeout(() => {
        setModalOpen(false);
        setEditingOccurrence(null);
        setForm(emptyForm);
        setSuccessMessage("");
      }, 700);
    } catch (err) {
      console.error(err);

      setFormError(
        err.response?.data?.message ||
          "Unable to save festival occurrence."
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(occurrence) {
    const confirmed = window.confirm(
      `Are you sure you want to delete the occurrence of "${occurrence.festivalName}" in ${occurrence.stateName}?`
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeletingId(occurrence.id);
      setSuccessMessage("");
      setError("");

      await festivalOccurrenceService.delete(
        occurrence.id
      );

      setOccurrences((current) =>
        current.filter(
          (item) => item.id !== occurrence.id
        )
      );

      setSuccessMessage(
        "Festival occurrence deleted successfully."
      );
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
          "Unable to delete festival occurrence."
      );
    } finally {
      setDeletingId(null);
    }
  }

  const columns = [
    {
      key: "festivalName",
      label: "Festival",
      render: (occurrence) => (
        <div>
          <p className="font-semibold text-slate-900">
            {occurrence.festivalName}
          </p>

          {occurrence.notes && (
            <p className="mt-1 max-w-md break-words text-xs leading-5 text-slate-500">
              {occurrence.notes}
            </p>
          )}
        </div>
      ),
    },

    {
      key: "stateName",
      label: "State",
      render: (occurrence) => (
        <span className="font-medium text-slate-700">
          {occurrence.stateName}
        </span>
      ),
    },

    {
      key: "year",
      label: "Year",
    },

    {
      key: "dates",
      label: "Dates",
      render: (occurrence) => (
        <div className="whitespace-nowrap">
          <p className="text-sm font-medium text-slate-700">
            {occurrence.startDate}
          </p>

          <p className="text-xs text-slate-500">
            to {occurrence.endDate}
          </p>
        </div>
      ),
    },

    {
      key: "confirmed",
      label: "Confirmed",
      render: (occurrence) =>
        occurrence.confirmed ? (
          <span className="rounded-full bg-emerald-50 px-3 py-1 text-xs font-medium text-emerald-700">
            Confirmed
          </span>
        ) : (
          <span className="rounded-full bg-amber-50 px-3 py-1 text-xs font-medium text-amber-700">
            Unconfirmed
          </span>
        ),
    },
  ];

  if (loading) {
    return (
      <>
        <AdminPageHeader
          title="Festival Occurrences"
          description="Manage festival dates and state-wise occurrences."
        />

        <Container>
          <LoadingSpinner message="Loading festival occurrences..." />
        </Container>
      </>
    );
  }

  return (
    <>
      <AdminPageHeader
        title="Festival Occurrences"
        description="Manage festival dates and state-wise occurrences."
        action={{
          label: "Add Occurrence",
          icon: <HiOutlinePlus size={20} />,
          onClick: openCreateModal,
        }}
      />

      <Container>
        {error && (
          <div className="mb-6">
            <ErrorMessage message={error} />
          </div>
        )}

        {successMessage && (
          <div className="mb-6 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm font-medium text-emerald-700">
            {successMessage}
          </div>
        )}

        <AdminTable
          columns={columns}
          data={occurrences}
          emptyMessage="No festival occurrences available."
          renderActions={(occurrence) => (
            <>
              <button
                type="button"
                onClick={() =>
                  openEditModal(occurrence)
                }
                className="
                  rounded-lg
                  p-2
                  text-slate-500
                  transition-colors
                  hover:bg-slate-100
                  hover:text-[var(--color-primary)]
                "
                aria-label={`Edit ${occurrence.festivalName}`}
              >
                <HiOutlinePencilSquare size={20} />
              </button>

              <button
                type="button"
                onClick={() =>
                  handleDelete(occurrence)
                }
                disabled={
                  deletingId === occurrence.id
                }
                className="
                  rounded-lg
                  p-2
                  text-slate-500
                  transition-colors
                  hover:bg-red-50
                  hover:text-red-600
                  disabled:cursor-not-allowed
                  disabled:opacity-50
                "
                aria-label={`Delete ${occurrence.festivalName}`}
              >
                <HiOutlineTrash size={20} />
              </button>
            </>
          )}
        />
      </Container>

      <AdminModal
        isOpen={modalOpen}
        onClose={closeModal}
        title={
          editingOccurrence
            ? "Edit Festival Occurrence"
            : "Add Festival Occurrence"
        }
        description={
          editingOccurrence
            ? "Update the festival occurrence information."
            : "Add a festival occurrence for a specific state and year."
        }
        size="lg"
      >
        <form
          onSubmit={handleSubmit}
          className="space-y-6"
        >
          {formError && (
            <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {formError}
            </div>
          )}

          {successMessage && (
            <div className="rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm text-emerald-700">
              {successMessage}
            </div>
          )}

          <div>
            <label
              htmlFor="festivalId"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Festival *
            </label>

            <select
              id="festivalId"
              name="festivalId"
              value={form.festivalId}
              onChange={handleChange}
              required
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
            >
              <option value="">
                Select a festival
              </option>

              {festivals.map((festival) => (
                <option
                  key={festival.id}
                  value={festival.id}
                >
                  {festival.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label
              htmlFor="stateId"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              State *
            </label>

            <select
              id="stateId"
              name="stateId"
              value={form.stateId}
              onChange={handleChange}
              required
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
            >
              <option value="">
                Select a state
              </option>

              {states.map((state) => (
                <option
                  key={state.id}
                  value={state.id}
                >
                  {state.name}
                </option>
              ))}
            </select>
          </div>

          <div>
            <label
              htmlFor="year"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Year *
            </label>

            <input
              id="year"
              name="year"
              type="number"
              value={form.year}
              onChange={handleChange}
              required
              min="1900"
              max="3000"
              className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
            />
          </div>

          <div className="grid gap-6 sm:grid-cols-2">
            <div>
              <label
                htmlFor="startDate"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Start Date *
              </label>

              <input
                id="startDate"
                name="startDate"
                type="date"
                value={form.startDate}
                onChange={handleChange}
                required
                className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
              />
            </div>

            <div>
              <label
                htmlFor="endDate"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                End Date *
              </label>

              <input
                id="endDate"
                name="endDate"
                type="date"
                value={form.endDate}
                onChange={handleChange}
                required
                min={form.startDate || undefined}
                className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
              />
            </div>
          </div>

          <div>
            <label className="flex cursor-pointer items-center gap-3">
              <input
                type="checkbox"
                name="confirmed"
                checked={form.confirmed}
                onChange={handleChange}
                className="h-4 w-4 rounded border-slate-300 text-[var(--color-primary)] focus:ring-[var(--color-primary)]"
              />

              <span className="text-sm font-medium text-slate-700">
                Confirmed festival dates
              </span>
            </label>
          </div>

          <div>
            <label
              htmlFor="notes"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Notes
            </label>

            <textarea
              id="notes"
              name="notes"
              value={form.notes}
              onChange={handleChange}
              maxLength={2000}
              rows={4}
              placeholder="Additional information about this occurrence..."
              className="w-full resize-none rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
            />
          </div>

          <div className="flex justify-end gap-3 border-t border-slate-200 pt-6">
            <Button
              type="button"
              variant="outline"
              onClick={closeModal}
              disabled={saving}
            >
              Cancel
            </Button>

            <Button
              type="submit"
              disabled={saving}
            >
              {saving
                ? "Saving..."
                : editingOccurrence
                  ? "Update Occurrence"
                  : "Create Occurrence"}
            </Button>
          </div>
        </form>
      </AdminModal>
    </>
  );
}