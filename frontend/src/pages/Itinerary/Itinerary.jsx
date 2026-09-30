import { useState } from "react";

import AiItineraryForm from "../../components/itinerary/AiItineraryForm";
import ItineraryDay from "../../components/itinerary/ItineraryDay";
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
      console.error(
        "Failed to generate AI itinerary:",
        err
      );

      setError(
        err.response?.data?.message ||
          "Failed to generate the itinerary. Please try again."
      );
    } finally {
      setLoading(false);
    }
  };

  const getDayItems = (dayNumber) => {
    if (!draft?.items) {
      return [];
    }

    return draft.items
      .filter(
        (item) => item.dayNumber === dayNumber
      )
      .sort(
        (a, b) =>
          a.activityOrder - b.activityOrder
      );
  };

  const getDayNumbers = () => {
    if (!draft?.items) {
      return [];
    }

    return [
      ...new Set(
        draft.items.map(
          (item) => item.dayNumber
        )
      ),
    ].sort((a, b) => a - b);
  };

  const handleRemoveItem = (
    dayNumber,
    index
  ) => {
    setDraft((current) => {
      const dayItems = current.items
        .filter(
          (item) =>
            item.dayNumber === dayNumber
        )
        .sort(
          (a, b) =>
            a.activityOrder -
            b.activityOrder
        );

      const itemToRemove =
        dayItems[index];

      const remainingDayItems =
        dayItems.filter(
          (item) =>
            item !== itemToRemove
        );

      const updatedDayItems =
        remainingDayItems.map(
          (item, itemIndex) => ({
            ...item,
            activityOrder:
              itemIndex + 1,
          })
        );

      const updatedItems =
        current.items
          .filter(
            (item) =>
              item.dayNumber !== dayNumber
          )
          .concat(updatedDayItems);

      return {
        ...current,
        items: updatedItems,
      };
    });
  };

  const handleMoveItemUp = (
    dayNumber,
    index
  ) => {
    if (index === 0) {
      return;
    }

    setDraft((current) => {
      const dayItems = current.items
        .filter(
          (item) =>
            item.dayNumber === dayNumber
        )
        .sort(
          (a, b) =>
            a.activityOrder -
            b.activityOrder
        );

      const currentItem =
        dayItems[index];

      const previousItem =
        dayItems[index - 1];

      const updatedDayItems = [
        ...dayItems,
      ];

      updatedDayItems[index - 1] = {
        ...currentItem,
        activityOrder:
          index,
        time:
          previousItem.time,
      };

      updatedDayItems[index] = {
        ...previousItem,
        activityOrder:
          index + 1,
        time:
          currentItem.time,
      };

      const updatedItems =
        current.items
          .filter(
            (item) =>
              item.dayNumber !== dayNumber
          )
          .concat(updatedDayItems);

      return {
        ...current,
        items: updatedItems,
      };
    });
  };

  const handleMoveItemDown = (
    dayNumber,
    index
  ) => {
    const dayItems =
      getDayItems(dayNumber);

    if (
      index >=
      dayItems.length - 1
    ) {
      return;
    }

    setDraft((current) => {
      const currentDayItems =
        current.items
          .filter(
            (item) =>
              item.dayNumber ===
              dayNumber
          )
          .sort(
            (a, b) =>
              a.activityOrder -
              b.activityOrder
          );

      const currentItem =
        currentDayItems[index];

      const nextItem =
        currentDayItems[index + 1];

      const updatedDayItems = [
        ...currentDayItems,
      ];

      updatedDayItems[index] = {
        ...nextItem,
        activityOrder:
          index + 1,
        time:
          currentItem.time,
      };

      updatedDayItems[index + 1] = {
        ...currentItem,
        activityOrder:
          index + 2,
        time:
          nextItem.time,
      };

      const updatedItems =
        current.items
          .filter(
            (item) =>
              item.dayNumber !==
              dayNumber
          )
          .concat(updatedDayItems);

      return {
        ...current,
        items: updatedItems,
      };
    });
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
        <div className="mx-auto max-w-4xl px-4 pb-12">
          <div className="rounded-2xl border border-gray-200 bg-white p-6 shadow-sm">
            <div className="border-b border-gray-200 pb-6">
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
                    {draft.startDate} →{" "}
                    {draft.endDate}
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
            </div>

            <div className="mt-8">
              <h3 className="text-xl font-semibold text-gray-900">
                Edit Your Itinerary
              </h3>

              <p className="mt-2 text-sm text-gray-500">
                This is a draft. Remove activities or
                change their order before saving.
              </p>

              <div className="mt-6 space-y-6">
                {getDayNumbers().map(
                  (dayNumber) => (
                    <ItineraryDay
                      key={dayNumber}
                      dayNumber={dayNumber}
                      items={getDayItems(
                        dayNumber
                      )}
                      onRemoveItem={
                        handleRemoveItem
                      }
                      onMoveItemUp={
                        handleMoveItemUp
                      }
                      onMoveItemDown={
                        handleMoveItemDown
                      }
                    />
                  )
                )}
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}

export default Itinerary;