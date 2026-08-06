import { useParams } from "react-router-dom";

import Section from "../../components/common/layout/Section";
import placeService from "../../services/placeService";

export default function PlaceDetails() {
  const { id } = useParams();

  const place = placeService.getById(id);

  if (!place) {
    return (
      <Section title="Place Not Found">
        <p>The requested destination could not be found.</p>
      </Section>
    );
  }

  return (
    <Section>
      <div className="grid gap-10 lg:grid-cols-2">
        <img
          src={place.image}
          alt={place.name}
          className="h-96 w-full rounded-2xl object-cover"
        />

        <div>
          <h1 className="text-4xl font-bold">
            {place.name}
          </h1>

          <p className="mt-3 text-slate-600">
            {place.state}
          </p>

          <span className="mt-4 inline-block rounded-full bg-emerald-100 px-3 py-1 text-sm font-medium text-emerald-700">
            {place.category}
          </span>

          <p className="mt-6 leading-8">
            {place.description}
          </p>

          <div className="mt-8">
            <h3 className="text-lg font-semibold">
              Best Time to Visit
            </h3>

            <p className="mt-2">
              {place.bestTime}
            </p>
          </div>

          <div className="mt-8">
            <h3 className="text-lg font-semibold">
              Highlights
            </h3>

            <ul className="mt-3 list-disc space-y-2 pl-5">
              {place.highlights.map((highlight) => (
                <li key={highlight}>
                  {highlight}
                </li>
              ))}
            </ul>
          </div>
        </div>
      </div>
    </Section>
  );
}