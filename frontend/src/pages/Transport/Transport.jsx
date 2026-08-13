import { useEffect, useState } from "react";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";

import TransportCard from "../../components/transport/TransportCard";

import transportService from "../../services/transportService";

export default function Transport() {
  const [transportOptions, setTransportOptions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadTransport();
  }, []);

  async function loadTransport() {
    try {
      setLoading(true);
      setError("");

      const data = await transportService.getAll();

      console.log("Transport:", data);

      setTransportOptions(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load transport options.");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <Section title="Transport">
        <LoadingSpinner message="Loading transport options..." />
      </Section>
    );
  }

  if (error) {
    return (
      <Section title="Transport">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  if (transportOptions.length === 0) {
    return (
      <Section
        title="Transport"
        subtitle="Explore transport options for your journey."
      >
        <EmptyState message="No transport options found." />
      </Section>
    );
  }

  return (
    <Section
      title="Transport"
      subtitle="Explore transport options for your journey."
    >
      <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {transportOptions.map((transport) => (
          <TransportCard
            key={transport.id}
            transport={transport}
          />
        ))}
      </div>
    </Section>
  );
}