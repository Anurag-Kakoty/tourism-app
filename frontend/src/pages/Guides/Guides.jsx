import { useEffect, useState } from "react";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";

import GuideCard from "../../components/guides/GuideCard";

import guideService from "../../services/guideService";
import destinationService from "../../services/destinationService";

export default function Guides() {
  const [guides, setGuides] = useState([]);
  const [destinations, setDestinations] = useState([]);

  const [destinationId, setDestinationId] = useState("");
  const [available, setAvailable] = useState("");
  const [providesTransport, setProvidesTransport] = useState("");
  const [language, setLanguage] = useState("");

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadInitialData();
  }, []);

  async function loadInitialData() {
    try {
      setLoading(true);
      setError("");

      const [guideData, destinationData] =
        await Promise.all([
          guideService.getAll(),
          destinationService.getAll(),
        ]);

      setGuides(guideData);
      setDestinations(destinationData);
    } catch (err) {
      console.error(err);
      setError("Unable to load guides.");
    } finally {
      setLoading(false);
    }
  }

  async function loadGuides() {
    try {
      setLoading(true);
      setError("");

      const data = await guideService.getAll({
        destinationId,
        available,
        providesTransport,
        language,
      });

      setGuides(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load guides.");
    } finally {
      setLoading(false);
    }
  }

  function handleFilterChange(setter, value) {
    setter(value);
  }

  function clearFilters() {
    setDestinationId("");
    setAvailable("");
    setProvidesTransport("");
    setLanguage("");

    loadGuidesWithFilters({
      destinationId: "",
      available: "",
      providesTransport: "",
      language: "",
    });
  }

  async function loadGuidesWithFilters(filters) {
    try {
      setLoading(true);
      setError("");

      const data = await guideService.getAll(filters);

      setGuides(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load guides.");
    } finally {
      setLoading(false);
    }
  }

  if (loading && guides.length === 0) {
    return (
      <Section title="Tour Guides">
        <LoadingSpinner message="Loading guides..." />
      </Section>
    );
  }

  if (error && guides.length === 0) {
    return (
      <Section title="Tour Guides">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  return (
    <Section
      title="Tour Guides"
      subtitle="Find local guides to help you explore your destination."
    >
      {/* Filters */}
      <div className="mb-10 rounded-2xl border border-[var(--color-border)] bg-white p-6 shadow-sm">
        <div className="grid gap-5 md:grid-cols-2 lg:grid-cols-4">

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
                handleFilterChange(
                  setDestinationId,
                  e.target.value
                )
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

          {/* Availability */}
          <div>
            <label
              htmlFor="availability"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Availability
            </label>

            <select
              id="availability"
              value={available}
              onChange={(e) =>
                handleFilterChange(
                  setAvailable,
                  e.target.value
                )
              }
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-[var(--color-primary)]"
            >
              <option value="">
                Any availability
              </option>

              <option value="true">
                Available
              </option>

              <option value="false">
                Unavailable
              </option>
            </select>
          </div>

          {/* Transport */}
          <div>
            <label
              htmlFor="transport"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Transport
            </label>

            <select
              id="transport"
              value={providesTransport}
              onChange={(e) =>
                handleFilterChange(
                  setProvidesTransport,
                  e.target.value
                )
              }
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-[var(--color-primary)]"
            >
              <option value="">
                Any option
              </option>

              <option value="true">
                Transport provided
              </option>

              <option value="false">
                No transport
              </option>
            </select>
          </div>

          {/* Language */}
          <div>
            <label
              htmlFor="language"
              className="mb-2 block text-sm font-medium text-slate-700"
            >
              Language
            </label>

            <select
              id="language"
              value={language}
              onChange={(e) =>
                handleFilterChange(
                  setLanguage,
                  e.target.value
                )
              }
              className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-sm outline-none focus:border-[var(--color-primary)]"
            >
              <option value="">
                All languages
              </option>

              <option value="ENGLISH">English</option>
              <option value="HINDI">Hindi</option>
              <option value="ASSAMESE">Assamese</option>
              <option value="KHASI">Khasi</option>
              <option value="GARO">Garo</option>
              <option value="BENGALI">Bengali</option>
              <option value="TAMIL">Tamil</option>
              <option value="TELUGU">Telugu</option>
              <option value="KANNADA">Kannada</option>
              <option value="MALAYALAM">Malayalam</option>
              <option value="MARATHI">Marathi</option>
              <option value="GUJARATI">Gujarati</option>
              <option value="PUNJABI">Punjabi</option>
              <option value="ODIA">Odia</option>
              <option value="NEPALI">Nepali</option>
              <option value="SANSKRIT">Sanskrit</option>
              <option value="FRENCH">French</option>
              <option value="GERMAN">German</option>
              <option value="SPANISH">Spanish</option>
              <option value="JAPANESE">Japanese</option>
              <option value="CHINESE">Chinese</option>
            </select>
            </div>
          </div>

        {/* Filter buttons */}
        <div className="mt-6 flex flex-wrap gap-3">
          <button
            type="button"
            onClick={loadGuides}
            className="rounded-xl bg-[var(--color-primary)] px-6 py-3 text-sm font-semibold text-white transition hover:opacity-90"
          >
            Apply Filters
          </button>

          <button
            type="button"
            onClick={clearFilters}
            className="rounded-xl border border-slate-300 bg-white px-6 py-3 text-sm font-semibold text-slate-700 transition hover:bg-slate-50"
          >
            Clear Filters
          </button>
        </div>
      </div>

      {/* Error after filtering */}
      {error && (
        <div className="mb-6">
          <ErrorMessage message={error} />
        </div>
      )}

      {/* Loading after filtering */}
      {loading ? (
        <LoadingSpinner message="Filtering guides..." />
      ) : guides.length === 0 ? (
        <EmptyState message="No guides match the selected filters." />
      ) : (
        <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
          {guides.map((guide) => (
            <GuideCard
              key={guide.id}
              guide={guide}
            />
          ))}
        </div>
      )}
    </Section>
  );
}