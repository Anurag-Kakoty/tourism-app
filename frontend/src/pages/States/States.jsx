import { useEffect, useState } from "react";

import Section from "../../components/common/layout/Section";

import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";

import StateCard from "../../components/states/StateCard";

import stateService from "../../services/stateService";

export default function States() {

  const [states, setStates] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadStates();
  }, []);

  async function loadStates() {

    try {

      setLoading(true);
      setError("");

      const data = await stateService.getAll();

      setStates(data);

    } catch (err) {

      console.error(err);
      setError("Unable to load states.");

    } finally {

      setLoading(false);

    }

  }

  if (loading) {
    return (
      <Section title="States">
        <LoadingSpinner message="Loading states..." />
      </Section>
    );
  }

  if (error) {
    return (
      <Section title="States">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  if (states.length === 0) {
    return (
      <Section title="States">
        <EmptyState message="No states available." />
      </Section>
    );
  }

  return (
    <Section
      title="Explore India by State"
      subtitle="Choose a state and begin discovering its destinations, attractions, festivals and experiences."
    >
      <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3">
        {states.map((state) => (
          <StateCard
            key={state.id}
            state={state}
          />
        ))}
      </div>
    </Section>
  );
}