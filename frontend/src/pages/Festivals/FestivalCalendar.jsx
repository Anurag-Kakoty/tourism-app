import { useEffect, useState } from "react";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";

import FestivalOccurrenceCard from "../../components/festivals/FestivalOccurrenceCard";

import festivalOccurrenceService from "../../services/festivalOccurrenceService";

export default function FestivalCalendar() {
  const [occurrences, setOccurrences] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadOccurrences();
  }, []);

  async function loadOccurrences() {
    try {
      setLoading(true);
      setError("");

      const data = await festivalOccurrenceService.getAll();

      setOccurrences(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load festival calendar.");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <Section title="Festival Calendar">
        <LoadingSpinner message="Loading festival calendar..." />
      </Section>
    );
  }

  if (error) {
    return (
      <Section title="Festival Calendar">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  if (occurrences.length === 0) {
    return (
      <Section
        title="Festival Calendar"
        subtitle="Explore festival celebrations across India."
      >
        <EmptyState message="No festival occurrences found." />
      </Section>
    );
  }

  return (
    <Section
      title="Festival Calendar"
      subtitle="Explore festival celebrations across India."
    >
      <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3">
        {occurrences.map((occurrence) => (
          <FestivalOccurrenceCard
            key={occurrence.id}
            occurrence={occurrence}
          />
        ))}
      </div>
    </Section>
  );
}