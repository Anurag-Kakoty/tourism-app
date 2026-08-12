import { useEffect, useState } from "react";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";

import StayCard from "../../components/stay/StayCard";

import stayService from "../../services/stayService";

export default function Stay() {
  const [stays, setStays] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadStays();
  }, []);

  async function loadStays() {
    try {
      setLoading(true);
      setError("");

      const data = await stayService.getAll();

      console.log("Stays:", data);

      setStays(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load stays.");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <Section title="Places to Stay">
        <LoadingSpinner message="Loading stays..." />
      </Section>
    );
  }

  if (error) {
    return (
      <Section title="Places to Stay">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  if (stays.length === 0) {
    return (
      <Section
        title="Places to Stay"
        subtitle="Find comfortable places to stay during your journey."
      >
        <EmptyState message="No stays found." />
      </Section>
    );
  }

  return (
    <Section
      title="Places to Stay"
      subtitle="Find comfortable places to stay during your journey."
    >
      <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {stays.map((stay) => (
          <StayCard
            key={stay.id}
            stay={stay}
          />
        ))}
      </div>
    </Section>
  );
}