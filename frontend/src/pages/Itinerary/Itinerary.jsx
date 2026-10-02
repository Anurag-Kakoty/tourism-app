import { useState } from "react";

import Button from "../../components/common/inputs/Button";
import AddActivityForm from "../../components/itinerary/AddActivityForm";
import AiItineraryForm from "../../components/itinerary/AiItineraryForm";
import ItineraryDay from "../../components/itinerary/ItineraryDay";
import aiItineraryService from "../../services/aiItineraryService";
import itineraryService from "../../services/itineraryService";

function Itinerary() {
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);

  const [draft, setDraft] = useState(null);

  const [error, setError] = useState("");
  const [saveMessage, setSaveMessage] = useState("");

  const [showAddActivity, setShowAddActivity] =
    useState(false);

  const handleGenerate = async (request) => {
    setLoading(true);
    setError("");
    setSaveMessage("");
    setShowAddActivity(false);

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

  const handleSaveItinerary = async () => {
    if (!draft || saving) return;

    setSaving(true);
    setError("");
    setSaveMessage("");

    try {
      /*
       * Step 1:
       * Create the itinerary itself.
       */
      const itineraryRequest = {
        title: draft.title,
        description: draft.description,
        startDate: draft.startDate,
        endDate: draft.endDate,
        numberOfTravelers: draft.numberOfTravelers,
        estimatedBudget: Number(draft.estimatedBudget),
        destinationId: draft.destinationId,
      };

      const createdItinerary =
        await itineraryService.createItinerary(
          itineraryRequest
        );

      /*
       * The backend should return the newly-created
       * itinerary ID.
       */
      const itineraryId = createdItinerary.id;

      if (!itineraryId) {
        throw new Error(
          "The created itinerary did not return an ID."
        );
      }

      /*
       * Step 2:
       * Add each edited draft activity to the
       * newly-created itinerary.
       */
      const sortedItems = [...(draft.items || [])].sort(
        (a, b) => {
          if (a.dayNumber !== b.dayNumber) {
            return a.dayNumber - b.dayNumber;
          }

          return a.activityOrder - b.activityOrder;
        }
      );

      for (const item of sortedItems) {
        const itemRequest = {
          dayNumber: item.dayNumber,
          activityOrder: item.activityOrder,
          time: item.time,
          activityType: item.activityType,
          referenceId: item.referenceId,
          notes: item.notes || null,
        };

        await itineraryService.addItineraryItem(
          itineraryId,
          itemRequest
        );
      }

      /*
       * Step 3:
       * Retrieve the final saved itinerary so that
       * the frontend has the actual persisted data.
       */
      const savedItinerary =
        await itineraryService.getItineraryById(
          itineraryId
        );

      setDraft(savedItinerary);

      setSaveMessage(
        "Itinerary saved successfully."
      );
    } catch (err) {
      console.error(
        "Failed to save itinerary:",
        err
      );

      setError(
        err.response?.data?.message ||
          err.message ||
          "Failed to save the itinerary. Please try again."
      );
    } finally {
      setSaving(false);
    }
  };

  const getDayItems = (dayNumber) => {
    if (!draft?.items) return [];

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
    if (!draft?.items) return [];

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

    setSaveMessage("");
  };

  const handleMoveItemUp = (
    dayNumber,
    index
  ) => {
    if (index === 0) return;

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
        activityOrder: index,
        time: previousItem.time,
      };

      updatedDayItems[index] = {
        ...previousItem,
        activityOrder: index + 1,
        time: currentItem.time,
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

    setSaveMessage("");
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
        activityOrder: index + 1,
        time: currentItem.time,
      };

      updatedDayItems[index + 1] = {
        ...currentItem,
        activityOrder: index + 2,
        time: nextItem.time,
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

    setSaveMessage("");
  };

  const handleAddActivity = (
    activity
  ) => {
    setDraft((current) => {
      if (!current) return current;

      const dayItems = current.items
        .filter(
          (item) =>
            item.dayNumber ===
            activity.dayNumber
        )
        .sort(
          (a, b) =>
            a.activityOrder -
            b.activityOrder
        );

      const newItem = {
        ...activity,
      };

      const insertIndex =
        dayItems.findIndex(
          (item) =>
            item.time >
            newItem.time
        );

      let updatedDayItems;

      if (insertIndex === -1) {
        updatedDayItems = [
          ...dayItems,
          newItem,
        ];
      } else {
        updatedDayItems = [
          ...dayItems.slice(
            0,
            insertIndex
          ),
          newItem,
          ...dayItems.slice(
            insertIndex
          ),
        ];
      }

      updatedDayItems =
        updatedDayItems.map(
          (item, index) => ({
            ...item,
            dayNumber:
              activity.dayNumber,
            activityOrder:
              index + 1,
          })
        );

      const updatedItems =
        current.items
          .filter(
            (item) =>
              item.dayNumber !==
              activity.dayNumber
          )
          .concat(updatedDayItems);

      return {
        ...current,
        items: updatedItems,
      };
    });

    setShowAddActivity(false);
    setSaveMessage("");
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

      {saveMessage && (
        <div className="mx-auto max-w-3xl px-4 pb-8">
          <div className="rounded-xl border border-green-200 bg-green-50 p-4 text-sm text-green-700">
            {saveMessage}
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
              <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                <div>
                  <h3 className="text-xl font-semibold text-gray-900">
                    Edit Your Itinerary
                  </h3>

                  <p className="mt-2 text-sm text-gray-500">
                    This is a draft. Remove activities,
                    add new ones, or change their order
                    before saving.
                  </p>
                </div>

                <div className="flex flex-col gap-3 sm:flex-row">
                  <Button
                    type="button"
                    onClick={() =>
                      setShowAddActivity(
                        (current) =>
                          !current
                      )
                    }
                    disabled={saving}
                  >
                    {showAddActivity
                      ? "Close Add Activity"
                      : "Add Activity"}
                  </Button>

                  <Button
                    type="button"
                    onClick={
                      handleSaveItinerary
                    }
                    disabled={saving}
                  >
                    {saving
                      ? "Saving Itinerary..."
                      : "Save Itinerary"}
                  </Button>
                </div>
              </div>

              {showAddActivity && (
                <div className="mt-6">
                  <AddActivityForm
                    destinationId={
                      draft.destinationId
                    }
                    dayNumbers={getDayNumbers()}
                    startDate={
                      draft.startDate
                    }
                    onAdd={
                      handleAddActivity
                    }
                    onCancel={() =>
                      setShowAddActivity(
                        false
                      )
                    }
                  />
                </div>
              )}

              <div className="mt-6 space-y-6">
                {getDayNumbers().map(
                  (dayNumber) => (
                    <ItineraryDay
                      key={dayNumber}
                      dayNumber={
                        dayNumber
                      }
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