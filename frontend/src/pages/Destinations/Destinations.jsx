import { useEffect, useState } from "react";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";

import DestinationCard from "../../components/destinations/DestinationCard";

import destinationService from "../../services/destinationService";
import stateService from "../../services/stateService";

export default function Destinations() {
  const [destinations, setDestinations] = useState([]);
  const [states, setStates] = useState([]);

  const [filters, setFilters] = useState({
    stateId: "",
    type: "",
    featured: "",
    popular: "",
  });

  const [destinationTypes, setDestinationTypes] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  /*
   * Load states and initial destinations
   */
  useEffect(() => {
    loadInitialData();
  }, []);

  async function loadInitialData() {
    try {
      setLoading(true);
      setError("");

      const [destinationData, stateData] =
        await Promise.all([
          destinationService.getAll(),
          stateService.getAll(),
        ]);

      console.log("Destinations:", destinationData);
      console.log("States:", stateData);

      setDestinations(destinationData);
      setStates(stateData);

      /*
       * Build the destination type dropdown
       * from types that actually exist in the data.
       */
      const types = [
        ...new Set(
          destinationData
            .map((destination) => destination.type)
            .filter(Boolean)
        ),
      ];

      setDestinationTypes(types);
    } catch (err) {
      console.error(err);
      setError("Unable to load destinations.");
    } finally {
      setLoading(false);
    }
  }

  /*
   * Load destinations whenever a filter changes.
   */
  useEffect(() => {
    /*
     * Don't run this on the initial render.
     * Initial data is already loaded by loadInitialData().
     */
    if (
      filters.stateId === "" &&
      filters.type === "" &&
      filters.featured === "" &&
      filters.popular === ""
    ) {
      return;
    }

    loadFilteredDestinations();
  }, [filters]);

  async function loadFilteredDestinations() {
    try {
      setLoading(true);
      setError("");

      const data = await destinationService.getAll({
        stateId: filters.stateId || undefined,
        type: filters.type || undefined,
        featured:
          filters.featured === ""
            ? undefined
            : filters.featured === "true",
        popular:
          filters.popular === ""
            ? undefined
            : filters.popular === "true",
      });

      console.log("Filtered destinations:", data);

      setDestinations(data);
    } catch (err) {
      console.error(err);
      setError("Unable to filter destinations.");
    } finally {
      setLoading(false);
    }
  }

  function handleFilterChange(event) {
    const { name, value } = event.target;

    setFilters((currentFilters) => ({
      ...currentFilters,
      [name]: value,
    }));
  }

  function clearFilters() {
    setFilters({
      stateId: "",
      type: "",
      featured: "",
      popular: "",
    });

    /*
     * Reload all destinations immediately.
     */
    loadAllDestinations();
  }

  async function loadAllDestinations() {
    try {
      setLoading(true);
      setError("");

      const data = await destinationService.getAll();

      setDestinations(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load destinations.");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <Section title="Destinations">
        <LoadingSpinner message="Loading destinations..." />
      </Section>
    );
  }

  if (error) {
    return (
      <Section title="Destinations">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  return (
    <Section
      title="Explore Destinations"
      subtitle="Discover remarkable places, landscapes and experiences across India."
    >
      {/* Filters */}
      <div className="mb-10 rounded-2xl border border-[var(--color-border)] bg-white p-6 shadow-sm">
        <div className="flex items-center justify-between gap-4">
          <div>
            <h2 className="text-lg font-bold">
              Filter Destinations
            </h2>

            <p className="mt-1 text-sm text-slate-500">
              Narrow down destinations based on your preferences.
            </p>
          </div>

          <button
            type="button"
            onClick={clearFilters}
            className="text-sm font-medium text-[var(--color-primary)] hover:underline"
          >
            Clear Filters
          </button>
        </div>

        <div className="mt-6 grid gap-4 sm:grid-cols-2 lg:grid-cols-4">

          {/* State */}
          <div>
            <label
              htmlFor="stateId"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              State
            </label>

            <select
              id="stateId"
              name="stateId"
              value={filters.stateId}
              onChange={handleFilterChange}
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
            >
              <option value="">
                All States
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

          {/* Destination Type */}
          <div>
            <label
              htmlFor="type"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Destination Type
            </label>

            <select
              id="type"
              name="type"
              value={filters.type}
              onChange={handleFilterChange}
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
            >
              <option value="">
                All Types
              </option>

              {destinationTypes.map((type) => (
                <option
                  key={type}
                  value={type}
                >
                  {type.replaceAll("_", " ")}
                </option>
              ))}
            </select>
          </div>

          {/* Featured */}
          <div>
            <label
              htmlFor="featured"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Featured
            </label>

            <select
              id="featured"
              name="featured"
              value={filters.featured}
              onChange={handleFilterChange}
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
            >
              <option value="">
                All
              </option>

              <option value="true">
                Featured
              </option>

              <option value="false">
                Not Featured
              </option>
            </select>
          </div>

          {/* Popular */}
          <div>
            <label
              htmlFor="popular"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Popular
            </label>

            <select
              id="popular"
              name="popular"
              value={filters.popular}
              onChange={handleFilterChange}
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
            >
              <option value="">
                All
              </option>

              <option value="true">
                Popular
              </option>

              <option value="false">
                Not Popular
              </option>
            </select>
          </div>

        </div>
      </div>

      {/* Results */}
      {destinations.length === 0 ? (
        <EmptyState message="No destinations match the selected filters." />
      ) : (
        <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {destinations.map((destination) => (
            <DestinationCard
              key={destination.id}
              destination={destination}
            />
          ))}
        </div>
      )}
    </Section>
  );
}