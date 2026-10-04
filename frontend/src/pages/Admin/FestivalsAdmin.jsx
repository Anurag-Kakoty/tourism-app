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

const emptyForm = {
  name: "",
  description: "",
  category: "",
  imageUrl: "",
  officialWebsite: "",
};

export default function FestivalsAdmin() {
  const [festivals, setFestivals] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [modalOpen, setModalOpen] = useState(false);
  const [editingFestival, setEditingFestival] =
    useState(null);

  const [form, setForm] = useState(emptyForm);

  const [saving, setSaving] = useState(false);
  const [deletingId, setDeletingId] = useState(null);

  const [successMessage, setSuccessMessage] =
    useState("");
  const [formError, setFormError] = useState("");

  useEffect(() => {
    loadFestivals();
  }, []);

  async function loadFestivals() {
    try {
      setLoading(true);
      setError("");

      const data = await festivalService.getAll();

      setFestivals(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load festivals.");
    } finally {
      setLoading(false);
    }
  }

  function openCreateModal() {
    setEditingFestival(null);
    setForm(emptyForm);
    setFormError("");
    setSuccessMessage("");
    setModalOpen(true);
  }

  function openEditModal(festival) {
    setEditingFestival(festival);

    setForm({
      name: festival.name || "",
      description: festival.description || "",
      category: festival.category || "",
      imageUrl: festival.imageUrl || "",
      officialWebsite:
        festival.officialWebsite || "",
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
    setEditingFestival(null);
    setForm(emptyForm);
    setFormError("");
  }

  function handleChange(event) {
    const { name, value } = event.target;

    setForm((current) => ({
      ...current,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    try {
      setSaving(true);
      setFormError("");
      setSuccessMessage("");

      const request = {
        name: form.name.trim(),
        description:
          form.description.trim() || null,
        category:
          form.category.trim() || null,
        imageUrl:
          form.imageUrl.trim() || null,
        officialWebsite:
          form.officialWebsite.trim() || null,
      };

      if (editingFestival) {
        await festivalService.update(
          editingFestival.id,
          request
        );

        setSuccessMessage(
          "Festival updated successfully."
        );
      } else {
        await festivalService.create(request);

        setSuccessMessage(
          "Festival created successfully."
        );
      }

      const updatedFestivals =
        await festivalService.getAll();

      setFestivals(updatedFestivals);

      setTimeout(() => {
        setModalOpen(false);
        setEditingFestival(null);
        setForm(emptyForm);
        setSuccessMessage("");
      }, 700);
    } catch (err) {
      console.error(err);

      setFormError(
        err.response?.data?.message ||
          "Unable to save festival."
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(festival) {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${festival.name}"?`
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeletingId(festival.id);
      setSuccessMessage("");
      setError("");

      await festivalService.delete(festival.id);

      setFestivals((current) =>
        current.filter(
          (item) => item.id !== festival.id
        )
      );

      setSuccessMessage(
        "Festival deleted successfully."
      );
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
          "Unable to delete festival."
      );
    } finally {
      setDeletingId(null);
    }
  }

  const columns = [
    {
      key: "name",
      label: "Festival",
      cellClassName: "max-w-xl",
      render: (festival) => (
        <div className="flex items-start gap-3">
          {festival.imageUrl ? (
            <img
              src={festival.imageUrl}
              alt={festival.name}
              className="h-12 w-16 shrink-0 rounded-lg object-cover"
            />
          ) : (
            <div className="h-12 w-16 shrink-0 rounded-lg bg-slate-100" />
          )}

          <div className="min-w-0">
            <p className="font-semibold text-slate-900">
              {festival.name}
            </p>

            <p className="mt-1 max-w-md break-words text-xs leading-5 text-slate-500">
              {festival.description ||
                "No description"}
            </p>
          </div>
        </div>
      ),
    },
    {
      key: "category",
      label: "Category",
      render: (festival) =>
        festival.category ? (
          <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-medium text-slate-700">
            {festival.category}
          </span>
        ) : (
          <span className="text-slate-400">
            —
          </span>
        ),
    },
    {
      key: "officialWebsite",
      label: "Website",
      render: (festival) =>
        festival.officialWebsite ? (
          <a
            href={festival.officialWebsite}
            target="_blank"
            rel="noreferrer"
            className="font-medium text-[var(--color-primary)] hover:underline"
          >
            Visit
          </a>
        ) : (
          <span className="text-slate-400">
            —
          </span>
        ),
    },
  ];

  if (loading) {
    return (
      <>
        <AdminPageHeader
          title="Festivals"
          description="Manage festivals available on the tourism platform."
        />

        <Container>
          <LoadingSpinner message="Loading festivals..." />
        </Container>
      </>
    );
  }

  return (
    <>
      <AdminPageHeader
        title="Festivals"
        description="Manage festivals available on the tourism platform."
        action={{
          label: "Add Festival",
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
          data={festivals}
          emptyMessage="No festivals available."
          renderActions={(festival) => (
            <>
              <button
                type="button"
                onClick={() =>
                  openEditModal(festival)
                }
                className="
                  rounded-lg
                  p-2
                  text-slate-500
                  transition-colors
                  hover:bg-slate-100
                  hover:text-[var(--color-primary)]
                "
                aria-label={`Edit ${festival.name}`}
              >
                <HiOutlinePencilSquare size={20} />
              </button>

              <button
                type="button"
                onClick={() =>
                  handleDelete(festival)
                }
                disabled={
                  deletingId === festival.id
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
                aria-label={`Delete ${festival.name}`}
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
          editingFestival
            ? "Edit Festival"
            : "Add Festival"
        }
        description={
          editingFestival
            ? "Update the festival information."
            : "Add a new festival to the tourism platform."
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
              htmlFor="name"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Festival Name *
            </label>

            <input
              id="name"
              name="name"
              value={form.name}
              onChange={handleChange}
              required
              maxLength={150}
              className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
            />
          </div>

          <div>
            <label
              htmlFor="category"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Category
            </label>

            <input
              id="category"
              name="category"
              value={form.category}
              onChange={handleChange}
              maxLength={100}
              placeholder="Cultural, Religious, Harvest, etc."
              className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
            />
          </div>

          <div>
            <label
              htmlFor="description"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Description
            </label>

            <textarea
              id="description"
              name="description"
              value={form.description}
              onChange={handleChange}
              maxLength={2000}
              rows={5}
              className="w-full resize-none rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
            />
          </div>

          <div>
            <label
              htmlFor="imageUrl"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Image URL
            </label>

            <input
              id="imageUrl"
              name="imageUrl"
              value={form.imageUrl}
              onChange={handleChange}
              maxLength={500}
              className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
            />
          </div>

          {form.imageUrl && (
            <div>
              <p className="mb-2 text-sm font-medium text-slate-700">
                Image Preview
              </p>

              <img
                src={form.imageUrl}
                alt="Festival preview"
                className="h-48 w-full rounded-xl object-cover"
              />
            </div>
          )}

          <div>
            <label
              htmlFor="officialWebsite"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Official Website
            </label>

            <input
              id="officialWebsite"
              name="officialWebsite"
              type="url"
              value={form.officialWebsite}
              onChange={handleChange}
              maxLength={500}
              placeholder="https://example.com"
              className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
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
                : editingFestival
                  ? "Update Festival"
                  : "Create Festival"}
            </Button>
          </div>
        </form>
      </AdminModal>
    </>
  );
}