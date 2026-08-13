import { useEffect, useState } from "react";
import { useParams } from "react-router-dom";

import Section from "../../components/common/layout/Section";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import Badge from "../../components/common/display/Badge";

import transportService from "../../services/transportService";

export default function TransportDetails() {
  const { id } = useParams();

  const [transport, setTransport] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadTransport();
  }, [id]);

  async function loadTransport() {
    try {
      setLoading(true);
      setError("");

      const data = await transportService.getById(id);

      setTransport(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load transport option.");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <Section title="Transport">
        <LoadingSpinner message="Loading transport option..." />
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

  if (!transport) {
    return (
      <Section title="Transport">
        <ErrorMessage message="Transport option not found." />
      </Section>
    );
  }

  return (
    <Section>
      <div className="mx-auto max-w-4xl">
        <div className="flex flex-wrap items-center gap-3">
          <Badge>
            {transport.type.replaceAll("_", " ")}
          </Badge>

          {transport.available ? (
            <Badge>
              Available
            </Badge>
          ) : (
            <Badge className="bg-slate-100 text-slate-600">
              Currently Unavailable
            </Badge>
          )}
        </div>

        <h1 className="mt-5 text-4xl font-bold sm:text-5xl">
          {transport.providerName}
        </h1>

        <p className="mt-3 text-lg text-slate-500">
          {transport.destinationName}, {transport.stateName}
        </p>

        <div className="mt-10 grid gap-6 sm:grid-cols-2">
          <div className="rounded-2xl border border-[var(--color-border)] bg-white p-6 shadow-sm">
            <p className="text-sm text-slate-500">
              Pickup Location
            </p>

            <p className="mt-2 text-lg font-semibold">
              {transport.pickupLocation}
            </p>
          </div>

          <div className="rounded-2xl border border-[var(--color-border)] bg-white p-6 shadow-sm">
            <p className="text-sm text-slate-500">
              Drop Location
            </p>

            <p className="mt-2 text-lg font-semibold">
              {transport.dropLocation}
            </p>
          </div>

          <div className="rounded-2xl border border-[var(--color-border)] bg-white p-6 shadow-sm">
            <p className="text-sm text-slate-500">
              Estimated Duration
            </p>

            <p className="mt-2 text-lg font-semibold">
              {transport.estimatedDuration || "Not specified"}
            </p>
          </div>

          <div className="rounded-2xl border border-[var(--color-border)] bg-white p-6 shadow-sm">
            <p className="text-sm text-slate-500">
              Estimated Fare
            </p>

            <p className="mt-2 text-2xl font-bold text-[var(--color-primary)]">
              {transport.estimatedFare != null
                ? `₹${Number(
                    transport.estimatedFare
                  ).toLocaleString("en-IN")}`
                : "Not specified"}
            </p>
          </div>
        </div>

        <div className="mt-8 rounded-2xl border border-[var(--color-border)] bg-white p-6 shadow-sm">
          <h2 className="text-xl font-bold">
            Provider Information
          </h2>

          <div className="mt-5 space-y-4">
            {transport.contactNumber && (
              <div>
                <p className="text-sm text-slate-500">
                  Contact
                </p>

                <p className="font-medium">
                  {transport.contactNumber}
                </p>
              </div>
            )}

            {transport.website && (
              <div>
                <p className="text-sm text-slate-500">
                  Website
                </p>

                <a
                  href={transport.website}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="font-medium text-[var(--color-primary)] hover:underline"
                >
                  Visit Website →
                </a>
              </div>
            )}

            {transport.bookingUrl && (
              <div>
                <p className="text-sm text-slate-500">
                  Booking
                </p>

                <a
                  href={transport.bookingUrl}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="font-medium text-[var(--color-primary)] hover:underline"
                >
                  Book Transport →
                </a>
              </div>
            )}
          </div>
        </div>
      </div>
    </Section>
  );
}