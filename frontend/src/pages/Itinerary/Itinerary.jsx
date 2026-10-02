import { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";

import Button from "../../components/common/inputs/Button";
import AddActivityForm from "../../components/itinerary/AddActivityForm";
import AiItineraryForm from "../../components/itinerary/AiItineraryForm";
import ItineraryDay from "../../components/itinerary/ItineraryDay";

import aiItineraryService from "../../services/aiItineraryService";
import itineraryService from "../../services/itineraryService";

function Itinerary() {
  const { id } = useParams();
  const navigate = useNavigate();

  const isEditMode = Boolean(id);

  const [loading, setLoading] = useState(false);
  const [pageLoading, setPageLoading] = useState(isEditMode);
  const [saving, setSaving] = useState(false);

  const [draft, setDraft] = useState(null);

  const [error, setError] = useState("");
  const [saveMessage, setSaveMessage] = useState("");

  const [showAddActivity, setShowAddActivity] =
    useState(false);

  /*
   * IDs of the itinerary items that existed when
   * edit mode was opened.
   *
   * We use this to determine which activities were
   * removed by the user.
   */
  const [originalItemIds, setOriginalItemIds] =
    useState([]);

  /*
   * Load an existing itinerary when this page is opened
   * through /itinerary/:id/edit.
   */
  useEffect(() => {
    if (!isEditMode) {
      setPageLoading(false);
      return;
    }

    const loadItinerary = async () => {
      setPageLoading(true);
      setError("");
      setSaveMessage("");

      try {
        const savedItinerary =
          await itineraryService.getItineraryById(id);

        setDraft(savedItinerary);

        setOriginalItemIds(
          (savedItinerary.items || [])
            .filter((item) => item.id)
            .map((item) => item.id)
        );
      } catch (err) {
        console.error(
          "Failed to load itinerary:",
          err
        );

        setError(
          err.response?.data?.message ||
            "Failed to load the itinerary. Please try again."
        );
      } finally {
        setPageLoading(false);
      }
    };

    loadItinerary();
  }, [id, isEditMode]);

  /*
   * Generate a completely new AI itinerary.
   */
  const handleGenerate = async (request) => {
    setLoading(true);
    setError("");
    setSaveMessage("");
    setShowAddActivity(false);

    try {
      const generatedItinerary =
        await aiItineraryService.generateAiItinerary(
          request
        );

      setDraft(generatedItinerary);

      setOriginalItemIds([]);
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

  /*
   * Save a completely new itinerary.
   *
   * This is the original save flow:
   *
   * 1. Create itinerary
   * 2. Add each activity
   * 3. Retrieve the saved itinerary
   */
  const handleCreateItinerary = async () => {
    if (!draft || saving) return;

    setSaving(true);
    setError("");
    setSaveMessage("");

    try {
      const itineraryRequest = {
        title: draft.title,
        description: draft.description,
        startDate: draft.startDate,
        endDate: draft.endDate,
        numberOfTravelers: draft.numberOfTravelers,
        estimatedBudget: Number(
          draft.estimatedBudget
        ),
        destinationId: draft.destinationId,
      };

      const createdItinerary =
        await itineraryService.createItinerary(
          itineraryRequest
        );

      const itineraryId = createdItinerary.id;

      if (!itineraryId) {
        throw new Error(
          "The created itinerary did not return an ID."
        );
      }

      const sortedItems = [
        ...(draft.items || []),
      ].sort((a, b) => {
        if (a.dayNumber !== b.dayNumber) {
          return a.dayNumber - b.dayNumber;
        }

        return (
          a.activityOrder - b.activityOrder
        );
      });

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

  /*
   * Update an existing saved itinerary.
   *
   * 1. Update itinerary details
   * 2. Delete activities removed from the draft
   * 3. Update existing activities
   * 4. Add newly-created activities
   * 5. Retrieve the final persisted itinerary
   */
  const handleUpdateItinerary = async () => {
    if (!draft || saving || !id) return;

    setSaving(true);
    setError("");
    setSaveMessage("");

    try {
      const itineraryRequest = {
        title: draft.title,
        description: draft.description,
        startDate: draft.startDate,
        endDate: draft.endDate,
        numberOfTravelers: draft.numberOfTravelers,
        estimatedBudget: Number(
          draft.estimatedBudget
        ),
        destinationId: draft.destinationId,
      };

      await itineraryService.updateItinerary(
        id,
        itineraryRequest
      );

      const currentItems = [
        ...(draft.items || []),
      ];

      /*
       * Determine which original activities have
       * been removed.
       */
      const currentItemIds = currentItems
        .filter((item) => item.id)
        .map((item) => item.id);

      const removedItemIds =
        originalItemIds.filter(
          (originalItemId) =>
            !currentItemIds.includes(
              originalItemId
            )
        );

      /*
       * Delete removed activities.
       */
      for (const itemId of removedItemIds) {
        await itineraryService.deleteItineraryItem(
          id,
          itemId
        );
      }

      /*
       * Sort activities before persisting them.
       */
      const sortedItems = [...currentItems].sort(
        (a, b) => {
          if (a.dayNumber !== b.dayNumber) {
            return a.dayNumber - b.dayNumber;
          }

          return (
            a.activityOrder - b.activityOrder
          );
        }
      );

      /*
       * Update existing activities and add new ones.
       */
      for (const item of sortedItems) {
        const itemRequest = {
          dayNumber: item.dayNumber,
          activityOrder: item.activityOrder,
          time: item.time,
          activityType: item.activityType,
          referenceId: item.referenceId,
          notes: item.notes || null,
        };

        if (item.id) {
          await itineraryService.updateItineraryItem(
            id,
            item.id,
            itemRequest
          );
        } else {
          await itineraryService.addItineraryItem(
            id,
            itemRequest
          );
        }
      }

      /*
       * Retrieve the final persisted version.
       */
      const updatedItinerary =
        await itineraryService.getItineraryById(
          id
        );

      setDraft(updatedItinerary);

      setOriginalItemIds(
        (updatedItinerary.items || [])
          .filter((item) => item.id)
          .map((item) => item.id)
      );

      setSaveMessage(
        "Itinerary updated successfully."
      );

      setShowAddActivity(false);

      /*
       * Return to My Itineraries after a short delay
       * so the success message can be seen.
       */
      setTimeout(() => {
        navigate("/my-itineraries");
      }, 700);
    } catch (err) {
      console.error(
        "Failed to update itinerary:",
        err
      );

      setError(
        err.response?.data?.message ||
          err.message ||
          "Failed to update the itinerary. Please try again."
      );
    } finally {
      setSaving(false);
    }
  };

  /*
   * Decide whether the Save button should create
   * or update an itinerary.
   */
  const handleSaveItinerary = async () => {
    if (isEditMode) {
      await handleUpdateItinerary();
      return;
    }

    await handleCreateItinerary();
  };

  const getDayItems = (dayNumber) => {
    if (!draft?.items) return [];

    return draft.items
      .filter(
        (item) =>
          item.dayNumber === dayNumber
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
      if (!current) return current;

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

      if (!itemToRemove) {
        return current;
      }

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
      if (!current) return current;

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
      index >= dayItems.length - 1
    ) {
      return;
    }

    setDraft((current) => {
      if (!current) return current;

      const currentDayItems =
        current.items
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
            item.time > newItem.time
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

  /*
   * Prevent the editor from rendering before an
   * existing itinerary has been loaded.
   */
  if (pageLoading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="mx-auto max-w-4xl px-4 py-12">
          <div className="rounded-2xl border border-gray-200 bg-white p-8 text-center shadow-sm">
            <p className="text-gray-600">
              Loading itinerary...
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      {!isEditMode && (
        <AiItineraryForm
          onGenerate={handleGenerate}
          loading={loading}
        />
      )}

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
              <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
                <div>
                  <h2 className="text-2xl font-bold text-gray-900">
                    {draft.title}
                  </h2>

                  <p className="mt-2 text-gray-600">
                    {draft.description}
                  </p>
                </div>

                {isEditMode && (
                  <Button
                    type="button"
                    variant="outline"
                    onClick={() =>
                      navigate(
                        "/my-itineraries"
                      )
                    }
                    disabled={saving}
                  >
                    Back to My Itineraries
                  </Button>
                )}
              </div>

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
                    {isEditMode
                      ? "Edit Your Itinerary"
                      : "Edit Your Itinerary"}
                  </h3>

                  <p className="mt-2 text-sm text-gray-500">
                    {isEditMode
                      ? "Modify your saved itinerary, then update it when you are finished."
                      : "This is a draft. Remove activities, add new ones, or change their order before saving."}
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
                      ? isEditMode
                        ? "Updating Itinerary..."
                        : "Saving Itinerary..."
                      : isEditMode
                      ? "Update Itinerary"
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