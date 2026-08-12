import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import Badge from "../../components/common/display/Badge";

import stayService from "../../services/stayService";

export default function StayDetails() {
  const { id } = useParams();

  const [stay, setStay] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadStay();
  }, [id]);

  async function loadStay() {
    try {
      setLoading(true);
      setError("");

      const data = await stayService.getById(id);

      setStay(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load stay.");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <Section title="Stay">
        <LoadingSpinner message="Loading stay..." />
      </Section>
    );
  }

  if (error) {
    return (
      <Section title="Stay">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  if (!stay) {
    return (
      <Section title="Stay">
        <ErrorMessage message="Stay not found." />
      </Section>
    );
  }

  return (
    <>
      {stay.imageUrl && (
        <section>
          <img
            src={stay.imageUrl}
            alt={stay.name}
            className="h-[420px] w-full object-cover"
          />
        </section>
      )}

      <Section>
        <div className="grid gap-10 lg:grid-cols-3">
          <div className="lg:col-span-2">
            <Badge>
              {stay.type.replaceAll("_", " ")}
            </Badge>

            <h1 className="mt-4 text-4xl font-bold sm:text-5xl">
              {stay.name}
            </h1>

            <p className="mt-3 text-lg text-slate-500">
              {stay.destinationName}, {stay.stateName}
            </p>

            <p className="mt-8 text-lg leading-8 text-slate-600">
              {stay.description}
            </p>

            <div className="mt-8">
              {stay.available ? (
                <Badge>
                  Currently Available
                </Badge>
              ) : (
                <Badge className="bg-slate-100 text-slate-600">
                  Currently Unavailable
                </Badge>
              )}
            </div>
          </div>

          <div className="rounded-2xl border border-[var(--color-border)] bg-white p-6 shadow-sm">
            <h2 className="text-xl font-bold">
              Stay Information
            </h2>

            <div className="mt-6 space-y-5">
              <div>
                <p className="text-sm text-slate-500">
                  Price per night
                </p>

                <p className="mt-1 text-2xl font-bold text-[var(--color-primary)]">
                  ₹
                  {Number(
                    stay.pricePerNight
                  ).toLocaleString("en-IN")}
                </p>
              </div>

              {stay.rating != null && (
                <div>
                  <p className="text-sm text-slate-500">
                    Rating
                  </p>

                  <p className="mt-1 font-semibold">
                    ★ {stay.rating}
                  </p>
                </div>
              )}

              <div>
                <p className="text-sm text-slate-500">
                  Address
                </p>

                <p className="mt-1 font-medium">
                  {stay.address}
                </p>
              </div>

              {stay.contactNumber && (
                <div>
                  <p className="text-sm text-slate-500">
                    Contact
                  </p>

                  <p className="mt-1 font-medium">
                    {stay.contactNumber}
                  </p>
                </div>
              )}

              {stay.email && (
                <div>
                  <p className="text-sm text-slate-500">
                    Email
                  </p>

                  <p className="mt-1 break-words font-medium">
                    {stay.email}
                  </p>
                </div>
              )}

              {stay.website && (
                <div>
                  <p className="text-sm text-slate-500">
                    Website
                  </p>

                  <a
                    href={stay.website}
                    target="_blank"
                    rel="noopener noreferrer"
                    className="mt-1 block break-words font-medium text-[var(--color-primary)] hover:underline"
                  >
                    Visit Website →
                  </a>
                </div>
              )}
            </div>
          </div>
        </div>
      </Section>
    </>
  );
}