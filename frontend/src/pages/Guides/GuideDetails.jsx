import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import Badge from "../../components/common/display/Badge";

import guideService from "../../services/guideService";

export default function GuideDetails() {
  const { id } = useParams();

  const [guide, setGuide] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadGuide();
  }, [id]);

  async function loadGuide() {
    try {
      setLoading(true);
      setError("");

      const data = await guideService.getById(id);

      setGuide(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load guide.");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <Section title="Guide">
        <LoadingSpinner message="Loading guide..." />
      </Section>
    );
  }

  if (error) {
    return (
      <Section title="Guide">
        <ErrorMessage message={error} />
      </Section>
    );
  }

  if (!guide) {
    return (
      <Section title="Guide">
        <ErrorMessage message="Guide not found." />
      </Section>
    );
  }

  return (
    <>
      {guide.imageUrl && (
        <section>
          <img
            src={guide.imageUrl}
            alt={guide.name}
            className="h-[420px] w-full object-cover"
          />
        </section>
      )}

      <Section>
        <div className="grid gap-10 lg:grid-cols-3">
          <div className="lg:col-span-2">
            <div className="flex flex-wrap gap-2">
              {guide.available ? (
                <Badge>
                  Available
                </Badge>
              ) : (
                <Badge className="bg-slate-100 text-slate-600">
                  Currently Unavailable
                </Badge>
              )}

              {guide.providesTransport && (
                <Badge className="bg-sky-100 text-sky-700">
                  Transport Provided
                </Badge>
              )}
            </div>

            <h1 className="mt-5 text-4xl font-bold sm:text-5xl">
              {guide.name}
            </h1>

            <p className="mt-3 text-lg text-slate-500">
              {guide.destinationName}, {guide.stateName}
            </p>

            <p className="mt-8 text-lg leading-8 text-slate-600">
              {guide.bio}
            </p>

            {guide.languages?.length > 0 && (
              <div className="mt-8">
                <h2 className="text-xl font-bold">
                  Languages
                </h2>

                <div className="mt-4 flex flex-wrap gap-2">
                  {guide.languages.map((language) => (
                    <Badge
                      key={language}
                      className="bg-sky-100 text-sky-700"
                    >
                      {language}
                    </Badge>
                  ))}
                </div>
              </div>
            )}
          </div>

          <div className="rounded-2xl border border-[var(--color-border)] bg-white p-6 shadow-sm">
            <h2 className="text-xl font-bold">
              Guide Information
            </h2>

            <div className="mt-6 space-y-5">
              <div>
                <p className="text-sm text-slate-500">
                  Price per day
                </p>

                <p className="mt-1 text-2xl font-bold text-[var(--color-primary)]">
                  ₹
                  {Number(
                    guide.pricePerDay
                  ).toLocaleString("en-IN")}
                </p>
              </div>

              {guide.rating != null && (
                <div>
                  <p className="text-sm text-slate-500">
                    Rating
                  </p>

                  <p className="mt-1 font-semibold">
                    ★ {guide.rating}
                  </p>
                </div>
              )}

              <div>
                <p className="text-sm text-slate-500">
                  Experience
                </p>

                <p className="mt-1 font-medium">
                  {guide.yearsOfExperience}{" "}
                  {guide.yearsOfExperience === 1
                    ? "year"
                    : "years"}
                </p>
              </div>

              {guide.licenseNumber && (
                <div>
                  <p className="text-sm text-slate-500">
                    License Number
                  </p>

                  <p className="mt-1 font-medium">
                    {guide.licenseNumber}
                  </p>
                </div>
              )}

              {guide.phone && (
                <div>
                  <p className="text-sm text-slate-500">
                    Phone
                  </p>

                  <p className="mt-1 font-medium">
                    {guide.phone}
                  </p>
                </div>
              )}

              {guide.email && (
                <div>
                  <p className="text-sm text-slate-500">
                    Email
                  </p>

                  <p className="mt-1 break-words font-medium">
                    {guide.email}
                  </p>
                </div>
              )}
            </div>
          </div>
        </div>
      </Section>
    </>
  );
}