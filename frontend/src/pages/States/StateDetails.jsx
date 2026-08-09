import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";

import stateService from "../../services/stateService";
import destinationService from "../../services/destinationService";
import DestinationCard from "../../components/destinations/DestinationCard";

export default function StateDetails() {
  const { id } = useParams();

  const [state, setState] = useState(null);
  const [destinations, setDestinations] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadState();
  }, [id]);

  async function loadState() {
    try {
      setLoading(true);
      setError("");

      const [stateData, destinationData] = await Promise.all([
        stateService.getById(id),
        destinationService.getByState(id),
      ]);

      setState(stateData);
      setDestinations(destinationData);
    } catch (err) {
      console.error(err);
      setError("Unable to load state information.");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <Section title="State">
        <LoadingSpinner message="Loading state..." />
      </Section>
    );
  }

  if (error) {
    return (
      <Section title="State">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  if (!state) {
    return (
      <Section title="State">
        <ErrorMessage message="State not found." />
      </Section>
    );
  }

  return (
    <>
      <Section>
        <div className="grid gap-10 lg:grid-cols-2">
          <img
            src={state.thumbnailUrl}
            alt={state.name}
            className="h-96 w-full rounded-2xl object-cover"
          />

          <div>
            <h1 className="text-5xl font-bold">
              {state.name}
            </h1>

            <p className="mt-4 text-xl text-slate-600">
              Capital: {state.capital}
            </p>

            <p className="mt-8 leading-8 text-slate-600">
              {state.description}
            </p>
          </div>
        </div>
      </Section>

      <Section
        title="Top Destinations"
        subtitle={`Explore destinations across ${state.name}.`}
      >
        {destinations.length === 0 ? (
          <EmptyState message="No destinations found for this state." />
        ) : (
          <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3">
            {destinations.map((destination) => (
              <DestinationCard
                key={destination.id}
                destination={destination}
              />
            ))}
          </div>
        )}
      </Section>
    </>
  );
}