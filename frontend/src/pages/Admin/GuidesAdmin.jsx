import { useEffect, useState } from "react";
import {
  HiOutlinePencilSquare,
  HiOutlinePlus,
  HiOutlineTrash,
  HiOutlineUserGroup,
} from "react-icons/hi2";

import Container from "../../components/common/layout/Container";
import Button from "../../components/common/inputs/Button";
import AdminPageHeader from "../../components/admin/AdminPageHeader";
import AdminTable from "../../components/admin/AdminTable";
import AdminModal from "../../components/admin/AdminModal";

import guideService from "../../services/guideService";
import destinationService from "../../services/destinationService";

const languages = [
  "ENGLISH",
  "HINDI",
  "ASSAMESE",
  "KHASI",
  "GARO",
  "BENGALI",
  "TAMIL",
  "TELUGU",
  "KANNADA",
  "MALAYALAM",
  "MARATHI",
  "GUJARATI",
  "PUNJABI",
  "ODIA",
  "NEPALI",
  "SANSKRIT",
  "FRENCH",
  "GERMAN",
  "SPANISH",
  "JAPANESE",
  "CHINESE",
];

const emptyForm = {
  name: "",
  bio: "",
  phone: "",
  email: "",
  languages: [],
  yearsOfExperience: "",
  pricePerDay: "",
  rating: "",
  available: true,
  licenseNumber: "",
  providesTransport: false,
  imageUrl: "",
  destinationId: "",
};

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

