import { useEffect, useState } from "react";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";

import DestinationCard from "../../components/destinations/DestinationCard";

import destinationService from "../../services/destinationService";

export default function Destinations() {
  const [destinations, setDestinations] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadDestinations();
  }, []);

  async function loadDestinations() {
    try {
      setLoading(true);
      setError("");

      const data = await destinationService.getAll();

      console.log("Destinations:", data);

      setDestinations(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load destinations.");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <Section title="Destinations">
        <LoadingSpinner message="Loading destinations..." />
      </Section>
    );
  }

  if (error) {
    return (
      <Section title="Destinations">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  if (destinations.length === 0) {
    return (
      <Section
        title="Explore Destinations"
        subtitle="Discover remarkable places across India."
      >
        <EmptyState message="No destinations found." />
      </Section>
    );
  }

  return (
    <Section
      title="Explore Destinations"
      subtitle="Discover remarkable places, landscapes and experiences across India."
    >
      <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {destinations.map((destination) => (
          <DestinationCard
            key={destination.id}
            destination={destination}
          />
        ))}
      </div>
    </Section>
  );
}