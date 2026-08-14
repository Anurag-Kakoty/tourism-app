import { useEffect, useState } from "react";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";

import GuideCard from "../../components/guides/GuideCard";

import guideService from "../../services/guideService";

export default function Guides() {
  const [guides, setGuides] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadGuides();
  }, []);

  async function loadGuides() {
    try {
      setLoading(true);
      setError("");

      const data = await guideService.getAll();

      console.log("Guides:", data);

      setGuides(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load guides.");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <Section title="Tour Guides">
        <LoadingSpinner message="Loading guides..." />
      </Section>
    );
  }

  if (error) {
    return (
      <Section title="Tour Guides">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  if (guides.length === 0) {
    return (
      <Section
        title="Tour Guides"
        subtitle="Find local guides to help you explore your destination."
      >
        <EmptyState message="No guides found." />
      </Section>
    );
  }

  return (
    <Section
      title="Tour Guides"
      subtitle="Find local guides to help you explore your destination."
    >
      <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {guides.map((guide) => (
          <GuideCard
            key={guide.id}
            guide={guide}
          />
        ))}
      </div>
    </Section>
  );
}