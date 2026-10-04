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

import placeService from "../../services/placeService";
import destinationService from "../../services/destinationService";
import experienceService from "../../services/experienceService";
import tagService from "../../services/tagService";

const emptyForm = {
  name: "",
  description: "",
  latitude: "",
  longitude: "",
  bestSeason: "",
  entryFee: "",
  thumbnailUrl: "",
  destinationId: "",
  featured: false,
  displayOrder: 0,
  tagIds: [],
  experienceIds: [],
};

export default function AttractionsAdmin() {
  const [attractions, setAttractions] = useState([]);
  const [destinations, setDestinations] = useState([]);
  const [experiences, setExperiences] = useState([]);
  const [tags, setTags] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [modalOpen, setModalOpen] = useState(false);
  const [editingAttraction, setEditingAttraction] =
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
        attractionData,
        destinationData,
        experienceData,
        tagData,
      ] = await Promise.all([
        placeService.getAll(),
        destinationService.getAll(),
        experienceService.getAll(),
        tagService.getAll(),
      ]);

      setAttractions(attractionData);
      setDestinations(destinationData);
      setExperiences(experienceData);
      setTags(tagData);
    } catch (err) {
      console.error(err);
      setError("Unable to load attraction data.");
    } finally {
      setLoading(false);
    }
  }

  function openCreateModal() {
    setEditingAttraction(null);
    setForm(emptyForm);
    setFormError("");
    setSuccessMessage("");
    setModalOpen(true);
  }

  function openEditModal(attraction) {
    setEditingAttraction(attraction);

    setForm({
      name: attraction.name || "",
      description: attraction.description || "",
      latitude:
        attraction.latitude !== null &&
        attraction.latitude !== undefined
          ? String(attraction.latitude)
          : "",
      longitude:
        attraction.longitude !== null &&
        attraction.longitude !== undefined
          ? String(attraction.longitude)
          : "",
      bestSeason: attraction.bestSeason || "",
      entryFee:
        attraction.entryFee !== null &&
        attraction.entryFee !== undefined
          ? String(attraction.entryFee)
          : "",
      thumbnailUrl: attraction.thumbnailUrl || "",
      destinationId:
        attraction.destinationId !== null &&
        attraction.destinationId !== undefined
          ? String(attraction.destinationId)
          : "",
      featured: Boolean(attraction.featured),
      displayOrder:
        attraction.displayOrder !== null &&
        attraction.displayOrder !== undefined
          ? attraction.displayOrder
          : 0,
      tagIds: attraction.tagIds || [],
      experienceIds: attraction.experienceIds || [],
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
    setEditingAttraction(null);
    setForm(emptyForm);
    setFormError("");
  }

  function handleChange(event) {
    const { name, value, type, checked } = event.target;

    setForm((current) => ({
      ...current,
      [name]:
        type === "checkbox"
          ? checked
          : value,
    }));
  }

  function toggleSelection(
    field,
    id
  ) {
    setForm((current) => {
      const currentIds = current[field];

      const exists = currentIds.includes(id);

      return {
        ...current,
        [field]: exists
          ? currentIds.filter(
              (currentId) => currentId !== id
            )
          : [...currentIds, id],
      };
    });
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
        latitude: Number(form.latitude),
        longitude: Number(form.longitude),
        bestSeason: form.bestSeason.trim(),
        entryFee:
          form.entryFee === ""
            ? null
            : Number(form.entryFee),
        thumbnailUrl:
          form.thumbnailUrl.trim() || null,
        destinationId: Number(
          form.destinationId
        ),
        featured: Boolean(form.featured),
        displayOrder: Number(
          form.displayOrder
        ),
        tagIds: form.tagIds,
        experienceIds: form.experienceIds,
      };

      if (editingAttraction) {
        await placeService.update(
          editingAttraction.id,
          request
        );

        setSuccessMessage(
          "Attraction updated successfully."
        );
      } else {
        await placeService.create(request);

        setSuccessMessage(
          "Attraction created successfully."
        );
      }

      const updatedAttractions =
        await placeService.getAll();

      setAttractions(updatedAttractions);

      setTimeout(() => {
        setModalOpen(false);
        setEditingAttraction(null);
        setForm(emptyForm);
        setSuccessMessage("");
      }, 700);
    } catch (err) {
      console.error(err);

      setFormError(
        err.response?.data?.message ||
          "Unable to save attraction."
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(attraction) {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${attraction.name}"?`
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeletingId(attraction.id);
      setSuccessMessage("");
      setError("");

      await placeService.delete(
        attraction.id
      );

      setAttractions((current) =>
        current.filter(
          (item) => item.id !== attraction.id
        )
      );

      setSuccessMessage(
        "Attraction deleted successfully."
      );
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
          "Unable to delete attraction."
      );
    } finally {
      setDeletingId(null);
    }
  }

  const columns = [
    {
      key: "name",
      label: "Attraction",
      render: (attraction) => (
        <div className="flex items-center gap-3">
          {attraction.thumbnailUrl ? (
            <img
              src={attraction.thumbnailUrl}
              alt={attraction.name}
              className="h-12 w-16 shrink-0 rounded-lg object-cover"
            />
          ) : (
            <div className="h-12 w-16 shrink-0 rounded-lg bg-slate-100" />
          )}

          <div className="min-w-0">
            <p className="font-semibold text-slate-900">
              {attraction.name}
            </p>

            <p className="truncate text-xs text-slate-500">
              {attraction.bestSeason}
            </p>
          </div>
        </div>
      ),
    },
    {
      key: "destinationName",
      label: "Destination",
    },
    {
      key: "stateName",
      label: "State",
    },
    {
      key: "featured",
      label: "Featured",
      render: (attraction) =>
        attraction.featured ? (
          <span className="font-medium text-emerald-600">
            Yes
          </span>
        ) : (
          <span className="text-slate-400">
            No
          </span>
        ),
    },
    {
      key: "displayOrder",
      label: "Order",
    },
  ];

  if (loading) {
    return (
      <>
        <AdminPageHeader
          title="Attractions"
          description="Manage tourist attractions and their relationships with destinations, experiences and tags."
        />

        <Container>
          <LoadingSpinner message="Loading attractions..." />
        </Container>
      </>
    );
  }

  return (
    <>
      <AdminPageHeader
        title="Attractions"
        description="Manage tourist attractions and their relationships with destinations, experiences and tags."
        action={{
          label: "Add Attraction",
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
          data={attractions}
          emptyMessage="No attractions available."
          renderActions={(attraction) => (
            <>
              <button
                type="button"
                onClick={() =>
                  openEditModal(attraction)
                }
                className="
                  rounded-lg
                  p-2
                  text-slate-500
                  transition-colors
                  hover:bg-slate-100
                  hover:text-[var(--color-primary)]
                "
                aria-label={`Edit ${attraction.name}`}
              >
                <HiOutlinePencilSquare size={20} />
              </button>

              <button
                type="button"
                onClick={() =>
                  handleDelete(attraction)
                }
                disabled={
                  deletingId ===
                  attraction.id
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
                aria-label={`Delete ${attraction.name}`}
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
          editingAttraction
            ? "Edit Attraction"
            : "Add Attraction"
        }
        description={
          editingAttraction
            ? "Update the attraction information and relationships."
            : "Add a new attraction and associate it with a destination, experiences and tags."
        }
        size="xl"
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

          <div className="grid gap-5 md:grid-cols-2">
            <div>
              <label
                htmlFor="name"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Name *
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
                htmlFor="destinationId"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Destination *
              </label>

              <select
                id="destinationId"
                name="destinationId"
                value={form.destinationId}
                onChange={handleChange}
                required
                className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
              >
                <option value="">
                  Select destination
                </option>

                {destinations.map(
                  (destination) => (
                    <option
                      key={destination.id}
                      value={destination.id}
                    >
                      {destination.name}
                      {destination.stateName
                        ? ` — ${destination.stateName}`
                        : ""}
                    </option>
                  )
                )}
              </select>
            </div>
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
              rows={4}
              className="w-full resize-none rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
            />
          </div>

          <div>
            <h3 className="mb-4 text-sm font-semibold text-slate-900">
              Location
            </h3>

            <div className="grid gap-5 md:grid-cols-2">
              <div>
                <label
                  htmlFor="latitude"
                  className="mb-2 block text-sm font-medium text-slate-700"
                >
                  Latitude *
                </label>

                <input
                  id="latitude"
                  name="latitude"
                  type="number"
                  step="any"
                  value={form.latitude}
                  onChange={handleChange}
                  required
                  className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
                />
              </div>

              <div>
                <label
                  htmlFor="longitude"
                  className="mb-2 block text-sm font-medium text-slate-700"
                >
                  Longitude *
                </label>

                <input
                  id="longitude"
                  name="longitude"
                  type="number"
                  step="any"
                  value={form.longitude}
                  onChange={handleChange}
                  required
                  className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
                />
              </div>
            </div>
          </div>

          <div className="grid gap-5 md:grid-cols-3">
            <div>
              <label
                htmlFor="bestSeason"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Best Season *
              </label>

              <input
                id="bestSeason"
                name="bestSeason"
                value={form.bestSeason}
                onChange={handleChange}
                required
                maxLength={150}
                placeholder="October to April"
                className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
              />
            </div>

            <div>
              <label
                htmlFor="entryFee"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Entry Fee
              </label>

              <input
                id="entryFee"
                name="entryFee"
                type="number"
                min="0"
                step="0.01"
                value={form.entryFee}
                onChange={handleChange}
                className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
              />
            </div>

            <div>
              <label
                htmlFor="displayOrder"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Display Order *
              </label>

              <input
                id="displayOrder"
                name="displayOrder"
                type="number"
                min="0"
                value={form.displayOrder}
                onChange={handleChange}
                required
                className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
              />
            </div>
          </div>

          <div>
            <label
              htmlFor="thumbnailUrl"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Thumbnail URL
            </label>

            <input
              id="thumbnailUrl"
              name="thumbnailUrl"
              value={form.thumbnailUrl}
              onChange={handleChange}
              maxLength={500}
              className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
            />
          </div>

          <div>
            <h3 className="mb-4 text-sm font-semibold text-slate-900">
              Experiences
            </h3>

            {experiences.length === 0 ? (
              <p className="text-sm text-slate-500">
                No experiences available.
              </p>
            ) : (
              <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
                {experiences.map(
                  (experience) => (
                    <label
                      key={experience.id}
                      className="
                        flex
                        cursor-pointer
                        items-center
                        gap-3
                        rounded-xl
                        border
                        border-slate-200
                        px-4
                        py-3
                        transition
                        hover:bg-slate-50
                      "
                    >
                      <input
                        type="checkbox"
                        checked={form.experienceIds.includes(
                          experience.id
                        )}
                        onChange={() =>
                          toggleSelection(
                            "experienceIds",
                            experience.id
                          )
                        }
                        className="h-4 w-4 rounded border-slate-300 text-[var(--color-primary)] focus:ring-[var(--color-primary)]"
                      />

                      <span className="text-sm text-slate-700">
                        {experience.name}
                      </span>
                    </label>
                  )
                )}
              </div>
            )}
          </div>

          <div>
            <h3 className="mb-4 text-sm font-semibold text-slate-900">
              Tags
            </h3>

            {tags.length === 0 ? (
              <p className="text-sm text-slate-500">
                No tags available.
              </p>
            ) : (
              <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
                {tags.map((tag) => (
                  <label
                    key={tag.id}
                    className="
                      flex
                      cursor-pointer
                      items-center
                      gap-3
                      rounded-xl
                      border
                      border-slate-200
                      px-4
                      py-3
                      transition
                      hover:bg-slate-50
                    "
                  >
                    <input
                      type="checkbox"
                      checked={form.tagIds.includes(
                        tag.id
                      )}
                      onChange={() =>
                        toggleSelection(
                          "tagIds",
                          tag.id
                        )
                      }
                      className="h-4 w-4 rounded border-slate-300 text-[var(--color-primary)] focus:ring-[var(--color-primary)]"
                    />

                    <span className="text-sm text-slate-700">
                      {tag.name}
                    </span>
                  </label>
                ))}
              </div>
            )}
          </div>

          <label className="flex cursor-pointer items-center gap-3">
            <input
              type="checkbox"
              name="featured"
              checked={form.featured}
              onChange={handleChange}
              className="h-5 w-5 rounded border-slate-300 text-[var(--color-primary)] focus:ring-[var(--color-primary)]"
            />

            <span className="text-sm font-medium text-slate-700">
              Featured attraction
            </span>
          </label>

          {form.thumbnailUrl && (
            <div>
              <p className="mb-2 text-sm font-medium text-slate-700">
                Thumbnail Preview
              </p>

              <img
                src={form.thumbnailUrl}
                alt="Attraction preview"
                className="h-40 w-full rounded-xl object-cover"
              />
            </div>
          )}

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
                : editingAttraction
                  ? "Update Attraction"
                  : "Create Attraction"}
            </Button>
          </div>
        </form>
      </AdminModal>
    </>
  );
}