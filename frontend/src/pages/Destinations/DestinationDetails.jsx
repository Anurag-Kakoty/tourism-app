import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import EmptyState from "../../components/common/feedback/EmptyState";

import destinationService from "../../services/destinationService";
import placeService from "../../services/placeService";

import PlaceCard from "../../components/places/PlaceCard";

export default function DestinationDetails() {
  const { id } = useParams();

  const [destination, setDestination] = useState(null);
  const [attractions, setAttractions] = useState([]);

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadDestination();
  }, [id]);

  async function loadDestination() {
    try {
      setLoading(true);
      setError("");

      const [destinationData, attractionData] = await Promise.all([
        destinationService.getById(id),
        placeService.getByDestination(id),
      ]);

      setDestination(destinationData);
      setAttractions(attractionData);
    } catch (err) {
      console.error(err);
      setError("Unable to load destination.");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <Section title="Destination">
        <LoadingSpinner message="Loading destination..." />
      </Section>
    );
  }

  if (error) {
    return (
      <Section title="Destination">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  if (!destination) {
    return (
      <Section title="Destination">
        <ErrorMessage message="Destination not found." />
      </Section>
    );
  }

  return (
    <>
      <section className="relative">
        <img
          src={destination.coverImageUrl}
          alt={destination.name}
          className="h-[420px] w-full object-cover"
        />

        <div className="absolute inset-0 bg-black/45" />

        <div className="absolute inset-0 flex items-end">
          <div className="mx-auto w-full max-w-7xl px-4 pb-12 sm:px-6 lg:px-8">
            <p className="text-sm font-semibold uppercase tracking-[0.25em] text-emerald-200">
              {destination.stateName}
            </p>

            <h1 className="mt-3 text-4xl font-bold text-white sm:text-5xl lg:text-6xl">
              {destination.name}
            </h1>

            <p className="mt-3 text-xl text-white/90">
              {destination.tagline}
            </p>
          </div>
        </div>
      </section>

      <Section title="About the Destination">
        <div className="grid gap-10 lg:grid-cols-3">
          <div className="lg:col-span-2">
            <p className="text-lg leading-8 text-slate-600">
              {destination.description}
            </p>
          </div>

          <div className="rounded-2xl border border-[var(--color-border)] bg-white p-6 shadow-sm">
            <h3 className="text-lg font-bold">
              Destination Information
            </h3>

            <div className="mt-5 space-y-4">
              <div>
                <p className="text-sm text-slate-500">
                  District
                </p>
                <p className="font-medium">
                  {destination.district}
                </p>
              </div>

              <div>
                <p className="text-sm text-slate-500">
                  Type
                </p>
                <p className="font-medium">
                  {destination.type.replaceAll("_", " ")}
                </p>
              </div>

              <div>
                <p className="text-sm text-slate-500">
                  Nearest Airport
                </p>
                <p className="font-medium">
                  {destination.nearestAirport}
                </p>
              </div>

              <div>
                <p className="text-sm text-slate-500">
                  Nearest Railway Station
                </p>
                <p className="font-medium">
                  {destination.nearestRailwayStation}
                </p>
              </div>

              <div>
                <p className="text-sm text-slate-500">
                  Timezone
                </p>
                <p className="font-medium">
                  {destination.timezone}
                </p>
              </div>
            </div>
          </div>
        </div>
      </Section>

      <Section
        title="Top Attractions"
        subtitle={`Places to explore in ${destination.name}.`}
      >
        {attractions.length === 0 ? (
          <EmptyState message="No attractions found for this destination." />
        ) : (
          <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3">
            {attractions.map((attraction) => (
              <PlaceCard
                key={attraction.id}
                place={attraction}
              />
            ))}
          </div>
        )}
      </Section>
    </>
  );
}