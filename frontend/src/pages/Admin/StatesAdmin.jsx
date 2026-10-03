import { useEffect, useState } from "react";
import {
  HiOutlinePencilSquare,
  HiOutlinePlus,
  HiOutlineTrash,
} from "react-icons/hi2";

import AdminPageHeader from "../../components/admin/AdminPageHeader";
import AdminTable from "../../components/admin/AdminTable";
import AdminModal from "../../components/admin/AdminModal";

import Container from "../../components/common/layout/Container";
import Button from "../../components/common/inputs/Button";

import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";

import stateService from "../../services/stateService";

const emptyForm = {
  name: "",
  capital: "",
  description: "",
  thumbnailUrl: "",
};

export default function StatesAdmin() {
  const [states, setStates] = useState([]);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  const [modalOpen, setModalOpen] = useState(false);
  const [editingState, setEditingState] = useState(null);

  const [formData, setFormData] = useState(emptyForm);

  useEffect(() => {
    loadStates();
  }, []);

  async function loadStates() {
    try {
      setLoading(true);
      setError("");

      const data = await stateService.getAll();

      setStates(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load states.");
    } finally {
      setLoading(false);
    }
  }

  function openAddModal() {
    setError("");
    setSuccess("");
    setEditingState(null);
    setFormData(emptyForm);
    setModalOpen(true);
  }

  function openEditModal(state) {
    setError("");
    setSuccess("");
    setEditingState(state);

    setFormData({
      name: state.name || "",
      capital: state.capital || "",
      description: state.description || "",
      thumbnailUrl: state.thumbnailUrl || "",
    });

    setModalOpen(true);
  }

  function closeModal() {
    if (saving) {
      return;
    }

    setModalOpen(false);
    setEditingState(null);
    setFormData(emptyForm);
  }

  function handleChange(event) {
    const { name, value } = event.target;

    setFormData((current) => ({
      ...current,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    try {
      setSaving(true);
      setError("");
      setSuccess("");

      if (editingState) {
        await stateService.update(
          editingState.id,
          formData
        );

        setSuccess(
          `"${formData.name}" was updated successfully.`
        );
      } else {
        await stateService.create(formData);

        setSuccess(
          `"${formData.name}" was created successfully.`
        );
      }

      closeModal();
      await loadStates();
    } catch (err) {
      console.error(err);

      const message =
        err.response?.data?.message ||
        "Unable to save state.";

      setError(message);
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(state) {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${state.name}"?`
    );

    if (!confirmed) {
      return;
    }

    try {
      setError("");
      setSuccess("");

      await stateService.delete(state.id);

      setSuccess(
        `"${state.name}" was deleted successfully.`
      );

      await loadStates();
    } catch (err) {
      console.error(err);

      const message =
        err.response?.data?.message ||
        "Unable to delete state.";

      setError(message);
    }
  }

  const columns = [
    {
      key: "name",
      label: "State",
      render: (state) => (
        <div className="flex items-center gap-4">
          {state.thumbnailUrl ? (
            <img
              src={state.thumbnailUrl}
              alt={state.name}
              className="h-12 w-16 rounded-lg object-cover"
            />
          ) : (
            <div className="flex h-12 w-16 items-center justify-center rounded-lg bg-slate-100 text-xs text-slate-400">
              No image
            </div>
          )}

          <div>
            <p className="font-semibold text-[var(--color-text)]">
              {state.name}
            </p>

            <p className="text-xs text-slate-500">
              ID: {state.id}
            </p>
          </div>
        </div>
      ),
    },
    {
      key: "capital",
      label: "Capital",
    },
    {
      key: "description",
      label: "Description",
      render: (state) => (
        <p className="max-w-md line-clamp-2">
          {state.description || "No description"}
        </p>
      ),
    },
  ];

  return (
    <main className="min-h-screen bg-slate-50">
      <AdminPageHeader
        title="Manage States"
        description="Add, edit, and manage the Indian states used throughout the tourism platform."
        action={{
          label: "Add State",
          icon: <HiOutlinePlus size={20} />,
          onClick: openAddModal,
        }}
      />

      <section className="py-10">
        <Container>
          {success && (
            <div className="mb-6 rounded-xl border border-emerald-200 bg-emerald-50 px-4 py-3 text-sm font-medium text-emerald-700">
              {success}
            </div>
          )}

          {error && (
            <div className="mb-6">
              <ErrorMessage message={error} />
            </div>
          )}

          {loading ? (
            <LoadingSpinner message="Loading states..." />
          ) : states.length === 0 ? (
            <EmptyState message="No states available." />
          ) : (
            <AdminTable
              columns={columns}
              data={states}
              rowKey="id"
              emptyMessage="No states available."
              renderActions={(state) => (
                <>
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() => openEditModal(state)}
                    className="px-3 py-2 text-sm"
                  >
                    <HiOutlinePencilSquare size={18} />
                    <span className="ml-2">
                      Edit
                    </span>
                  </Button>

                  <button
                    type="button"
                    onClick={() => handleDelete(state)}
                    className="
                      inline-flex
                      items-center
                      justify-center
                      gap-2
                      rounded-xl
                      border
                      border-red-200
                      px-3
                      py-2
                      text-sm
                      font-medium
                      text-red-600
                      transition-colors
                      hover:bg-red-50
                      focus:outline-none
                      focus:ring-2
                      focus:ring-red-200
                    "
                  >
                    <HiOutlineTrash size={18} />
                    Delete
                  </button>
                </>
              )}
            />
          )}
        </Container>
      </section>

      <AdminModal
        isOpen={modalOpen}
        onClose={closeModal}
        title={
          editingState
            ? "Edit State"
            : "Add State"
        }
        description={
          editingState
            ? "Update the information for this state."
            : "Add a new Indian state to the tourism platform."
        }
        size="lg"
      >
        <form
          onSubmit={handleSubmit}
          className="space-y-5"
        >
          <div>
            <label
              htmlFor="state-name"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              State Name
            </label>

            <input
              id="state-name"
              name="name"
              type="text"
              value={formData.name}
              onChange={handleChange}
              maxLength={100}
              required
              placeholder="e.g. Assam"
              className="
                w-full
                rounded-xl
                border
                border-slate-300
                px-4
                py-3
                outline-none
                transition
                focus:border-[var(--color-primary)]
                focus:ring-2
                focus:ring-emerald-100
              "
            />
          </div>

          <div>
            <label
              htmlFor="state-capital"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Capital
            </label>

            <input
              id="state-capital"
              name="capital"
              type="text"
              value={formData.capital}
              onChange={handleChange}
              maxLength={100}
              required
              placeholder="e.g. Dispur"
              className="
                w-full
                rounded-xl
                border
                border-slate-300
                px-4
                py-3
                outline-none
                transition
                focus:border-[var(--color-primary)]
                focus:ring-2
                focus:ring-emerald-100
              "
            />
          </div>

          <div>
            <label
              htmlFor="state-description"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Description
            </label>

            <textarea
              id="state-description"
              name="description"
              value={formData.description}
              onChange={handleChange}
              maxLength={2000}
              rows={5}
              placeholder="Brief description of the state..."
              className="
                w-full
                resize-y
                rounded-xl
                border
                border-slate-300
                px-4
                py-3
                outline-none
                transition
                focus:border-[var(--color-primary)]
                focus:ring-2
                focus:ring-emerald-100
              "
            />
          </div>

          <div>
            <label
              htmlFor="state-thumbnail-url"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Thumbnail URL
            </label>

            <input
              id="state-thumbnail-url"
              name="thumbnailUrl"
              type="url"
              value={formData.thumbnailUrl}
              onChange={handleChange}
              placeholder="https://example.com/images/assam.jpg"
              className="
                w-full
                rounded-xl
                border
                border-slate-300
                px-4
                py-3
                outline-none
                transition
                focus:border-[var(--color-primary)]
                focus:ring-2
                focus:ring-emerald-100
              "
            />
          </div>

          {formData.thumbnailUrl && (
            <div>
              <p className="mb-2 text-sm font-medium text-slate-700">
                Image Preview
              </p>

              <img
                src={formData.thumbnailUrl}
                alt="State preview"
                className="h-48 w-full rounded-xl object-cover"
              />
            </div>
          )}

          <div className="flex justify-end gap-3 border-t border-slate-200 pt-5">
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
                : editingState
                  ? "Update State"
                  : "Create State"}
            </Button>
          </div>
        </form>
      </AdminModal>
    </main>
  );
}