import { useEffect, useState } from "react";
import {
  HiOutlineBuildingOffice2,
  HiOutlinePencilSquare,
  HiOutlinePlus,
  HiOutlineTrash,
} from "react-icons/hi2";

import Container from "../../components/common/layout/Container";
import Button from "../../components/common/inputs/Button";
import AdminPageHeader from "../../components/admin/AdminPageHeader";
import AdminTable from "../../components/admin/AdminTable";
import AdminModal from "../../components/admin/AdminModal";

import stayService from "../../services/stayService";
import destinationService from "../../services/destinationService";

const accommodationTypes = [
  "HOTEL",
  "RESORT",
  "HOMESTAY",
  "HOSTEL",
  "GUEST_HOUSE",
  "LODGE",
  "APARTMENT",
  "CAMPING",
];

const emptyForm = {
  name: "",
  description: "",
  type: "HOTEL",
  pricePerNight: "",
  rating: "",
  contactNumber: "",
  email: "",
  website: "",
  address: "",
  latitude: "",
  longitude: "",
  imageUrl: "",
  available: true,
  destinationId: "",
};

export default function AccommodationsAdmin() {
  const [accommodations, setAccommodations] = useState([]);
  const [destinations, setDestinations] = useState([]);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingAccommodation, setEditingAccommodation] =
    useState(null);

  const [form, setForm] = useState(emptyForm);

  const loadData = async () => {
    try {
      setLoading(true);
      setError("");

      const [
        accommodationData,
        destinationData,
      ] = await Promise.all([
        stayService.getAll(),
        destinationService.getAll(),
      ]);

      setAccommodations(accommodationData);
      setDestinations(destinationData);
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
          "Failed to load accommodations."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const openCreateModal = () => {
    setEditingAccommodation(null);
    setForm(emptyForm);
    setError("");
    setIsModalOpen(true);
  };

  const openEditModal = (accommodation) => {
    setEditingAccommodation(accommodation);

    setForm({
      name: accommodation.name || "",
      description: accommodation.description || "",
      type: accommodation.type || "HOTEL",
      pricePerNight:
        accommodation.pricePerNight ?? "",
      rating: accommodation.rating ?? "",
      contactNumber:
        accommodation.contactNumber || "",
      email: accommodation.email || "",
      website: accommodation.website || "",
      address: accommodation.address || "",
      latitude: accommodation.latitude ?? "",
      longitude: accommodation.longitude ?? "",
      imageUrl: accommodation.imageUrl || "",
      available:
        accommodation.available ?? true,
      destinationId:
        accommodation.destinationId || "",
    });

    setError("");
    setIsModalOpen(true);
  };

  const closeModal = () => {
    if (saving) {
      return;
    }

    setIsModalOpen(false);
    setEditingAccommodation(null);
    setForm(emptyForm);
    setError("");
  };

  const handleChange = (event) => {
    const { name, value, type, checked } =
      event.target;

    setForm((current) => ({
      ...current,
      [name]:
        type === "checkbox"
          ? checked
          : value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      setSaving(true);
      setError("");

      if (!form.destinationId) {
        setError("Please select a destination.");
        return;
      }

      const accommodationData = {
        name: form.name.trim(),
        description:
          form.description.trim() || null,
        type: form.type,
        pricePerNight:
          Number(form.pricePerNight),
        rating:
          form.rating === ""
            ? null
            : Number(form.rating),
        contactNumber:
          form.contactNumber.trim() || null,
        email:
          form.email.trim() || null,
        website:
          form.website.trim() || null,
        address: form.address.trim(),
        latitude:
          form.latitude === ""
            ? null
            : Number(form.latitude),
        longitude:
          form.longitude === ""
            ? null
            : Number(form.longitude),
        imageUrl:
          form.imageUrl.trim() || null,
        available: form.available,
        destinationId:
          Number(form.destinationId),
      };

      if (editingAccommodation) {
        await stayService.update(
          editingAccommodation.id,
          accommodationData
        );
      } else {
        await stayService.create(
          accommodationData
        );
      }

      closeModal();
      await loadData();
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
          "Failed to save accommodation."
      );
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (accommodation) => {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${accommodation.name}"?`
    );

    if (!confirmed) {
      return;
    }

    try {
      setError("");

      await stayService.delete(
        accommodation.id
      );

      await loadData();
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
          "Failed to delete accommodation."
      );
    }
  };

  const columns = [
    {
      key: "name",
      label: "Accommodation",
      headerClassName: "min-w-[260px]",
      cellClassName: "max-w-md",
      render: (accommodation) => (
        <div className="min-w-0">
          <p className="font-semibold text-slate-800">
            {accommodation.name}
          </p>

          {accommodation.description && (
            <p className="mt-1 max-w-md break-words text-xs leading-5 text-slate-500">
              {accommodation.description}
            </p>
          )}
        </div>
      ),
    },
    {
      key: "type",
      label: "Type",
      render: (accommodation) => (
        <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-700">
          {formatType(accommodation.type)}
        </span>
      ),
    },
    {
      key: "destinationName",
      label: "Destination",
      render: (accommodation) => (
        <div>
          <p className="font-medium text-slate-700">
            {accommodation.destinationName}
          </p>

          {accommodation.stateName && (
            <p className="mt-1 text-xs text-slate-500">
              {accommodation.stateName}
            </p>
          )}
        </div>
      ),
    },
    {
      key: "pricePerNight",
      label: "Price / Night",
      render: (accommodation) => (
        <span className="font-medium text-slate-700">
          ₹{Number(
            accommodation.pricePerNight
          ).toLocaleString("en-IN")}
        </span>
      ),
    },
    {
      key: "rating",
      label: "Rating",
      render: (accommodation) =>
        accommodation.rating != null
          ? `${accommodation.rating}/5`
          : "—",
    },
    {
      key: "available",
      label: "Status",
      render: (accommodation) => (
        <span
          className={`
            rounded-full
            px-3
            py-1
            text-xs
            font-semibold
            ${
              accommodation.available
                ? "bg-emerald-50 text-emerald-700"
                : "bg-slate-100 text-slate-500"
            }
          `}
        >
          {accommodation.available
            ? "Available"
            : "Unavailable"}
        </span>
      ),
    },
  ];

  return (
    <main className="min-h-screen bg-slate-50">
      <AdminPageHeader
        title="Accommodations"
        description="Manage hotels, homestays, resorts, and other accommodation options."
        action={{
          label: "Add Accommodation",
          icon: <HiOutlinePlus size={20} />,
          onClick: openCreateModal,
        }}
      />

      <section className="py-10">
        <Container>
          {error && !isModalOpen && (
            <div className="mb-6 rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {error}
            </div>
          )}

          {loading ? (
            <div className="rounded-2xl border border-slate-200 bg-white px-6 py-12 text-center shadow-sm">
              <p className="text-slate-500">
                Loading accommodations...
              </p>
            </div>
          ) : (
            <AdminTable
              columns={columns}
              data={accommodations}
              emptyMessage="No accommodations available."
              renderActions={(accommodation) => (
                <>
                  <button
                    type="button"
                    onClick={() =>
                      openEditModal(
                        accommodation
                      )
                    }
                    className="
                      rounded-lg
                      p-2
                      text-slate-500
                      transition-colors
                      hover:bg-emerald-50
                      hover:text-[var(--color-primary)]
                    "
                    aria-label={`Edit ${accommodation.name}`}
                  >
                    <HiOutlinePencilSquare
                      size={20}
                    />
                  </button>

                  <button
                    type="button"
                    onClick={() =>
                      handleDelete(
                        accommodation
                      )
                    }
                    className="
                      rounded-lg
                      p-2
                      text-slate-500
                      transition-colors
                      hover:bg-red-50
                      hover:text-red-600
                    "
                    aria-label={`Delete ${accommodation.name}`}
                  >
                    <HiOutlineTrash
                      size={20}
                    />
                  </button>
                </>
              )}
            />
          )}
        </Container>
      </section>

      <AdminModal
        isOpen={isModalOpen}
        onClose={closeModal}
        title={
          editingAccommodation
            ? "Edit Accommodation"
            : "Add Accommodation"
        }
        description={
          editingAccommodation
            ? "Update the accommodation information."
            : "Add a new accommodation to the tourism platform."
        }
        size="xl"
      >
        <form
          onSubmit={handleSubmit}
          className="space-y-6"
        >
          {error && (
            <div className="rounded-xl border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
              {error}
            </div>
          )}

          <div className="grid gap-5 md:grid-cols-2">
            <FormField
              label="Accommodation Name"
              name="name"
              value={form.name}
              onChange={handleChange}
              required
              placeholder="e.g. Pinewood Resort"
            />

            <div>
              <label
                htmlFor="type"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Type
              </label>

              <select
                id="type"
                name="type"
                value={form.type}
                onChange={handleChange}
                required
                className={inputClass}
              >
                {accommodationTypes.map(
                  (type) => (
                    <option
                      key={type}
                      value={type}
                    >
                      {formatType(type)}
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
              rows={4}
              placeholder="Describe the accommodation..."
              className={inputClass}
            />
          </div>

          <div className="grid gap-5 md:grid-cols-3">
            <FormField
              label="Price Per Night"
              name="pricePerNight"
              type="number"
              value={form.pricePerNight}
              onChange={handleChange}
              required
              min="0.01"
              step="0.01"
              placeholder="2500"
            />

            <FormField
              label="Rating"
              name="rating"
              type="number"
              value={form.rating}
              onChange={handleChange}
              min="0"
              max="5"
              step="0.1"
              placeholder="4.5"
            />

            <div>
              <label
                htmlFor="destinationId"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Destination
              </label>

              <select
                id="destinationId"
                name="destinationId"
                value={form.destinationId}
                onChange={handleChange}
                required
                className={inputClass}
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
                    </option>
                  )
                )}
              </select>
            </div>
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <FormField
              label="Contact Number"
              name="contactNumber"
              value={form.contactNumber}
              onChange={handleChange}
              placeholder="+91 9876543210"
            />

            <FormField
              label="Email"
              name="email"
              type="email"
              value={form.email}
              onChange={handleChange}
              placeholder="contact@example.com"
            />
          </div>

          <FormField
            label="Website"
            name="website"
            type="url"
            value={form.website}
            onChange={handleChange}
            placeholder="https://example.com"
          />

          <div>
            <label
              htmlFor="address"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Address
            </label>

            <textarea
              id="address"
              name="address"
              value={form.address}
              onChange={handleChange}
              rows={3}
              required
              placeholder="Full accommodation address"
              className={inputClass}
            />
          </div>

          <div>
            <h3 className="text-sm font-semibold text-slate-700">
              Location
            </h3>

            <p className="mt-1 text-xs text-slate-500">
              Optional geographic coordinates used for
              location-based recommendations.
            </p>

            <div className="mt-3 grid gap-5 md:grid-cols-2">
              <FormField
                label="Latitude"
                name="latitude"
                type="number"
                value={form.latitude}
                onChange={handleChange}
                step="any"
                placeholder="25.3176"
              />

              <FormField
                label="Longitude"
                name="longitude"
                type="number"
                value={form.longitude}
                onChange={handleChange}
                step="any"
                placeholder="82.9739"
              />
            </div>
          </div>

          <FormField
            label="Image URL"
            name="imageUrl"
            type="url"
            value={form.imageUrl}
            onChange={handleChange}
            placeholder="https://example.com/image.jpg"
          />

          <label className="flex items-center gap-3 rounded-xl border border-slate-200 bg-slate-50 px-4 py-3">
            <input
              type="checkbox"
              name="available"
              checked={form.available}
              onChange={handleChange}
              className="h-4 w-4 rounded border-slate-300 text-[var(--color-primary)] focus:ring-[var(--color-primary)]"
            />

            <span>
              <span className="block text-sm font-medium text-slate-700">
                Available
              </span>

              <span className="block text-xs text-slate-500">
                Make this accommodation available
                for users.
              </span>
            </span>
          </label>

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
              <HiOutlineBuildingOffice2
                size={20}
              />

              {saving
                ? "Saving..."
                : editingAccommodation
                  ? "Update Accommodation"
                  : "Add Accommodation"}
            </Button>
          </div>
        </form>
      </AdminModal>
    </main>
  );
}

function FormField({
  label,
  name,
  value,
  onChange,
  type = "text",
  required = false,
  min,
  max,
  step,
  placeholder,
}) {
  return (
    <div>
      <label
        htmlFor={name}
        className="mb-2 block text-sm font-medium text-slate-700"
      >
        {label}
      </label>

      <input
        id={name}
        name={name}
        type={type}
        value={value}
        onChange={onChange}
        required={required}
        min={min}
        max={max}
        step={step}
        placeholder={placeholder}
        className={inputClass}
      />
    </div>
  );
}

const inputClass = `
  w-full
  rounded-xl
  border
  border-slate-300
  bg-white
  px-4
  py-3
  text-sm
  text-slate-700
  outline-none
  transition
  focus:border-[var(--color-primary)]
  focus:ring-2
  focus:ring-[var(--color-primary)]/20
`;

function formatType(type) {
  if (!type) {
    return "—";
  }

  return type
    .toLowerCase()
    .split("_")
    .map(
      (word) =>
        word.charAt(0).toUpperCase() +
        word.slice(1)
    )
    .join(" ");
}