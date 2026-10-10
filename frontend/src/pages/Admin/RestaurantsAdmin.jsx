import { useEffect, useState } from "react";
import {
  HiOutlineBuildingStorefront,
  HiOutlinePencilSquare,
  HiOutlinePlus,
  HiOutlineTrash,
} from "react-icons/hi2";

import Container from "../../components/common/layout/Container";
import Button from "../../components/common/inputs/Button";
import AdminPageHeader from "../../components/admin/AdminPageHeader";
import AdminTable from "../../components/admin/AdminTable";
import AdminModal from "../../components/admin/AdminModal";

import restaurantService from "../../services/restaurantService";
import destinationService from "../../services/destinationService";

const cuisines = [
  "ASSAMESE",
  "MEGHALAYAN",
  "JAINTIA",
  "KHASI",
  "GARO",
  "NAGA",
  "MANIPURI",
  "MIZO",
  "NORTH_INDIAN",
  "SOUTH_INDIAN",
  "CHINESE",
  "CONTINENTAL",
  "ITALIAN",
  "MEXICAN",
  "CAFE",
  "FAST_FOOD",
  "SEAFOOD",
  "MULTI_CUISINE",
];

const priceRanges = [
  "BUDGET",
  "MID_RANGE",
  "PREMIUM",
];

const DAYS_OF_WEEK = [
  { value: "MONDAY", label: "Monday" },
  { value: "TUESDAY", label: "Tuesday" },
  { value: "WEDNESDAY", label: "Wednesday" },
  { value: "THURSDAY", label: "Thursday" },
  { value: "FRIDAY", label: "Friday" },
  { value: "SATURDAY", label: "Saturday" },
  { value: "SUNDAY", label: "Sunday" },
];

function normalizeTimeForInput(timeStr) {
  if (!timeStr || typeof timeStr !== "string") {
    return "";
  }
  const trimmed = timeStr.trim();
  const parts = trimmed.split(":");
  if (parts.length >= 2) {
    const hours = parts[0].padStart(2, "0");
    const minutes = parts[1].padStart(2, "0");
    return `${hours}:${minutes}`;
  }
  return "";
}

function formatTimeForPayload(timeStr) {
  if (!timeStr || typeof timeStr !== "string") {
    return null;
  }
  const trimmed = timeStr.trim();
  return trimmed ? trimmed : null;
}

function formatEnum(val) {
  if (!val) {
    return "—";
  }
  return val
    .toLowerCase()
    .split("_")
    .map(
      (word) =>
        word.charAt(0).toUpperCase() +
        word.slice(1)
    )
    .join(" ");
}

const emptyForm = {
  name: "",
  description: "",
  cuisine: "MULTI_CUISINE",
  vegetarian: false,
  rating: "",
  priceRange: "MID_RANGE",
  openingHours: "",
  openingTime: "",
  closingTime: "",
  secondOpeningTime: "",
  secondClosingTime: "",
  closedDays: [],
  phone: "",
  website: "",
  imageUrl: "",
  destinationId: "",
};

