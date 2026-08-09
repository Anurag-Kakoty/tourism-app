import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";

import festivalService from "../../services/festivalService";

export default function FestivalDetails() {
  const { id } = useParams();

  const [festival, setFestival] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadFestival();
  }, [id]);

  async function loadFestival() {
    try {
      setLoading(true);
      setError("");

      const data = await festivalService.getById(id);

      setFestival(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load festival.");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <Section title="Festival">
        <LoadingSpinner message="Loading festival..." />
      </Section>
    );
  }

  if (error) {
    return (
      <Section title="Festival">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  if (!festival) {
    return (
      <Section title="Festival">
        <ErrorMessage message="Festival not found." />
      </Section>
    );
  }

  return (
    <>
      <section className="relative">
        <img
          src={festival.imageUrl}
          alt={festival.name}
          className="h-[420px] w-full object-cover"
        />

        <div className="absolute inset-0 bg-black/45" />

        <div className="absolute inset-0 flex items-end">
          <div className="mx-auto w-full max-w-7xl px-4 pb-12 sm:px-6 lg:px-8">
            <span className="inline-block rounded-full bg-orange-500 px-4 py-2 text-sm font-semibold text-white">
              {festival.category}
            </span>

            <h1 className="mt-4 text-4xl font-bold text-white sm:text-5xl lg:text-6xl">
              {festival.name}
            </h1>
          </div>
        </div>
      </section>

      <Section title="About the Festival">
        <div className="max-w-4xl">
          <p className="text-lg leading-8 text-slate-600">
            {festival.description}
          </p>
        </div>
      </Section>

      <Section title="Festival Information">
        <div className="grid gap-6 sm:grid-cols-2">
          <div className="rounded-2xl border border-[var(--color-border)] bg-white p-6">
            <p className="text-sm text-slate-500">
              Category
            </p>

            <p className="mt-2 text-lg font-semibold">
              {festival.category}
            </p>
          </div>

          {festival.officialWebsite && (
            <div className="rounded-2xl border border-[var(--color-border)] bg-white p-6">
              <p className="text-sm text-slate-500">
                Official Website
              </p>

              <a
                href={festival.officialWebsite}
                target="_blank"
                rel="noopener noreferrer"
                className="mt-2 inline-block font-semibold text-[var(--color-primary)] hover:underline"
              >
                Visit Official Website →
              </a>
            </div>
          )}
        </div>
      </Section>
    </>
  );
}