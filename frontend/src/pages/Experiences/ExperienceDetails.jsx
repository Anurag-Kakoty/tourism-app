import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";

import PlaceCard from "../../components/places/PlaceCard";

import experienceService from "../../services/experienceService";
import placeService from "../../services/placeService";

export default function ExperienceDetails() {
  const { id } = useParams();

  const [experience, setExperience] = useState(null);
  const [places, setPlaces] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadExperience();
  }, [id]);

  async function loadExperience() {
    try {
      setLoading(true);
      setError("");

      const [experienceData, attractionData] = await Promise.all([
        experienceService.getById(id),
        placeService.getAll(),
      ]);

      const experienceId = Number(id);

      const matchingPlaces = attractionData.filter((place) =>
        (place.experienceIds ?? []).includes(experienceId)
      );

      setExperience(experienceData);
      setPlaces(matchingPlaces);
    } catch (err) {
      console.error(err);
      setError("Unable to load experience.");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <Section title="Experience">
        <LoadingSpinner message="Loading experience..." />
      </Section>
    );
  }

  if (error) {
    return (
      <Section title="Experience">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  if (!experience) {
    return (
      <Section title="Experience">
        <ErrorMessage message="Experience not found." />
      </Section>
    );
  }

  return (
    <>
      <Section>
        <div className="mx-auto max-w-4xl text-center">
          <div className="mx-auto flex h-20 w-20 items-center justify-center rounded-3xl bg-emerald-100 text-4xl text-[var(--color-primary)]">
            {experience.icon || "★"}
          </div>

          <h1 className="mt-6 text-4xl font-bold sm:text-5xl">
            {experience.name}
          </h1>

          <p className="mt-6 text-lg leading-8 text-slate-600">
            {experience.description ||
              `Discover ${experience.name.toLowerCase()} experiences across India.`}
          </p>
        </div>
      </Section>

      <Section
        title={`Places for ${experience.name}`}
        subtitle={`Discover destinations and attractions where you can experience ${experience.name.toLowerCase()}.`}
      >
        {places.length === 0 ? (
          <EmptyState
            message={`No attractions currently offer ${experience.name.toLowerCase()}.`}
          />
        ) : (
          <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3">
            {places.map((place) => (
              <PlaceCard
                key={place.id}
                place={place}
              />
            ))}
          </div>
        )}
      </Section>
    </>
  );
}