import { useEffect, useState } from "react";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";

import FestivalCard from "../../components/festivals/FestivalCard";

import festivalService from "../../services/festivalService";

export default function Festivals() {
  const [festivals, setFestivals] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadFestivals();
  }, []);

  async function loadFestivals() {
    try {
      setLoading(true);
      setError("");

      const data = await festivalService.getAll();

      console.log("Festivals:", data);

      setFestivals(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load festivals.");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <Section title="Festivals">
        <LoadingSpinner message="Loading festivals..." />
      </Section>
    );
  }

  if (error) {
    return (
      <Section title="Festivals">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  if (festivals.length === 0) {
    return (
      <Section
        title="Festivals of India"
        subtitle="Discover the cultural celebrations and traditions of India."
        action={{
          label: "View Festival Calendar",
          to: "/festival-calendar",
        }}
      >
        <EmptyState message="No festivals found." />
      </Section>
    );
  }

  return (
    <Section
      title="Festivals of India"
      subtitle="Discover the cultural celebrations and traditions of India."
      action={{
        label: "View Festival Calendar",
        to: "/festival-calendar",
      }}
    >
      <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {festivals.map((festival) => (
          <FestivalCard
            key={festival.id}
            festival={festival}
          />
        ))}
      </div>
    </Section>
  );
}