import { useEffect, useState } from "react";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";

import StayCard from "../../components/stay/StayCard";

import stayService from "../../services/stayService";
import destinationService from "../../services/destinationService";

export default function Stay() {
  const [stays, setStays] = useState([]);
  const [destinations, setDestinations] = useState([]);

  const [destinationId, setDestinationId] = useState("");
  const [type, setType] = useState("");
  const [available, setAvailable] = useState("");

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadDestinations();
  }, []);

  useEffect(() => {
    loadStays();
  }, [destinationId, type, available]);

  async function loadDestinations() {
    try {
      const data = await destinationService.getAll();

      setDestinations(data);
    } catch (err) {
      console.error(err);
    }
  }

  async function loadStays() {
    try {
      setLoading(true);
      setError("");

      const filters = {};

      if (destinationId) {
        filters.destinationId = Number(destinationId);
      }

      if (type) {
        filters.type = type;
      }

      if (available !== "") {
        filters.available = available === "true";
      }

      const data = await stayService.getAll(filters);

      console.log("Stays:", data);

      setStays(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load stays.");
    } finally {
      setLoading(false);
    }
  }

  function clearFilters() {
    setDestinationId("");
    setType("");
    setAvailable("");
  }

  return (
    <>
      <Section
        title="Places to Stay"
        subtitle="Find comfortable places to stay during your journey."
      >
        {/* Filters */}
        <div className="mb-8 rounded-2xl border border-[var(--color-border)] bg-white p-6 shadow-sm">
          <div className="grid gap-4 md:grid-cols-3">

            {/* Destination */}
            <div>
              <label
                htmlFor="destination"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Destination
              </label>

              <select
                id="destination"
                value={destinationId}
                onChange={(e) =>
                  setDestinationId(e.target.value)
                }
                className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-[var(--color-primary)]"
              >
                <option value="">
                  All destinations
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

            {/* Accommodation Type */}
            <div>
              <label
                htmlFor="type"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Accommodation Type
              </label>

              <select
                id="type"
                value={type}
                onChange={(e) =>
                  setType(e.target.value)
                }
                className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-[var(--color-primary)]"
              >
                <option value="">
                  All types
                </option>

                <option value="HOTEL">
                  Hotel
                </option>

                <option value="RESORT">
                  Resort
                </option>

                <option value="HOMESTAY">
                  Homestay
                </option>

                <option value="GUESTHOUSE">
                  Guesthouse
                </option>

                <option value="HOSTEL">
                  Hostel
                </option>
              </select>
            </div>

            {/* Availability */}
            <div>
              <label
                htmlFor="available"
                className="mb-2 block text-sm font-medium text-slate-700"
              >
                Availability
              </label>

              <select
                id="available"
                value={available}
                onChange={(e) =>
                  setAvailable(e.target.value)
                }
                className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-[var(--color-primary)]"
              >
                <option value="">
                  All
                </option>

                <option value="true">
                  Available
                </option>

                <option value="false">
                  Unavailable
                </option>
              </select>
            </div>
          </div>

          {/* Clear filters */}
          {(destinationId || type || available !== "") && (
            <button
              type="button"
              onClick={clearFilters}
              className="mt-4 text-sm font-medium text-[var(--color-primary)] hover:underline"
            >
              Clear filters
            </button>
          )}
        </div>

        {/* Loading */}
        {loading && (
          <LoadingSpinner message="Loading stays..." />
        )}

        {/* Error */}
        {!loading && error && (
          <ErrorMessage message={error} />
        )}

        {/* Empty */}
        {!loading && !error && stays.length === 0 && (
          <EmptyState message="No stays found matching your filters." />
        )}

        {/* Results */}
        {!loading && !error && stays.length > 0 && (
          <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {stays.map((stay) => (
              <StayCard
                key={stay.id}
                stay={stay}
              />
            ))}
          </div>
        )}
      </Section>
    </>
  );
}