export default function RestaurantsAdmin() {
  const [restaurants, setRestaurants] = useState([]);
  const [destinations, setDestinations] = useState([]);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingRestaurant, setEditingRestaurant] = useState(null);

  const [form, setForm] = useState(emptyForm);

  const loadData = async () => {
    try {
      setLoading(true);
      setError("");

      const [restaurantData, destinationData] = await Promise.all([
        restaurantService.getAll(),
        destinationService.getAll(),
      ]);

      setRestaurants(restaurantData);
      setDestinations(destinationData);
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message || "Failed to load restaurants."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    let ignore = false;

    async function init() {
      try {
        setError("");
        const [restaurantData, destinationData] = await Promise.all([
          restaurantService.getAll(),
          destinationService.getAll(),
        ]);

        if (!ignore) {
          setRestaurants(restaurantData);
          setDestinations(destinationData);
        }
      } catch (err) {
        if (!ignore) {
          console.error(err);
          setError(
            err.response?.data?.message || "Failed to load restaurants."
          );
        }
      } finally {
        if (!ignore) {
          setLoading(false);
        }
      }
    }

    init();

    return () => {
      ignore = true;
    };
  }, []);

  const openCreateModal = () => {
    setEditingRestaurant(null);
    setForm(emptyForm);
    setError("");
    setIsModalOpen(true);
  };

  const openEditModal = (restaurant) => {
    setEditingRestaurant(restaurant);

    setForm({
      name: restaurant.name || "",
      description: restaurant.description || "",
      cuisine: restaurant.cuisine || "MULTI_CUISINE",
      vegetarian: Boolean(restaurant.vegetarian),
      rating: restaurant.rating ?? "",
      priceRange: restaurant.priceRange || "MID_RANGE",
      openingHours: restaurant.openingHours || "",
      openingTime: normalizeTimeForInput(restaurant.openingTime),
      closingTime: normalizeTimeForInput(restaurant.closingTime),
      secondOpeningTime: normalizeTimeForInput(restaurant.secondOpeningTime),
      secondClosingTime: normalizeTimeForInput(restaurant.secondClosingTime),
      closedDays: restaurant.closedDays
        ? Array.from(restaurant.closedDays)
        : [],
      phone: restaurant.phone || "",
      website: restaurant.website || "",
      imageUrl: restaurant.imageUrl || "",
      destinationId:
        restaurant.destinationId !== null && restaurant.destinationId !== undefined
          ? String(restaurant.destinationId)
          : "",
    });

    setError("");
    setIsModalOpen(true);
  };

  const closeModal = () => {
    if (saving) {
      return;
    }

    setIsModalOpen(false);
    setEditingRestaurant(null);
    setForm(emptyForm);
    setError("");
  };

  const handleChange = (event) => {
    const { name, value, type, checked } = event.target;

    setForm((current) => ({
      ...current,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const toggleClosedDay = (dayValue) => {
    setForm((current) => {
      const exists = current.closedDays.includes(dayValue);
      return {
        ...current,
        closedDays: exists
          ? current.closedDays.filter((d) => d !== dayValue)
          : [...current.closedDays, dayValue],
      };
    });
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      setSaving(true);
      setError("");

      if (!form.name.trim()) {
        setError("Restaurant name is required.");
        return;
      }

      if (!form.destinationId) {
        setError("Please select a destination.");
        return;
      }

      const restaurantData = {
        name: form.name.trim(),
        description: form.description.trim() || null,
        cuisine: form.cuisine,
        vegetarian: Boolean(form.vegetarian),
        rating:
          form.rating === "" || form.rating === null
            ? null
            : Number(form.rating),
        priceRange: form.priceRange,
        openingHours: form.openingHours.trim() || null,
        openingTime: formatTimeForPayload(form.openingTime),
        closingTime: formatTimeForPayload(form.closingTime),
        secondOpeningTime: formatTimeForPayload(form.secondOpeningTime),
        secondClosingTime: formatTimeForPayload(form.secondClosingTime),
        closedDays: form.closedDays || [],
        phone: form.phone.trim() || null,
        website: form.website.trim() || null,
        imageUrl: form.imageUrl.trim() || null,
        destinationId: Number(form.destinationId),
      };

      if (editingRestaurant) {
        await restaurantService.update(
          editingRestaurant.id,
          restaurantData
        );
      } else {
        await restaurantService.create(restaurantData);
      }

      closeModal();
      await loadData();
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message || "Failed to save restaurant."
      );
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (restaurant) => {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${restaurant.name}"?`
    );

    if (!confirmed) {
      return;
    }

    try {
      setError("");
      await restaurantService.delete(restaurant.id);
      await loadData();
    } catch (err) {
      console.error(err);
      setError(
        err.response?.data?.message || "Failed to delete restaurant."
      );
    }
  };

  const columns = [
    {
      key: "name",
      label: "Restaurant",
      headerClassName: "min-w-[260px]",
      cellClassName: "max-w-md",
      render: (restaurant) => (
        <div className="min-w-0">
          <p className="font-semibold text-slate-800">
            {restaurant.name}
          </p>

          {restaurant.description && (
            <p className="mt-1 max-w-md break-words text-xs leading-5 text-slate-500">
              {restaurant.description}
            </p>
          )}
        </div>
      ),
    },
    {
      key: "cuisine",
      label: "Cuisine",
      render: (restaurant) => (
        <span className="rounded-full bg-slate-100 px-3 py-1 text-xs font-semibold text-slate-700">
          {formatEnum(restaurant.cuisine)}
        </span>
      ),
    },
    {
      key: "destinationName",
      label: "Destination",
      render: (restaurant) => (
        <div>
          <p className="font-medium text-slate-700">
            {restaurant.destinationName}
          </p>

          {restaurant.stateName && (
            <p className="mt-1 text-xs text-slate-500">
              {restaurant.stateName}
            </p>
          )}
        </div>
      ),
    },
    {
      key: "priceRange",
      label: "Price Range",
      render: (restaurant) => (
        <span className="font-medium text-slate-700">
          {formatEnum(restaurant.priceRange)}
        </span>
      ),
    },
    {
      key: "rating",
      label: "Rating",
      render: (restaurant) =>
        restaurant.rating != null
          ? `${restaurant.rating}/5`
          : "—",
    },
    {
      key: "vegetarian",
      label: "Type",
      render: (restaurant) => (
        <span
          className={`
            rounded-full
            px-3
            py-1
            text-xs
            font-semibold
            ${
              restaurant.vegetarian
                ? "bg-emerald-50 text-emerald-700"
                : "bg-amber-50 text-amber-700"
            }
          `}
        >
          {restaurant.vegetarian ? "Pure Veg" : "Non-Veg / Mixed"}
        </span>
      ),
    },
    {
      key: "schedule",
      label: "Hours",
      render: (restaurant) => {
        if (restaurant.openingTime && restaurant.closingTime) {
          return (
            <div className="text-xs">
              <p className="font-medium text-slate-700">
                {normalizeTimeForInput(restaurant.openingTime)} - {normalizeTimeForInput(restaurant.closingTime)}
              </p>
              {restaurant.secondOpeningTime && restaurant.secondClosingTime && (
                <p className="text-slate-500">
                  {normalizeTimeForInput(restaurant.secondOpeningTime)} - {normalizeTimeForInput(restaurant.secondClosingTime)}
                </p>
              )}
            </div>
          );
        }
        if (restaurant.openingHours) {
          return (
            <span className="text-xs text-slate-600">
              {restaurant.openingHours}
            </span>
          );
        }
        return <span className="text-xs text-slate-400">—</span>;
      },
    },
  ];

  return (
    <main className="min-h-screen bg-slate-50">
      <AdminPageHeader
        title="Restaurants"
        description="Manage restaurants, cuisines, dining options, and operating schedules."
        action={{
          label: "Add Restaurant",
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
                Loading restaurants...
              </p>
            </div>
          ) : (
            <AdminTable
              columns={columns}
              data={restaurants}
              emptyMessage="No restaurants available."
              renderActions={(restaurant) => (
                <>
                  <button
                    type="button"
                    onClick={() => openEditModal(restaurant)}
                    className="
                      rounded-lg
                      p-2
                      text-slate-500
                      transition-colors
                      hover:bg-emerald-50
                      hover:text-[var(--color-primary)]
                    "
                    aria-label={`Edit ${restaurant.name}`}
                  >
                    <HiOutlinePencilSquare size={20} />
                  </button>

                  <button
                    type="button"
                    onClick={() => handleDelete(restaurant)}
                    className="
                      rounded-lg
                      p-2
                      text-slate-500
                      transition-colors
                      hover:bg-red-50
                      hover:text-red-600
                    "
                    aria-label={`Delete ${restaurant.name}`}
                  >
                    <HiOutlineTrash size={20} />
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
          editingRestaurant
            ? "Edit Restaurant"
            : "Add Restaurant"
        }
        description={
          editingRestaurant
            ? "Update the restaurant information and operating schedule."
            : "Add a new restaurant to the tourism platform."
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
              label="Restaurant Name"
              name="name"
              value={form.name}
              onChange={handleChange}
              required
              placeholder="e.g. Paradise Restaurant"
            />

            <div>
              <label
                htmlFor="destinationId"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Destination <span className="text-red-500">*</span>
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

                {destinations.map((destination) => (
                  <option
                    key={destination.id}
                    value={destination.id}
                  >
                    {destination.name}
                  </option>
                ))}
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
              placeholder="Describe the restaurant, specialties, ambience..."
              className={inputClass}
            />
          </div>

          <div className="grid gap-5 md:grid-cols-3">
            <div>
              <label
                htmlFor="cuisine"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Cuisine <span className="text-red-500">*</span>
              </label>

              <select
                id="cuisine"
                name="cuisine"
                value={form.cuisine}
                onChange={handleChange}
                required
                className={inputClass}
              >
                {cuisines.map((cuisine) => (
                  <option
                    key={cuisine}
                    value={cuisine}
                  >
                    {formatEnum(cuisine)}
                  </option>
                ))}
              </select>
            </div>

            <div>
              <label
                htmlFor="priceRange"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Price Range <span className="text-red-500">*</span>
              </label>

              <select
                id="priceRange"
                name="priceRange"
                value={form.priceRange}
                onChange={handleChange}
                required
                className={inputClass}
              >
                {priceRanges.map((range) => (
                  <option
                    key={range}
                    value={range}
                  >
                    {formatEnum(range)}
                  </option>
                ))}
              </select>
            </div>

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
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <FormField
              label="Contact Number"
              name="phone"
              value={form.phone}
              onChange={handleChange}
              placeholder="+91 9876543210"
            />

            <FormField
              label="Website"
              name="website"
              type="url"
              value={form.website}
              onChange={handleChange}
              placeholder="https://example.com"
            />
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <FormField
              label="Image URL"
              name="imageUrl"
              type="url"
              value={form.imageUrl}
              onChange={handleChange}
              placeholder="https://example.com/image.jpg"
            />

            <FormField
              label="Legacy Opening Hours"
              name="openingHours"
              value={form.openingHours}
              onChange={handleChange}
              placeholder="e.g. 08:00 - 22:00"
            />
          </div>

          <label className="flex items-center gap-3 rounded-xl border border-slate-200 bg-slate-50 px-4 py-3">
            <input
              type="checkbox"
              name="vegetarian"
              checked={form.vegetarian}
              onChange={handleChange}
              className="h-4 w-4 rounded border-slate-300 text-[var(--color-primary)] focus:ring-[var(--color-primary)]"
            />

            <span>
              <span className="block text-sm font-medium text-slate-700">
                Pure Vegetarian
              </span>

              <span className="block text-xs text-slate-500">
                Check if this restaurant serves exclusively vegetarian food.
              </span>
            </span>
          </label>

          <div className="space-y-4 rounded-xl border border-slate-200 bg-slate-50/50 p-4">
            <div>
              <h3 className="text-sm font-semibold text-slate-800">
                Operating Schedule & Sessions
              </h3>

              <p className="mt-1 text-xs text-slate-500">
                Optional structured daily operating hours (24-hour format) and weekly closed days.
              </p>
            </div>

            <div>
              <h4 className="text-xs font-semibold uppercase tracking-wider text-slate-500">
                Primary Session (Session 1)
              </h4>

              <div className="mt-2 grid gap-5 md:grid-cols-2">
                <FormField
                  label="Opening Time"
                  name="openingTime"
                  type="time"
                  value={form.openingTime}
                  onChange={handleChange}
                />

                <FormField
                  label="Closing Time"
                  name="closingTime"
                  type="time"
                  value={form.closingTime}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div>
              <h4 className="text-xs font-semibold uppercase tracking-wider text-slate-500">
                Second Session (Optional split / dinner session)
              </h4>

              <div className="mt-2 grid gap-5 md:grid-cols-2">
                <FormField
                  label="Second Opening Time"
                  name="secondOpeningTime"
                  type="time"
                  value={form.secondOpeningTime}
                  onChange={handleChange}
                />

                <FormField
                  label="Second Closing Time"
                  name="secondClosingTime"
                  type="time"
                  value={form.secondClosingTime}
                  onChange={handleChange}
                />
              </div>
            </div>

            <div>
              <label className="mb-2 block text-sm font-medium text-slate-700">
                Closed Days
              </label>

              <div className="grid grid-cols-2 gap-2 sm:grid-cols-4 lg:grid-cols-7">
                {DAYS_OF_WEEK.map((day) => (
                  <label
                    key={day.value}
                    className="flex cursor-pointer items-center gap-2 rounded-xl border border-slate-200 bg-white px-3 py-2.5 transition hover:bg-slate-50"
                  >
                    <input
                      type="checkbox"
                      checked={form.closedDays.includes(day.value)}
                      onChange={() => toggleClosedDay(day.value)}
                      className="h-4 w-4 rounded border-slate-300 text-[var(--color-primary)] focus:ring-[var(--color-primary)]"
                    />

                    <span className="text-xs font-medium text-slate-700">
                      {day.label}
                    </span>
                  </label>
                ))}
              </div>
            </div>
          </div>

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
              <HiOutlineBuildingStorefront size={20} />

              {saving
                ? "Saving..."
                : editingRestaurant
                  ? "Update Restaurant"
                  : "Add Restaurant"}
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
        {label} {required && <span className="text-red-500">*</span>}
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

