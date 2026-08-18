import { useEffect, useState } from "react";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";

import PlaceCard from "../../components/places/PlaceCard";

import placeService from "../../services/placeService";
import stateService from "../../services/stateService";
import destinationService from "../../services/destinationService";
import experienceService from "../../services/experienceService";

export default function Places() {
  const [places, setPlaces] = useState([]);

  const [states, setStates] = useState([]);
  const [destinations, setDestinations] = useState([]);
  const [experiences, setExperiences] = useState([]);

  const [filters, setFilters] = useState({
    stateId: "",
    destinationId: "",
    experienceId: "",
    featured: false,
  });

  const [loading, setLoading] = useState(true);
  const [filterLoading, setFilterLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    loadFilterOptions();
    loadPlaces();
  }, []);

  async function loadFilterOptions() {
    try {
      const [
        stateData,
        destinationData,
        experienceData,
      ] = await Promise.all([
        stateService.getAll(),
        destinationService.getAll(),
        experienceService.getAll(),
      ]);

      setStates(stateData);
      setDestinations(destinationData);
      setExperiences(experienceData);
    } catch (err) {
      console.error(err);
      setError("Unable to load filter options.");
    }
  }

  async function loadPlaces(currentFilters = filters) {
    try {
      setFilterLoading(true);
      setError("");

      const params = {};

      if (currentFilters.stateId) {
        params.stateId = currentFilters.stateId;
      }

      if (currentFilters.destinationId) {
        params.destinationId = currentFilters.destinationId;
      }

      if (currentFilters.experienceId) {
        params.experienceId = currentFilters.experienceId;
      }

      if (currentFilters.featured) {
        params.featured = true;
      }

      const data = await placeService.getAll(params);

      console.log("Attractions:", data);

      setPlaces(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load attractions.");
    } finally {
      setLoading(false);
      setFilterLoading(false);
    }
  }

  function handleFilterChange(event) {
    const { name, value, type, checked } = event.target;

    const newFilters = {
      ...filters,
      [name]: type === "checkbox" ? checked : value,
    };

    // If state changes, clear the selected destination.
    if (name === "stateId") {
      newFilters.destinationId = "";
    }

    setFilters(newFilters);

    loadPlaces(newFilters);
  }

  function clearFilters() {
    const emptyFilters = {
      stateId: "",
      destinationId: "",
      experienceId: "",
      featured: false,
    };

    setFilters(emptyFilters);
    loadPlaces(emptyFilters);
  }

  const filteredDestinations = filters.stateId
    ? destinations.filter(
        (destination) =>
          String(destination.stateId) ===
          String(filters.stateId)
      )
    : destinations;

  if (loading) {
    return (
      <Section title="Places">
        <LoadingSpinner message="Loading attractions..." />
      </Section>
    );
  }

  if (error && places.length === 0) {
    return (
      <Section title="Places">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  return (
    <Section
      title="Discover Places"
      subtitle="Explore destinations from every corner of India."
    >
      {/* Filters */}
      <div className="mb-10 rounded-2xl border border-[var(--color-border)] bg-white p-6 shadow-sm">
        <div className="flex items-center justify-between gap-4">
          <div>
            <h2 className="text-lg font-bold">
              Find Places
            </h2>

            <p className="mt-1 text-sm text-slate-500">
              Filter attractions based on your interests.
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

        <div className="mt-6 grid gap-4 md:grid-cols-2 lg:grid-cols-4">
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
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-[var(--color-primary)]"
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

          {/* Destination */}
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
              value={filters.destinationId}
              onChange={handleFilterChange}
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-[var(--color-primary)]"
            >
              <option value="">
                All Destinations
              </option>

              {filteredDestinations.map(
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

          {/* Experience */}
          <div>
            <label
              htmlFor="experienceId"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Experience
            </label>

            <select
              id="experienceId"
              name="experienceId"
              value={filters.experienceId}
              onChange={handleFilterChange}
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-[var(--color-primary)]"
            >
              <option value="">
                All Experiences
              </option>

              {experiences.map((experience) => (
                <option
                  key={experience.id}
                  value={experience.id}
                >
                  {experience.name}
                </option>
              ))}
            </select>
          </div>

          {/* Featured */}
          <div className="flex items-end">
            <label className="flex w-full cursor-pointer items-center gap-3 rounded-xl border border-slate-300 px-4 py-3">
              <input
                type="checkbox"
                name="featured"
                checked={filters.featured}
                onChange={handleFilterChange}
                className="h-4 w-4 rounded"
              />

              <span className="text-sm font-medium text-slate-700">
                Featured only
              </span>
            </label>
          </div>
        </div>
      </div>

      {/* Loading filtered results */}
      {filterLoading ? (
        <LoadingSpinner message="Finding places..." />
      ) : places.length === 0 ? (
        <EmptyState message="No attractions match your selected filters." />
      ) : (
        <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {places.map((place) => (
            <PlaceCard
              key={place.id}
              place={place}
            />
          ))}
        </div>
      )}
    </Section>
  );
}