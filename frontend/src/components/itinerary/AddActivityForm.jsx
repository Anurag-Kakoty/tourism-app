import { useEffect, useState } from "react";

import Button from "../common/inputs/Button";
import festivalOccurrenceService from "../../services/festivalOccurrenceService";
import guideService from "../../services/guideService";
import placeService from "../../services/placeService";
import restaurantService from "../../services/restaurantService";
import stayService from "../../services/stayService";
import transportService from "../../services/transportService";

const ACTIVITY_TYPES = [
  {
    value: "ATTRACTION",
    label: "Attraction",
    service: placeService,
  },
  {
    value: "ACCOMMODATION",
    label: "Accommodation",
    service: stayService,
  },
  {
    value: "RESTAURANT",
    label: "Restaurant",
    service: restaurantService,
  },
  {
    value: "GUIDE",
    label: "Guide",
    service: guideService,
  },
  {
    value: "TRANSPORT",
    label: "Transport",
    service: transportService,
  },
  {
    value: "FESTIVAL",
    label: "Festival",
    service: festivalOccurrenceService,
  },
];

const initialForm = {
  dayNumber: "",
  activityType: "ATTRACTION",
  referenceId: "",
  time: "10:00",
  notes: "",
};

function AddActivityForm({
  destinationId,
  dayNumbers,
  startDate,
  onAdd,
  onCancel,
}) {
  const [form, setForm] = useState(initialForm);
  const [activities, setActivities] = useState([]);
  const [loadingActivities, setLoadingActivities] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (dayNumbers.length > 0 && !form.dayNumber) {
      setForm((current) => ({
        ...current,
        dayNumber: String(dayNumbers[0]),
      }));
    }
  }, [dayNumbers, form.dayNumber]);

  useEffect(() => {
    let cancelled = false;

    const loadActivities = async () => {
      setLoadingActivities(true);
      setError("");
      setActivities([]);

      setForm((current) => ({
        ...current,
        referenceId: "",
      }));

      try {
        const selectedType = ACTIVITY_TYPES.find(
          (type) => type.value === form.activityType
        );

        let data;

        if (form.activityType === "FESTIVAL") {
          data = await selectedType.service.getAll();
        } else {
          data = await selectedType.service.getByDestination(
            destinationId
          );
        }

        if (!cancelled) {
          setActivities(Array.isArray(data) ? data : []);
        }
      } catch (err) {
        console.error("Failed to load activities:", err);

        if (!cancelled) {
          setError(
            err.response?.data?.message ||
              "Failed to load activities. Please try again."
          );
        }
      } finally {
        if (!cancelled) {
          setLoadingActivities(false);
        }
      }
    };

    if (destinationId) {
      loadActivities();
    }

    return () => {
      cancelled = true;
    };
  }, [destinationId, form.activityType]);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((current) => ({
      ...current,
      [name]: value,
    }));

    if (name === "activityType") {
      setError("");
    }
  };

  const getActivityName = (activity) => {
    return (
      activity.name ||
      activity.festivalName ||
      activity.title ||
      `Activity ${activity.id}`
    );
  };

  const getDayLabel = (dayNumber) => {
    if (!startDate) {
      return `Day ${dayNumber}`;
    }

    const date = new Date(`${startDate}T00:00:00`);

    date.setDate(date.getDate() + dayNumber - 1);

    return `Day ${dayNumber} — ${date.toLocaleDateString(
      "en-IN",
      {
        day: "numeric",
        month: "short",
        year: "numeric",
      }
    )}`;
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    if (!form.dayNumber) {
      setError("Please select a day.");
      return;
    }

    if (!form.referenceId) {
      setError("Please select an activity.");
      return;
    }

    const selectedActivity = activities.find(
      (activity) =>
        String(activity.id) === String(form.referenceId)
    );

    if (!selectedActivity) {
      setError("The selected activity could not be found.");
      return;
    }

    onAdd({
      dayNumber: Number(form.dayNumber),
      activityType: form.activityType,
      referenceId: Number(form.referenceId),
      referenceName: getActivityName(selectedActivity),
      time: form.time,
      notes: form.notes.trim(),
    });

    setForm({
      ...initialForm,
      dayNumber: form.dayNumber,
    });

    setError("");
  };

  return (
    <div className="rounded-2xl border border-gray-200 bg-white p-6 shadow-sm">
      <div className="mb-6">
        <h3 className="text-xl font-bold text-gray-900">
          Add Activity
        </h3>

        <p className="mt-1 text-sm text-gray-500">
          Choose the day and time for the activity. It will be
          inserted into the correct position automatically.
        </p>
      </div>

      {error && (
        <div className="mb-5 rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
          {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="space-y-5">
        <div>
          <label
            htmlFor="dayNumber"
            className="mb-2 block text-sm font-medium text-gray-700"
          >
            Day
          </label>

          <select
            id="dayNumber"
            name="dayNumber"
            value={form.dayNumber}
            onChange={handleChange}
            required
            className="w-full rounded-xl border border-gray-300 bg-white px-4 py-3 outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
          >
            <option value="">Select a day</option>

            {dayNumbers.map((dayNumber) => (
              <option key={dayNumber} value={dayNumber}>
                {getDayLabel(dayNumber)}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label
            htmlFor="activityType"
            className="mb-2 block text-sm font-medium text-gray-700"
          >
            Activity Type
          </label>

          <select
            id="activityType"
            name="activityType"
            value={form.activityType}
            onChange={handleChange}
            className="w-full rounded-xl border border-gray-300 bg-white px-4 py-3 outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
          >
            {ACTIVITY_TYPES.map((type) => (
              <option key={type.value} value={type.value}>
                {type.label}
              </option>
            ))}
          </select>
        </div>

        <div>
          <label
            htmlFor="referenceId"
            className="mb-2 block text-sm font-medium text-gray-700"
          >
            Activity
          </label>

          <select
            id="referenceId"
            name="referenceId"
            value={form.referenceId}
            onChange={handleChange}
            disabled={loadingActivities}
            required
            className="w-full rounded-xl border border-gray-300 bg-white px-4 py-3 outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20 disabled:bg-gray-100"
          >
            <option value="">
              {loadingActivities
                ? "Loading activities..."
                : "Select an activity"}
            </option>

            {activities.map((activity) => (
              <option key={activity.id} value={activity.id}>
                {getActivityName(activity)}
              </option>
            ))}
          </select>

          {!loadingActivities && activities.length === 0 && (
            <p className="mt-2 text-sm text-gray-500">
              No activities of this type are available for this
              destination.
            </p>
          )}
        </div>

        <div>
          <label
            htmlFor="time"
            className="mb-2 block text-sm font-medium text-gray-700"
          >
            Time
          </label>

          <input
            id="time"
            name="time"
            type="time"
            value={form.time}
            onChange={handleChange}
            required
            className="w-full rounded-xl border border-gray-300 px-4 py-3 outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
          />

          <p className="mt-2 text-xs text-gray-500">
            The activity will be positioned chronologically
            within the selected day.
          </p>
        </div>

        <div>
          <label
            htmlFor="notes"
            className="mb-2 block text-sm font-medium text-gray-700"
          >
            Notes
          </label>

          <textarea
            id="notes"
            name="notes"
            value={form.notes}
            onChange={handleChange}
            rows="3"
            placeholder="Optional notes..."
            className="w-full resize-none rounded-xl border border-gray-300 px-4 py-3 outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
          />
        </div>

        <div className="flex flex-col gap-3 sm:flex-row sm:justify-end">
          <Button
            type="button"
            variant="outline"
            onClick={onCancel}
          >
            Cancel
          </Button>

          <Button
            type="submit"
            disabled={
              loadingActivities || activities.length === 0
            }
          >
            Add Activity
          </Button>
        </div>
      </form>
    </div>
  );
}

export default AddActivityForm;