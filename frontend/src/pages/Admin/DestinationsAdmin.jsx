import { useEffect, useState } from "react";
import { HiOutlinePencilSquare, HiOutlinePlus, HiOutlineTrash } from "react-icons/hi2";

import Container from "../../components/common/layout/Container";
import Button from "../../components/common/inputs/Button";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";

import AdminPageHeader from "../../components/admin/AdminPageHeader";
import AdminTable from "../../components/admin/AdminTable";
import AdminModal from "../../components/admin/AdminModal";

import destinationService from "../../services/destinationService";
import stateService from "../../services/stateService";

const destinationTypes = [
  "CITY",
  "TOWN",
  "VILLAGE",
  "HILL_STATION",
  "BEACH",
  "ISLAND",
  "NATIONAL_PARK",
  "WILDLIFE_SANCTUARY",
];

const emptyForm = {
  name: "",
  tagline: "",
  description: "",
  district: "",
  stateId: "",
  type: "",
  latitude: "",
  longitude: "",
  thumbnailUrl: "",
  featured: false,
  popular: false,
  displayOrder: 0,
  coverImageUrl: "",
  timezone: "",
  nearestAirport: "",
  nearestRailwayStation: "",
};

export default function DestinationsAdmin() {
  const [destinations, setDestinations] = useState([]);
  const [states, setStates] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [modalOpen, setModalOpen] = useState(false);
  const [editingDestination, setEditingDestination] = useState(null);

  const [form, setForm] = useState(emptyForm);

  const [saving, setSaving] = useState(false);
  const [deletingId, setDeletingId] = useState(null);

  const [successMessage, setSuccessMessage] = useState("");
  const [formError, setFormError] = useState("");

  useEffect(() => {
    loadData();
  }, []);

  async function loadData() {
    try {
      setLoading(true);
      setError("");

      const [destinationData, stateData] =
        await Promise.all([
          destinationService.getAll(),
          stateService.getAll(),
        ]);

      setDestinations(destinationData);
      setStates(stateData);
    } catch (err) {
      console.error(err);
      setError("Unable to load destinations.");
    } finally {
      setLoading(false);
    }
  }

  function openCreateModal() {
    setEditingDestination(null);
    setForm(emptyForm);
    setFormError("");
    setSuccessMessage("");
    setModalOpen(true);
  }

  function openEditModal(destination) {
    setEditingDestination(destination);

    setForm({
      name: destination.name || "",
      tagline: destination.tagline || "",
      description: destination.description || "",
      district: destination.district || "",
      stateId: destination.stateId
        ? String(destination.stateId)
        : "",
      type: destination.type || "",
      latitude:
        destination.latitude !== null &&
        destination.latitude !== undefined
          ? String(destination.latitude)
          : "",
      longitude:
        destination.longitude !== null &&
        destination.longitude !== undefined
          ? String(destination.longitude)
          : "",
      thumbnailUrl: destination.thumbnailUrl || "",
      featured: Boolean(destination.featured),
      popular: Boolean(destination.popular),
      displayOrder:
        destination.displayOrder !== null &&
        destination.displayOrder !== undefined
          ? destination.displayOrder
          : 0,
      coverImageUrl: destination.coverImageUrl || "",
      timezone: destination.timezone || "",
      nearestAirport: destination.nearestAirport || "",
      nearestRailwayStation:
        destination.nearestRailwayStation || "",
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
    setEditingDestination(null);
    setForm(emptyForm);
    setFormError("");
  }

  function handleChange(event) {
    const { name, value, type, checked } = event.target;

    setForm((current) => ({
      ...current,
      [name]: type === "checkbox" ? checked : value,
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
        tagline: form.tagline.trim() || null,
        description: form.description.trim() || null,
        district: form.district.trim() || null,
        stateId: Number(form.stateId),
        type: form.type,
        latitude: Number(form.latitude),
        longitude: Number(form.longitude),
        thumbnailUrl: form.thumbnailUrl.trim() || null,
        featured: Boolean(form.featured),
        popular: Boolean(form.popular),
        displayOrder: Number(form.displayOrder),
        coverImageUrl: form.coverImageUrl.trim() || null,
        timezone: form.timezone.trim() || null,
        nearestAirport:
          form.nearestAirport.trim() || null,
        nearestRailwayStation:
          form.nearestRailwayStation.trim() || null,
      };

      if (editingDestination) {
        await destinationService.update(
          editingDestination.id,
          request
        );

        setSuccessMessage(
          "Destination updated successfully."
        );
      } else {
        await destinationService.create(request);

        setSuccessMessage(
          "Destination created successfully."
        );
      }

      const updatedDestinations =
        await destinationService.getAll();

      setDestinations(updatedDestinations);

      setTimeout(() => {
        setModalOpen(false);
        setEditingDestination(null);
        setForm(emptyForm);
        setSuccessMessage("");
      }, 700);
    } catch (err) {
      console.error(err);

      setFormError(
        err.response?.data?.message ||
          "Unable to save destination."
      );
    } finally {
      setSaving(false);
    }
  }

  async function handleDelete(destination) {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${destination.name}"?`
    );

    if (!confirmed) {
      return;
    }

    try {
      setDeletingId(destination.id);
      setSuccessMessage("");
      setError("");

      await destinationService.delete(
        destination.id
      );

      setDestinations((current) =>
        current.filter(
          (item) => item.id !== destination.id
        )
      );

      setSuccessMessage(
        "Destination deleted successfully."
      );
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
          "Unable to delete destination."
      );
    } finally {
      setDeletingId(null);
    }
  }

  function getStateName(stateId) {
    const state = states.find(
      (item) => item.id === stateId
    );

    return state?.name || "Unknown";
  }

  const columns = [
    {
      key: "name",
      label: "Destination",
      render: (destination) => (
        <div className="flex items-center gap-3">
          {destination.thumbnailUrl ? (
            <img
              src={destination.thumbnailUrl}
              alt={destination.name}
              className="h-12 w-16 shrink-0 rounded-lg object-cover"
            />
          ) : (
            <div className="h-12 w-16 shrink-0 rounded-lg bg-slate-100" />
          )}

          <div className="min-w-0">
            <p className="font-semibold text-slate-900">
              {destination.name}
            </p>

            <p className="truncate text-xs text-slate-500">
              {destination.district || "No district"}
            </p>
          </div>
        </div>
      ),
    },
    {
      key: "stateName",
      label: "State",
    },
    {
      key: "type",
      label: "Type",
      render: (destination) => (
        <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-medium text-slate-700">
          {destination.type}
        </span>
      ),
    },
    {
      key: "featured",
      label: "Featured",
      render: (destination) =>
        destination.featured ? (
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
      key: "popular",
      label: "Popular",
      render: (destination) =>
        destination.popular ? (
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
          title="Destinations"
          description="Manage destinations available throughout India."
        />

        <Container>
          <LoadingSpinner message="Loading destinations..." />
        </Container>
      </>
    );
  }

  return (
    <>
      <AdminPageHeader
        title="Destinations"
        description="Manage destinations available throughout India."
        action={{
          label: "Add Destination",
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
          data={destinations}
          emptyMessage="No destinations available."
          renderActions={(destination) => (
            <>
              <button
                type="button"
                onClick={() =>
                  openEditModal(destination)
                }
                className="
                  rounded-lg
                  p-2
                  text-slate-500
                  transition-colors
                  hover:bg-slate-100
                  hover:text-[var(--color-primary)]
                "
                aria-label={`Edit ${destination.name}`}
              >
                <HiOutlinePencilSquare size={20} />
              </button>

              <button
                type="button"
                onClick={() =>
                  handleDelete(destination)
                }
                disabled={
                  deletingId === destination.id
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
                aria-label={`Delete ${destination.name}`}
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
          editingDestination
            ? "Edit Destination"
            : "Add Destination"
        }
        description={
          editingDestination
            ? "Update the destination information."
            : "Add a new destination to the tourism platform."
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
                maxLength={100}
                className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
              />
            </div>

            <div>
              <label
                htmlFor="district"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                District
              </label>

              <input
                id="district"
                name="district"
                value={form.district}
                onChange={handleChange}
                maxLength={100}
                className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
              />
            </div>
          </div>

          <div>
            <label
              htmlFor="tagline"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Tagline
            </label>

            <input
              id="tagline"
              name="tagline"
              value={form.tagline}
              onChange={handleChange}
              maxLength={200}
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
              maxLength={1000}
              rows={4}
              className="w-full resize-none rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
            />
          </div>

          <div className="grid gap-5 md:grid-cols-2">
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
                  Select state
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
                htmlFor="type"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Destination Type *
              </label>

              <select
                id="type"
                name="type"
                value={form.type}
                onChange={handleChange}
                required
                className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
              >
                <option value="">
                  Select type
                </option>

                {destinationTypes.map((type) => (
                  <option
                    key={type}
                    value={type}
                  >
                    {type
                      .toLowerCase()
                      .replaceAll("_", " ")}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div>
            <h3 className="mb-4 text-sm font-semibold text-slate-900">
              Location
            </h3>

            <div className="grid gap-5 md:grid-cols-3">
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

              <div>
                <label
                  htmlFor="timezone"
                  className="mb-2 block text-sm font-medium text-slate-700"
                >
                  Timezone
                </label>

                <input
                  id="timezone"
                  name="timezone"
                  value={form.timezone}
                  onChange={handleChange}
                  maxLength={100}
                  placeholder="Asia/Kolkata"
                  className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
                />
              </div>
            </div>
          </div>

          <div>
            <h3 className="mb-4 text-sm font-semibold text-slate-900">
              Images
            </h3>

            <div className="grid gap-5 md:grid-cols-2">
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
                <label
                  htmlFor="coverImageUrl"
                  className="mb-2 block text-sm font-medium text-slate-700"
                >
                  Cover Image URL
                </label>

                <input
                  id="coverImageUrl"
                  name="coverImageUrl"
                  value={form.coverImageUrl}
                  onChange={handleChange}
                  maxLength={500}
                  className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
                />
              </div>
            </div>
          </div>

          <div>
            <h3 className="mb-4 text-sm font-semibold text-slate-900">
              Transport
            </h3>

            <div className="grid gap-5 md:grid-cols-2">
              <div>
                <label
                  htmlFor="nearestAirport"
                  className="mb-2 block text-sm font-medium text-slate-700"
                >
                  Nearest Airport
                </label>

                <input
                  id="nearestAirport"
                  name="nearestAirport"
                  value={form.nearestAirport}
                  onChange={handleChange}
                  maxLength={100}
                  className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
                />
              </div>

              <div>
                <label
                  htmlFor="nearestRailwayStation"
                  className="mb-2 block text-sm font-medium text-slate-700"
                >
                  Nearest Railway Station
                </label>

                <input
                  id="nearestRailwayStation"
                  name="nearestRailwayStation"
                  value={form.nearestRailwayStation}
                  onChange={handleChange}
                  maxLength={100}
                  className="w-full rounded-xl border border-slate-300 px-4 py-3 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
                />
              </div>
            </div>
          </div>

          <div className="grid gap-5 md:grid-cols-3">
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

            <label className="flex cursor-pointer items-center gap-3 md:pt-8">
              <input
                type="checkbox"
                name="featured"
                checked={form.featured}
                onChange={handleChange}
                className="h-5 w-5 rounded border-slate-300 text-[var(--color-primary)] focus:ring-[var(--color-primary)]"
              />

              <span className="text-sm font-medium text-slate-700">
                Featured destination
              </span>
            </label>

            <label className="flex cursor-pointer items-center gap-3 md:pt-8">
              <input
                type="checkbox"
                name="popular"
                checked={form.popular}
                onChange={handleChange}
                className="h-5 w-5 rounded border-slate-300 text-[var(--color-primary)] focus:ring-[var(--color-primary)]"
              />

              <span className="text-sm font-medium text-slate-700">
                Popular destination
              </span>
            </label>
          </div>

          {form.thumbnailUrl && (
            <div>
              <p className="mb-2 text-sm font-medium text-slate-700">
                Thumbnail Preview
              </p>

              <img
                src={form.thumbnailUrl}
                alt="Destination preview"
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
                : editingDestination
                  ? "Update Destination"
                  : "Create Destination"}
            </Button>
          </div>
        </form>
      </AdminModal>
    </>
  );
}