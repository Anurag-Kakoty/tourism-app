import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import Section from "../../components/common/layout/Section";
import placeService from "../../services/placeService";
import { getAttractionSchedule } from "../../utils/schedule";

export default function PlaceDetails() {
  const { id } = useParams();

  const [place, setPlace] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    async function loadPlace() {
      try {
        setLoading(true);

        const data = await placeService.getById(id);

        setPlace(data);
      } catch (err) {
        console.error(err);
        setError("Unable to load attraction.");
      } finally {
        setLoading(false);
      }
    }

    loadPlace();
  }, [id]);

  if (loading) {
    return (
      <Section title="Loading...">
        <p>Loading attraction...</p>
      </Section>
    );
  }

  if (error) {
    return (
      <Section title="Error">
        <p className="text-red-600">{error}</p>
      </Section>
    );
  }

  if (!place) {
    return (
      <Section title="Not Found">
        <p>Attraction not found.</p>
      </Section>
    );
  }

  const { hoursText, closedDaysText } = getAttractionSchedule(place);

  return (
    <Section>
      <div className="grid gap-10 lg:grid-cols-2">

        <img
          src={place.thumbnailUrl}
          alt={place.name}
          className="h-96 w-full rounded-2xl object-cover"
        />

        <div>

          <h1 className="text-4xl font-bold">
            {place.name}
          </h1>

          <p className="mt-3 text-slate-600">
            {place.destinationName}, {place.stateName}
          </p>

          <div className="mt-4 flex flex-wrap gap-2">
            {(place.tagNames ?? []).map((tag) => (
              <span
                key={tag}
                className="rounded-full bg-emerald-100 px-3 py-1 text-sm font-medium text-emerald-700"
              >
                {tag}
              </span>
            ))}
          </div>

          <p className="mt-8 leading-8">
            {place.description}
          </p>

          <div className="mt-8">
            <h3 className="text-lg font-semibold">
              Opening Hours
            </h3>

            <p className="mt-2 text-slate-700">
              {hoursText}
            </p>

            {closedDaysText && (
              <p className="mt-1 text-sm text-slate-500">
                Closed on {closedDaysText}
              </p>
            )}
          </div>

          <div className="mt-8">
            <h3 className="text-lg font-semibold">
              Best Season
            </h3>

            <p className="mt-2">
              {place.bestSeason}
            </p>
          </div>

          <div className="mt-8">
            <h3 className="text-lg font-semibold">
              Entry Fee
            </h3>

            <p className="mt-2">
              ₹ {place.entryFee ?? "Free"}
            </p>
          </div>

          <div className="mt-8">
            <h3 className="text-lg font-semibold">
              Experiences
            </h3>

            <ul className="mt-3 list-disc space-y-2 pl-5">
              {(place.experienceNames ?? []).map((experience) => (
                <li key={experience}>
                  {experience}
                </li>
              ))}
            </ul>
          </div>

        </div>

      </div>
    </Section>
  );
}