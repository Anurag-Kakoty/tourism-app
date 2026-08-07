import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";

import stateService from "../../services/stateService";

export default function StateDetails() {

  const { id } = useParams();

  const [state, setState] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadState();
  }, [id]);

  async function loadState() {

    try {

      setLoading(true);
      setError("");

      const data = await stateService.getById(id);

      setState(data);

    } catch (err) {

      console.error(err);
      setError("Unable to load state.");

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

          <p className="mt-8 leading-8">
            {state.description}
          </p>

        </div>

      </div>
    </Section>
  );
}