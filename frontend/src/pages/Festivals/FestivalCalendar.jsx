import { useEffect, useState } from "react";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";

import FestivalFilters from "../../components/festivals/FestivalFilters";
import FestivalOccurrenceCard from "../../components/festivals/FestivalOccurrenceCard";

import festivalOccurrenceService from "../../services/festivalOccurrenceService";

export default function FestivalCalendar() {
  const [occurrences, setOccurrences] = useState([]);
  const [years, setYears] = useState([]);

  const [selectedState, setSelectedState] = useState("");
  const [selectedYear, setSelectedYear] = useState("");

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadInitialOccurrences();
  }, []);

  useEffect(() => {
    if (!loading) {
      loadFilteredOccurrences();
    }
  }, [selectedState, selectedYear]);

  async function loadInitialOccurrences() {
    try {
      setLoading(true);
      setError("");

      const data = await festivalOccurrenceService.getAll();

      setOccurrences(data);

      const availableYears = [
        ...new Set(
          data
            .map((occurrence) => occurrence.year)
            .filter(Boolean)
        ),
      ].sort((a, b) => a - b);

      setYears(availableYears);
    } catch (err) {
      console.error(err);
      setError("Unable to load festival calendar.");
    } finally {
      setLoading(false);
    }
  }

  async function loadFilteredOccurrences() {
    try {
      setLoading(true);
      setError("");

      let data;

      if (selectedState && selectedYear) {
        data =
          await festivalOccurrenceService.getByStateAndYear(
            selectedState,
            selectedYear
          );
      } else if (selectedState) {
        data =
          await festivalOccurrenceService.getByState(
            selectedState
          );
      } else if (selectedYear) {
        data =
          await festivalOccurrenceService.getByYear(
            selectedYear
          );
      } else {
        data =
          await festivalOccurrenceService.getAll();
      }

      setOccurrences(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load festival calendar.");
    } finally {
      setLoading(false);
    }
  }

  function resetFilters() {
    setSelectedState("");
    setSelectedYear("");
  }

  return (
    <Section
      title="Festival Calendar"
      subtitle="Explore festival celebrations across India by state and year."
    >
      <FestivalFilters
        selectedState={selectedState}
        selectedYear={selectedYear}
        years={years}
        onStateChange={setSelectedState}
        onYearChange={setSelectedYear}
        onReset={resetFilters}
      />

      {loading && (
        <LoadingSpinner message="Loading festival calendar..." />
      )}

      {!loading && error && (
        <ErrorMessage message={error} />
      )}

      {!loading && !error && occurrences.length === 0 && (
        <EmptyState message="No festival occurrences found for the selected filters." />
      )}

      {!loading && !error && occurrences.length > 0 && (
        <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3">
          {occurrences.map((occurrence) => (
            <FestivalOccurrenceCard
              key={occurrence.id}
              occurrence={occurrence}
            />
          ))}
        </div>
      )}
    </Section>
  );
}