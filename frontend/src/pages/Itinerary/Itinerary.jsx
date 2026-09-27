import { useState } from "react";

import AiItineraryForm from "../../components/itinerary/AiItineraryForm";
import aiItineraryService from "../../services/aiItineraryService";

function Itinerary() {
  const [loading, setLoading] = useState(false);
  const [draft, setDraft] = useState(null);
  const [error, setError] = useState("");

  const handleGenerate = async (request) => {
    setLoading(true);
    setError("");

    try {
      const generatedItinerary =
        await aiItineraryService.generateAiItinerary(request);

      setDraft(generatedItinerary);
    } catch (err) {
      console.error("Failed to generate AI itinerary:", err);

      setError(
        err.response?.data?.message ||
          "Failed to generate the itinerary. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <AiItineraryForm
        onGenerate={handleGenerate}
        loading={loading}
      />

      {error && (
        <div className="mx-auto max-w-3xl px-4 pb-8">
          <div className="rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
            {error}
          </div>
        </div>
      )}

      {draft && (
        <div className="mx-auto max-w-3xl px-4 pb-12">
          <div className="rounded-2xl border border-gray-200 bg-white p-6 shadow-sm">
            <h2 className="text-2xl font-bold text-gray-900">
              {draft.title}
            </h2>

            <p className="mt-2 text-gray-600">
              {draft.description}
            </p>

            <div className="mt-6 grid gap-4 sm:grid-cols-3">
              <div className="rounded-xl bg-gray-50 p-4">
                <p className="text-sm text-gray-500">
                  Dates
                </p>

                <p className="mt-1 font-medium text-gray-900">
                  {draft.startDate} → {draft.endDate}
                </p>
              </div>

              <div className="rounded-xl bg-gray-50 p-4">
                <p className="text-sm text-gray-500">
                  Travelers
                </p>

                <p className="mt-1 font-medium text-gray-900">
                  {draft.numberOfTravelers}
                </p>
              </div>

              <div className="rounded-xl bg-gray-50 p-4">
                <p className="text-sm text-gray-500">
                  Estimated Budget
                </p>

                <p className="mt-1 font-medium text-gray-900">
                  ₹{draft.estimatedBudget}
                </p>
              </div>
            </div>

            <div className="mt-8">
              <h3 className="text-xl font-semibold text-gray-900">
                Your AI Draft
              </h3>

              <p className="mt-2 text-sm text-gray-500">
                Your itinerary has been generated as a draft.
                It has not been saved yet.
              </p>

              <div className="mt-6 space-y-6">
                {draft.items?.map((item) => (
                  <div
                    key={`${item.dayNumber}-${item.activityOrder}`}
                    className="rounded-xl border border-gray-200 p-4"
                  >
                    <div className="flex flex-wrap items-center justify-between gap-2">
                      <span className="text-sm font-medium text-gray-500">
                        Day {item.dayNumber}
                      </span>

                      <span className="text-sm text-gray-500">
                        {item.time}
                      </span>
                    </div>

                    <h4 className="mt-2 font-semibold text-gray-900">
                      {item.referenceName}
                    </h4>

                    <p className="mt-1 text-sm text-primary">
                      {item.activityType}
                    </p>

                    {item.notes && (
                      <p className="mt-2 text-sm text-gray-600">
                        {item.notes}
                      </p>
                    )}
                  </div>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Itinerary;