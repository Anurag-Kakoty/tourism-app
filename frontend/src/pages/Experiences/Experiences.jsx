import { useEffect, useState } from "react";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";

import ExperienceCard from "../../components/experiences/ExperienceCard";

import experienceService from "../../services/experienceService";

export default function Experiences() {
  const [experiences, setExperiences] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadExperiences();
  }, []);

  async function loadExperiences() {
    try {
      setLoading(true);
      setError("");

      const data = await experienceService.getAll();

      console.log("Experiences:", data);

      setExperiences(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load experiences.");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <Section title="Experiences">
        <LoadingSpinner message="Loading experiences..." />
      </Section>
    );
  }

  if (error) {
    return (
      <Section title="Experiences">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  if (experiences.length === 0) {
    return (
      <Section
        title="Experiences Across India"
        subtitle="Discover activities and experiences waiting for you."
      >
        <EmptyState message="No experiences found." />
      </Section>
    );
  }

  return (
    <Section
      title="Experiences Across India"
      subtitle="Discover activities and experiences waiting for you."
    >
      <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {experiences.map((experience) => (
          <ExperienceCard
            key={experience.id}
            experience={experience}
          />
        ))}
      </div>
    </Section>
  );
}