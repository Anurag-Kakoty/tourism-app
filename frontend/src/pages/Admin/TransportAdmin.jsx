import { useEffect, useState } from "react";
import {
  HiOutlinePencilSquare,
  HiOutlinePlus,
  HiOutlineTrash,
  HiOutlineTruck,
} from "react-icons/hi2";

import Container from "../../components/common/layout/Container";
import Button from "../../components/common/inputs/Button";
import AdminPageHeader from "../../components/admin/AdminPageHeader";
import AdminTable from "../../components/admin/AdminTable";
import AdminModal from "../../components/admin/AdminModal";

import transportService from "../../services/transportService";
import destinationService from "../../services/destinationService";

const transportTypes = [
  "AIR",
  "TRAIN",
  "BUS",
  "CAB",
  "SELF_DRIVE",
  "FERRY",
];

const emptyForm = {
  type: "BUS",
  providerName: "",
  pickupLocation: "",
  dropLocation: "",
  estimatedDuration: "",
  estimatedFare: "",
  contactNumber: "",
  website: "",
  bookingUrl: "",
  available: true,
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

export default function TransportAdmin() {
  const [transportOptions, setTransportOptions] =
    useState([]);

  const [destinations, setDestinations] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [saving, setSaving] =
    useState(false);

  const [error, setError] =
    useState("");

  const [isModalOpen, setIsModalOpen] =
    useState(false);

  const [editingTransport, setEditingTransport] =
    useState(null);

  const [form, setForm] =
    useState(emptyForm);

  const loadData = async () => {
    try {
      setLoading(true);
      setError("");

      const [
        transportData,
        destinationData,
      ] = await Promise.all([
        transportService.getAll(),
        destinationService.getAll(),
      ]);

      setTransportOptions(
        transportData
      );

      setDestinations(
        destinationData
      );
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
          "Failed to load transport options."
      );
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const openCreateModal = () => {
    setEditingTransport(null);
    setForm(emptyForm);
    setError("");
    setIsModalOpen(true);
  };

  const openEditModal = (
    transport
  ) => {
    setEditingTransport(transport);

    setForm({
      type:
        transport.type || "BUS",
      providerName:
        transport.providerName || "",
      pickupLocation:
        transport.pickupLocation || "",
      dropLocation:
        transport.dropLocation || "",
      estimatedDuration:
        transport.estimatedDuration ||
        "",
      estimatedFare:
        transport.estimatedFare ?? "",
      contactNumber:
        transport.contactNumber || "",
      website:
        transport.website || "",
      bookingUrl:
        transport.bookingUrl || "",
      available:
        transport.available ?? true,
      destinationId:
        transport.destinationId || "",
    });

    setError("");
    setIsModalOpen(true);
  };

  const closeModal = () => {
    if (saving) {
      return;
    }

    setIsModalOpen(false);
    setEditingTransport(null);
    setForm(emptyForm);
    setError("");
  };

  const handleChange = (
    event
  ) => {
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

  const handleSubmit = async (
    event
  ) => {
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

      const transportData = {
        type: form.type,
        providerName:
          form.providerName.trim(),
        pickupLocation:
          form.pickupLocation.trim(),
        dropLocation:
          form.dropLocation.trim(),
        estimatedDuration:
          form.estimatedDuration.trim(),
        estimatedFare:
          Number(form.estimatedFare),
        contactNumber:
          form.contactNumber.trim() ||
          null,
        website:
          form.website.trim() ||
          null,
        bookingUrl:
          form.bookingUrl.trim() ||
          null,
        available:
          form.available,
        destinationId:
          Number(form.destinationId),
      };

      if (editingTransport) {
        await transportService.update(
          editingTransport.id,
          transportData
        );
      } else {
        await transportService.create(
          transportData
        );
      }

      closeModal();
      await loadData();
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
          "Failed to save transport option."
      );
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (
    transport
  ) => {
    const confirmed =
      window.confirm(
        `Are you sure you want to delete "${transport.providerName}"?`
      );

    if (!confirmed) {
      return;
    }

    try {
      setError("");

      await transportService.delete(
        transport.id
      );

      await loadData();
    } catch (err) {
      console.error(err);

      setError(
        err.response?.data?.message ||
          "Failed to delete transport option."
      );
    }
  };

  const columns = [
    {
      key: "type",
      label: "Type",
      render: (transport) => (
        <span className="rounded-full bg-emerald-50 px-3 py-1 text-xs font-semibold text-emerald-700">
          {formatType(
            transport.type
          )}
        </span>
      ),
    },
    {
      key: "providerName",
      label: "Provider",
      headerClassName:
        "min-w-[220px]",
      render: (transport) => (
        <div>
          <p className="font-semibold text-slate-800">
            {
              transport.providerName
            }
          </p>

          {transport.contactNumber && (
            <p className="mt-1 text-xs text-slate-500">
              {
                transport.contactNumber
              }
            </p>
          )}
        </div>
      ),
    },
    {
      key: "route",
      label: "Route",
      headerClassName:
        "min-w-[260px]",
      render: (transport) => (
        <div className="max-w-md">
          <p className="break-words text-sm text-slate-700">
            {
              transport.pickupLocation
            }
          </p>

          <p className="my-1 text-xs text-slate-400">
            ↓
          </p>

          <p className="break-words text-sm text-slate-700">
            {
              transport.dropLocation
            }
          </p>
        </div>
      ),
    },
    {
      key: "estimatedDuration",
      label: "Duration",
    },
    {
      key: "estimatedFare",
      label: "Fare",
      render: (transport) => (
        <span className="font-medium text-slate-700">
          ₹
          {Number(
            transport.estimatedFare
          ).toLocaleString(
            "en-IN"
          )}
        </span>
      ),
    },
    {
      key: "destinationName",
      label: "Destination",
      render: (transport) => (
        <div>
          <p className="font-medium text-slate-700">
            {
              transport.destinationName
            }
          </p>

          {transport.stateName && (
            <p className="mt-1 text-xs text-slate-500">
              {
                transport.stateName
              }
            </p>
          )}
        </div>
      ),
    },
    {
      key: "available",
      label: "Status",
      render: (transport) => (
        <span
          className={`
            rounded-full
            px-3
            py-1
            text-xs
            font-semibold
            ${
              transport.available
                ? "bg-emerald-50 text-emerald-700"
                : "bg-slate-100 text-slate-500"
            }
          `}
        >
          {transport.available
            ? "Available"
            : "Unavailable"}
        </span>
      ),
    },
  ];

  return (
    <main className="min-h-screen bg-slate-50">
      <AdminPageHeader
        title="Transport"
        description="Manage flights, trains, buses, cabs, ferries, and other transport options."
        action={{
          label: "Add Transport",
          icon: (
            <HiOutlinePlus
              size={20}
            />
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
                Loading transport options...
              </p>
            </div>
          ) : (
            <AdminTable
              columns={columns}
              data={transportOptions}
              emptyMessage="No transport options available."
              renderActions={(
                transport
              ) => (
                <>
                  <button
                    type="button"
                    onClick={() =>
                      openEditModal(
                        transport
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
                    aria-label={`Edit ${transport.providerName}`}
                  >
                    <HiOutlinePencilSquare
                      size={20}
                    />
                  </button>

                  <button
                    type="button"
                    onClick={() =>
                      handleDelete(
                        transport
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
                    aria-label={`Delete ${transport.providerName}`}
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
          editingTransport
            ? "Edit Transport"
            : "Add Transport"
        }
        description={
          editingTransport
            ? "Update the transport option."
            : "Add a new transport option."
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
            <div>
              <label
                htmlFor="type"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Transport Type
              </label>

              <select
                id="type"
                name="type"
                value={form.type}
                onChange={
                  handleChange
                }
                required
                className={
                  inputClass
                }
              >
                {transportTypes.map(
                  (type) => (
                    <option
                      key={type}
                      value={type}
                    >
                      {formatType(
                        type
                      )}
                    </option>
                  )
                )}
              </select>
            </div>

            <FormField
              label="Provider Name"
              name="providerName"
              value={
                form.providerName
              }
              onChange={
                handleChange
              }
              required
              placeholder="e.g. Assam State Transport"
            />
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <FormField
              label="Pickup Location"
              name="pickupLocation"
              value={
                form.pickupLocation
              }
              onChange={
                handleChange
              }
              required
              placeholder="e.g. Guwahati"
            />

            <FormField
              label="Drop Location"
              name="dropLocation"
              value={
                form.dropLocation
              }
              onChange={
                handleChange
              }
              required
              placeholder="e.g. Shillong"
            />
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <FormField
              label="Estimated Duration"
              name="estimatedDuration"
              value={
                form.estimatedDuration
              }
              onChange={
                handleChange
              }
              required
              placeholder="e.g. 3 hours 30 minutes"
            />

            <FormField
              label="Estimated Fare"
              name="estimatedFare"
              type="number"
              value={
                form.estimatedFare
              }
              onChange={
                handleChange
              }
              required
              min="0"
              step="0.01"
              placeholder="450"
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
              onChange={
                handleChange
              }
              required
              className={
                inputClass
              }
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
                    {
                      destination.name
                    }
                  </option>
                )
              )}
            </select>
          </div>

          <div className="grid gap-5 md:grid-cols-2">
            <FormField
              label="Contact Number"
              name="contactNumber"
              value={
                form.contactNumber
              }
              onChange={
                handleChange
              }
              placeholder="+91 9876543210"
            />

            <FormField
              label="Website"
              name="website"
              type="url"
              value={
                form.website
              }
              onChange={
                handleChange
              }
              placeholder="https://example.com"
            />
          </div>

          <FormField
            label="Booking URL"
            name="bookingUrl"
            type="url"
            value={
              form.bookingUrl
            }
            onChange={
              handleChange
            }
            placeholder="https://example.com/book"
          />

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
                Make this transport option available to users.
              </span>
            </span>
          </label>

          <div className="flex justify-end gap-3 border-t border-slate-200 pt-5">
            <Button
              type="button"
              variant="outline"
              onClick={
                closeModal
              }
              disabled={saving}
            >
              Cancel
            </Button>

            <Button
              type="submit"
              disabled={saving}
            >
              <HiOutlineTruck
                size={20}
              />

              {saving
                ? "Saving..."
                : editingTransport
                  ? "Update Transport"
                  : "Add Transport"}
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

function formatType(type) {
  if (!type) {
    return "";
  }

  return type
    .toLowerCase()
    .replace(/_/g, " ")
    .replace(
      /\b\w/g,
      (letter) =>
        letter.toUpperCase()
    );
}