export default function GuidesAdmin() {
  const [guides, setGuides] = useState([]);
  const [destinations, setDestinations] =
    useState([]);

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const [error, setError] = useState("");

  const [isModalOpen, setIsModalOpen] =
    useState(false);

  const [editingGuide, setEditingGuide] =
    useState(null);

  const [form, setForm] = useState(emptyForm);

  const loadData = async () => {
    try {
      setLoading(true);
      setError("");

      const [
        guideData,
        destinationData,
      ] = await Promise.all([
        guideService.getAll(),
        destinationService.getAll(),
      ]);

      setGuides(guideData);
      setDestinations(destinationData);
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
          "Failed to load guides."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const openCreateModal = () => {
    setEditingGuide(null);
    setForm(emptyForm);
    setError("");
    setIsModalOpen(true);
  };

  const openEditModal = (guide) => {
    setEditingGuide(guide);

    setForm({
      name: guide.name || "",
      bio: guide.bio || "",
      phone: guide.phone || "",
      email: guide.email || "",
      languages: guide.languages || [],
      yearsOfExperience:
        guide.yearsOfExperience ?? "",
      pricePerDay:
        guide.pricePerDay ?? "",
      rating: guide.rating ?? "",
      available:
        guide.available ?? true,
      licenseNumber:
        guide.licenseNumber || "",
      providesTransport:
        guide.providesTransport ?? false,
      imageUrl: guide.imageUrl || "",
      destinationId:
        guide.destinationId || "",
    });

    setError("");
    setIsModalOpen(true);
  };

  const closeModal = () => {
    if (saving) {
      return;
    }

    setIsModalOpen(false);
    setEditingGuide(null);
    setForm(emptyForm);
    setError("");
  };

  const handleChange = (event) => {
    const {
      name,
      value,
      type,
      checked,
    } = event.target;

    setForm((current) => ({
      ...current,
      [name]:
        type === "checkbox"
          ? checked
          : value,
    }));
  };

  const handleLanguageChange = (
    event
  ) => {
    const { value, checked } =
      event.target;

    setForm((current) => ({
      ...current,
      languages: checked
        ? [
            ...current.languages,
            value,
          ]
        : current.languages.filter(
            (language) =>
              language !== value
          ),
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    try {
      setSaving(true);
      setError("");

      if (!form.destinationId) {
        setError(
          "Please select a destination."
        );
        return;
      }

      if (form.languages.length === 0) {
        setError(
          "Please select at least one language."
        );
        return;
      }

      const guideData = {
        name: form.name.trim(),
        bio: form.bio.trim() || null,
        phone: form.phone.trim(),
        email: form.email.trim(),
        languages: form.languages,
        yearsOfExperience:
          Number(form.yearsOfExperience),
        pricePerDay:
          Number(form.pricePerDay),
        rating:
          form.rating === ""
            ? null
            : Number(form.rating),
        available: form.available,
        licenseNumber:
          form.licenseNumber.trim(),
        providesTransport:
          form.providesTransport,
        imageUrl:
          form.imageUrl.trim() || null,
        destinationId:
          Number(form.destinationId),
      };

      if (editingGuide) {
        await guideService.update(
          editingGuide.id,
          guideData
        );
      } else {
        await guideService.create(
          guideData
        );
      }

      closeModal();
      await loadData();
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
          "Failed to save guide."
      );
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (guide) => {
    const confirmed = window.confirm(
      `Are you sure you want to delete "${guide.name}"?`
    );

    if (!confirmed) {
      return;
    }

    try {
      setError("");

      await guideService.delete(
        guide.id
      );

      await loadData();
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
          "Failed to delete guide."
      );
    }
  };

  const columns = [
    {
      key: "name",
      label: "Guide",
      headerClassName:
        "min-w-[260px]",
      cellClassName: "max-w-md",
      render: (guide) => (
        <div className="min-w-0">
          <p className="font-semibold text-slate-800">
            {guide.name}
          </p>

          {guide.bio && (
            <p className="mt-1 max-w-md break-words text-xs leading-5 text-slate-500">
              {guide.bio}
            </p>
          )}
        </div>
      ),
    },
    {
      key: "destinationName",
      label: "Destination",
      render: (guide) => (
        <div>
          <p className="font-medium text-slate-700">
            {guide.destinationName}
          </p>

          {guide.stateName && (
            <p className="mt-1 text-xs text-slate-500">
              {guide.stateName}
            </p>
          )}
        </div>
      ),
    },
    {
      key: "languages",
      label: "Languages",
      headerClassName:
        "min-w-[180px]",
      render: (guide) => (
        <div className="flex flex-wrap gap-1.5">
          {guide.languages?.map(
            (language) => (
              <span
                key={language}
                className="
                  rounded-full
                  bg-emerald-50
                  px-2.5
                  py-1
                  text-xs
                  font-medium
                  text-emerald-700
                "
              >
                {formatLanguage(
                  language
                )}
              </span>
            )
          )}
        </div>
      ),
    },
    {
      key: "yearsOfExperience",
      label: "Experience",
      render: (guide) =>
        `${guide.yearsOfExperience} years`,
    },
    {
      key: "pricePerDay",
      label: "Price / Day",
      render: (guide) => (
        <span className="font-medium text-slate-700">
          ₹
          {Number(
            guide.pricePerDay
          ).toLocaleString("en-IN")}
        </span>
      ),
    },
    {
      key: "rating",
      label: "Rating",
      render: (guide) =>
        guide.rating != null
          ? `${guide.rating}/5`
          : "—",
    },
    {
      key: "available",
      label: "Status",
      render: (guide) => (
        <span
          className={`
            rounded-full
            px-3
            py-1
            text-xs
            font-semibold
            ${
              guide.available
                ? "bg-emerald-50 text-emerald-700"
                : "bg-slate-100 text-slate-500"
            }
          `}
        >
          {guide.available
            ? "Available"
            : "Unavailable"}
        </span>
      ),
    },
    {
      key: "providesTransport",
      label: "Transport",
      render: (guide) =>
        guide.providesTransport
          ? "Yes"
          : "No",
    },
  ];

  return (
    <main className="min-h-screen bg-slate-50">
      <AdminPageHeader
        title="Guides"
        description="Manage registered tour guides, their languages, pricing, availability, and services."
        action={{
          label: "Add Guide",
          icon: (
            <HiOutlinePlus size={20} />
          ),
          onClick:
            openCreateModal,
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
                Loading guides...
              </p>
            </div>
          ) : (
            <AdminTable
              columns={columns}
              data={guides}
              emptyMessage="No guides available."
              renderActions={(
                guide
              ) => (
                <>
                  <button
                    type="button"
                    onClick={() =>
                      openEditModal(
                        guide
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
                    aria-label={`Edit ${guide.name}`}
                  >
                    <HiOutlinePencilSquare
                      size={20}
                    />
                  </button>

                  <button
                    type="button"
                    onClick={() =>
                      handleDelete(
                        guide
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
                    aria-label={`Delete ${guide.name}`}
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
          editingGuide
            ? "Edit Guide"
            : "Add Guide"
        }
        description={
          editingGuide
            ? "Update the guide information."
            : "Register a new tour guide."
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
              label="Guide Name"
              name="name"
              value={form.name}
              onChange={handleChange}
              required
              placeholder="e.g. Rohan Das"
            />

            <FormField
              label="Phone"
              name="phone"
              value={form.phone}
              onChange={handleChange}
              required
              placeholder="+91 9876543210"
            />
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <FormField
              label="Email"
              name="email"
              type="email"
              value={form.email}
              onChange={handleChange}
              required
              placeholder="guide@example.com"
            />

            <FormField
              label="License Number"
              name="licenseNumber"
              value={form.licenseNumber}
              onChange={handleChange}
              required
              placeholder="MG-GUIDE-2026-001"
            />
          </div>

          <div>
            <label
              htmlFor="bio"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Bio
            </label>

            <textarea
              id="bio"
              name="bio"
              value={form.bio}
              onChange={handleChange}
              rows={4}
              placeholder="Tell users about the guide's experience and expertise..."
              className={inputClass}
            />
          </div>

          <div>
            <label className="mb-3 block text-sm font-medium text-slate-700">
              Languages
            </label>

            <div className="grid max-h-64 gap-2 overflow-y-auto rounded-xl border border-slate-200 bg-slate-50 p-4 sm:grid-cols-2 lg:grid-cols-3">
              {languages.map(
                (language) => (
                  <label
                    key={language}
                    className="
                      flex
                      cursor-pointer
                      items-center
                      gap-2
                      rounded-lg
                      px-3
                      py-2
                      transition-colors
                      hover:bg-white
                    "
                  >
                    <input
                      type="checkbox"
                      value={language}
                      checked={form.languages.includes(
                        language
                      )}
                      onChange={
                        handleLanguageChange
                      }
                      className="
                        h-4
                        w-4
                        rounded
                        border-slate-300
                        text-[var(--color-primary)]
                        focus:ring-[var(--color-primary)]
                      "
                    />

                    <span className="text-sm text-slate-700">
                      {formatLanguage(
                        language
                      )}
                    </span>
                  </label>
                )
              )}
            </div>

            {form.languages.length > 0 && (
              <p className="mt-2 text-xs text-slate-500">
                Selected:{" "}
                {form.languages
                  .map(
                    formatLanguage
                  )
                  .join(", ")}
              </p>
            )}
          </div>

          <div className="grid gap-5 md:grid-cols-3">
            <FormField
              label="Years of Experience"
              name="yearsOfExperience"
              type="number"
              value={
                form.yearsOfExperience
              }
              onChange={handleChange}
              required
              min="0"
              step="1"
              placeholder="8"
            />

            <FormField
              label="Price Per Day"
              name="pricePerDay"
              type="number"
              value={
                form.pricePerDay
              }
              onChange={handleChange}
              required
              min="0"
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
              placeholder="4.7"
            />
          </div>

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
              value={
                form.destinationId
              }
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
                    key={
                      destination.id
                    }
                    value={
                      destination.id
                    }
                  >
                    {destination.name}
                  </option>
                )
              )}
            </select>
          </div>

          <FormField
            label="Image URL"
            name="imageUrl"
            type="url"
            value={form.imageUrl}
            onChange={handleChange}
            placeholder="https://example.com/guide.jpg"
          />

          <div className="grid gap-4 md:grid-cols-2">
            <label className="flex items-center gap-3 rounded-xl border border-slate-200 bg-slate-50 px-4 py-3">
              <input
                type="checkbox"
                name="available"
                checked={
                  form.available
                }
                onChange={
                  handleChange
                }
                className="
                  h-4
                  w-4
                  rounded
                  border-slate-300
                  text-[var(--color-primary)]
                  focus:ring-[var(--color-primary)]
                "
              />

              <span>
                <span className="block text-sm font-medium text-slate-700">
                  Available
                </span>

                <span className="block text-xs text-slate-500">
                  Allow users to book this guide.
                </span>
              </span>
            </label>

            <label className="flex items-center gap-3 rounded-xl border border-slate-200 bg-slate-50 px-4 py-3">
              <input
                type="checkbox"
                name="providesTransport"
                checked={
                  form.providesTransport
                }
                onChange={
                  handleChange
                }
                className="
                  h-4
                  w-4
                  rounded
                  border-slate-300
                  text-[var(--color-primary)]
                  focus:ring-[var(--color-primary)]
                "
              />

              <span>
                <span className="block text-sm font-medium text-slate-700">
                  Provides Transport
                </span>

                <span className="block text-xs text-slate-500">
                  Guide can provide transport services.
                </span>
              </span>
            </label>
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
              <HiOutlineUserGroup
                size={20}
              />

              {saving
                ? "Saving..."
                : editingGuide
                  ? "Update Guide"
                  : "Add Guide"}
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

function formatLanguage(language) {
  if (!language) {
    return "";
  }

  return language
    .toLowerCase()
    .replace(/_/g, " ")
    .replace(
      /\b\w/g,
      (letter) =>
        letter.toUpperCase()
    );
}