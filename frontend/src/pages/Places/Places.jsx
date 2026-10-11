import { useEffect, useState } from "react";
import { useSearchParams, Link } from "react-router-dom";
import {
  HiOutlineSparkles,
  HiOutlineSquares2X2,
  HiOutlineUserCircle,
  HiOutlineAdjustmentsHorizontal,
} from "react-icons/hi2";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";
import Button from "../../components/common/inputs/Button";

import PlaceCard from "../../components/places/PlaceCard";

import placeService from "../../services/placeService";
import stateService from "../../services/stateService";
import destinationService from "../../services/destinationService";
import experienceService from "../../services/experienceService";
import tagService from "../../services/tagService";
import recommendationService from "../../services/recommendationService";
import authService from "../../services/authService";

export default function Places() {
  const [searchParams, setSearchParams] = useSearchParams();
  const initialView =
    searchParams.get("view") === "recommended"
      ? "recommended"
      : "all";

  const [viewMode, setViewMode] = useState(initialView);
  const [isAuthenticated, setIsAuthenticated] = useState(
    authService.isAuthenticated()
  );

  const [places, setPlaces] = useState([]);
  const [recommendations, setRecommendations] = useState([]);

  const [states, setStates] = useState([]);
  const [destinations, setDestinations] = useState([]);
  const [experiences, setExperiences] = useState([]);
  const [tags, setTags] = useState([]);

  const [filters, setFilters] = useState({
    stateId: "",
    destinationId: "",
    experienceId: "",
    tagId: "",
    featured: false,
  });

  const [refreshKey, setRefreshKey] = useState(0);
  const [loading, setLoading] = useState(true);
  const [filterLoading, setFilterLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    let isMounted = true;

    async function loadFilterOptions() {
      try {
        const [
          stateData,
          destinationData,
          experienceData,
          tagData,
        ] = await Promise.all([
          stateService.getAll(),
          destinationService.getAll(),
          experienceService.getAll(),
          tagService.getAll(),
        ]);

        if (!isMounted) return;
        setStates(stateData);
        setDestinations(destinationData);
        setExperiences(experienceData);
        setTags(tagData);
      } catch (err) {
        if (!isMounted) return;
        console.error(err);
        setError("Unable to load filter options.");
      }
    }

    loadFilterOptions();

    return () => {
      isMounted = false;
    };
  }, []);

  useEffect(() => {
    let isMounted = true;

    async function fetchAttractions() {
      try {
        setFilterLoading(true);
        setError("");

        if (viewMode === "recommended") {
          const params = {};
          if (filters.stateId) {
            params.stateId = filters.stateId;
          }
          if (filters.destinationId) {
            params.destinationId = filters.destinationId;
          }

          const data =
            await recommendationService.getAttractionRecommendations(
              params
            );

          if (isMounted) {
            setRecommendations(data || []);
          }
        } else {
          const params = {};
          if (filters.stateId) {
            params.stateId = filters.stateId;
          }
          if (filters.destinationId) {
            params.destinationId = filters.destinationId;
          }
          if (filters.experienceId) {
            params.experienceId = filters.experienceId;
          }
          if (filters.tagId) {
            params.tagId = filters.tagId;
          }
          if (filters.featured) {
            params.featured = true;
          }

          const data = await placeService.getAll(params);
          if (isMounted) {
            setPlaces(data || []);
          }
        }
      } catch (err) {
        if (!isMounted) return;
        console.error(err);
        setError(
          viewMode === "recommended"
            ? "Unable to load recommendations."
            : "Unable to load attractions."
        );
      } finally {
        if (isMounted) {
          setLoading(false);
          setFilterLoading(false);
        }
      }
    }

    fetchAttractions();

    return () => {
      isMounted = false;
    };
  }, [viewMode, filters, refreshKey]);

  useEffect(() => {
    const handleAuthChange = () => {
      setIsAuthenticated(authService.isAuthenticated());
      setRefreshKey((prev) => prev + 1);
    };

    const handlePreferencesUpdated = () => {
      setRefreshKey((prev) => prev + 1);
    };

    window.addEventListener("authChange", handleAuthChange);
    window.addEventListener(
      "preferencesUpdated",
      handlePreferencesUpdated
    );

    return () => {
      window.removeEventListener("authChange", handleAuthChange);
      window.removeEventListener(
        "preferencesUpdated",
        handlePreferencesUpdated
      );
    };
  }, []);

  const handleViewChange = (newView) => {
    setViewMode(newView);
    if (newView === "recommended") {
      setSearchParams({ view: "recommended" });
    } else {
      setSearchParams({});
    }
  };

  const handleFilterChange = (event) => {
    const { name, value, type, checked } = event.target;

    const newFilters = {
      ...filters,
      [name]: type === "checkbox" ? checked : value,
    };

    if (name === "stateId") {
      newFilters.destinationId = "";
    }

    setFilters(newFilters);
  };

  const clearFilters = () => {
    const emptyFilters = {
      stateId: "",
      destinationId: "",
      experienceId: "",
      tagId: "",
      featured: false,
    };

    setFilters(emptyFilters);
  };

  const filteredDestinations = filters.stateId
    ? destinations.filter(
        (destination) =>
          String(destination.stateId) === String(filters.stateId)
      )
    : destinations;

  // Filter recommendations client-side for additional UI filters (tag, featured)
  const displayedRecommendations = recommendations.filter((rec) => {
    const attr = rec.attraction;
    if (!attr) return false;

    if (filters.featured && !attr.featured) {
      return false;
    }

    if (filters.tagId) {
      const selectedTag = tags.find(
        (t) => String(t.id) === String(filters.tagId)
      );
      if (
        selectedTag &&
        !(attr.tagNames || []).includes(selectedTag.name)
      ) {
        return false;
      }
    }

    return true;
  });

  const hasPersonalizedMatches = recommendations.some(
    (r) => !r.isFallback
  );

  if (loading) {
    return (
      <Section title="Places">
        <LoadingSpinner message="Loading attractions..." />
      </Section>
    );
  }

  if (
    error &&
    places.length === 0 &&
    recommendations.length === 0
  ) {
    return (
      <Section title="Places">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  return (
    <Section
      title="Discover Places"
      subtitle="Explore destinations and attractions from every corner of India."
    >
      {/* View Mode Switcher Tabs */}
      <div className="mb-8 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between border-b border-slate-200 pb-4">
        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={() => handleViewChange("all")}
            className={`
              flex items-center gap-2 rounded-xl px-5 py-2.5 text-sm font-semibold transition-all
              ${
                viewMode === "all"
                  ? "bg-slate-900 text-white shadow-sm"
                  : "bg-slate-100 text-slate-600 hover:bg-slate-200 hover:text-slate-900"
              }
            `}
          >
            <HiOutlineSquares2X2 size={18} />
            <span>All Places</span>
          </button>

          <button
            type="button"
            onClick={() => handleViewChange("recommended")}
            className={`
              flex items-center gap-2 rounded-xl px-5 py-2.5 text-sm font-semibold transition-all
              ${
                viewMode === "recommended"
                  ? "bg-emerald-600 text-white shadow-sm"
                  : "bg-emerald-50 text-emerald-800 hover:bg-emerald-100"
              }
            `}
          >
            <HiOutlineSparkles size={18} />
            <span>Recommended for You</span>
            {isAuthenticated && (
              <span className="ml-1 rounded-full bg-emerald-100 px-2 py-0.5 text-xs font-bold text-emerald-800">
                Personalized
              </span>
            )}
          </button>
        </div>

        {viewMode === "recommended" && isAuthenticated && (
          <Link
            to="/preferences"
            className="flex items-center gap-1.5 text-xs font-semibold text-[var(--color-primary)] hover:underline"
          >
            <HiOutlineAdjustmentsHorizontal size={16} />
            <span>Edit travel preferences</span>
          </Link>
        )}
      </div>

      {/* Recommendation Context Banner */}
      {viewMode === "recommended" && (
        <div className="mb-8">
          {!isAuthenticated ? (
            <div className="flex flex-col gap-4 rounded-2xl border border-sky-200 bg-sky-50 p-5 sm:flex-row sm:items-center sm:justify-between">
              <div className="flex items-start gap-3">
                <span className="flex h-10 w-10 shrink-0 items-center justify-center rounded-xl bg-sky-100 text-sky-700">
                  <HiOutlineUserCircle size={24} />
                </span>
                <div>
                  <h3 className="text-sm font-bold text-sky-900">
                    Explore top attractions
                  </h3>
                  <p className="mt-0.5 text-xs text-sky-700">
                    Showing popular attractions across India. Log in and pick
                    your travel interests to receive personalized recommendations
                    tailored to your style.
                  </p>
                </div>
              </div>

              <Button
                to="/login"
                variant="outline"
                className="shrink-0 text-xs py-2 px-4 bg-white hover:bg-sky-100 border-sky-300 text-sky-800"
              >
                Log In for Personalized Picks
              </Button>
            </div>
          ) : hasPersonalizedMatches ? (
            <div className="flex flex-col gap-3 rounded-2xl border border-emerald-200 bg-emerald-50/70 p-4 sm:flex-row sm:items-center sm:justify-between">
              <div className="flex items-center gap-3">
                <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-emerald-100 text-emerald-700">
                  <HiOutlineSparkles size={20} />
                </span>
                <div>
                  <p className="text-sm font-semibold text-emerald-950">
                    Tailored to your travel preferences
                  </p>
                  <p className="text-xs text-emerald-800/80">
                    Ranked by how closely these attractions match your selected
                    interests.
                  </p>
                </div>
              </div>

              <Link
                to="/preferences"
                className="text-xs font-semibold text-emerald-800 hover:text-emerald-950 hover:underline shrink-0"
              >
                Manage interests &rarr;
              </Link>
            </div>
          ) : (
            <div className="flex flex-col gap-3 rounded-2xl border border-amber-200 bg-amber-50/80 p-4 sm:flex-row sm:items-center sm:justify-between">
              <div className="flex items-center gap-3">
                <span className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-amber-100 text-amber-800">
                  <HiOutlineSparkles size={20} />
                </span>
                <div>
                  <p className="text-sm font-semibold text-amber-950">
                    Showing popular attractions
                  </p>
                  <p className="text-xs text-amber-800/80">
                    You haven't set your travel preferences yet, or no attractions
                    in this area match them.
                  </p>
                </div>
              </div>

              <Button
                to="/preferences"
                variant="outline"
                className="shrink-0 text-xs py-2 px-4 border-amber-400 text-amber-900 hover:bg-amber-100"
              >
                Choose Travel Interests
              </Button>
            </div>
          )}
        </div>
      )}

      {/* Filters */}
      <div className="mb-10 rounded-2xl border border-[var(--color-border)] bg-white p-6 shadow-sm">
        <div className="flex items-center justify-between gap-4">
          <div>
            <h2 className="text-lg font-bold">
              {viewMode === "recommended"
                ? "Filter Recommendations"
                : "Find Places"}
            </h2>

            <p className="mt-1 text-sm text-slate-500">
              {viewMode === "recommended"
                ? "Narrow recommendations by state or destination."
                : "Filter attractions based on your interests."}
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

        <div className="mt-6 grid gap-4 md:grid-cols-2 lg:grid-cols-5">
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
              <option value="">All States</option>

              {states.map((state) => (
                <option key={state.id} value={state.id}>
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
              <option value="">All Destinations</option>

              {filteredDestinations.map((destination) => (
                <option key={destination.id} value={destination.id}>
                  {destination.name}
                </option>
              ))}
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
              disabled={viewMode === "recommended"}
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-[var(--color-primary)] disabled:bg-slate-100 disabled:opacity-60"
            >
              <option value="">All Experiences</option>

              {experiences.map((experience) => (
                <option key={experience.id} value={experience.id}>
                  {experience.name}
                </option>
              ))}
            </select>
          </div>

          {/* Tag */}
          <div>
            <label
              htmlFor="tagId"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Tag
            </label>

            <select
              id="tagId"
              name="tagId"
              value={filters.tagId}
              onChange={handleFilterChange}
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-[var(--color-primary)]"
            >
              <option value="">All Tags</option>

              {tags.map((tag) => (
                <option key={tag.id} value={tag.id}>
                  {tag.name}
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

      {/* Results */}
      {filterLoading ? (
        <LoadingSpinner
          message={
            viewMode === "recommended"
              ? "Finding recommendations..."
              : "Finding places..."
          }
        />
      ) : viewMode === "recommended" ? (
        displayedRecommendations.length === 0 ? (
          <EmptyState message="No recommendations found matching your selected filters." />
        ) : (
          <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
            {displayedRecommendations.map((rec) => (
              <PlaceCard
                key={rec.attraction.id}
                place={rec.attraction}
                recommendation={{
                  matchScore: rec.matchScore,
                  matchedTags: rec.matchedTags,
                  explanation: rec.explanation,
                  isFallback: rec.isFallback,
                }}
              />
            ))}
          </div>
        )
      ) : places.length === 0 ? (
        <EmptyState message="No attractions match your selected filters." />
      ) : (
        <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {places.map((place) => (
            <PlaceCard key={place.id} place={place} />
          ))}
        </div>
      )}
    </Section>
  );
}