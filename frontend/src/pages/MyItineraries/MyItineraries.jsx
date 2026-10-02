import { useEffect, useState } from "react";
import {
  HiOutlineChevronDown,
  HiOutlineChevronUp,
} from "react-icons/hi2";
import { useNavigate } from "react-router-dom";

import Button from "../../components/common/inputs/Button";
import itineraryService from "../../services/itineraryService";

function MyItineraries() {
  const navigate = useNavigate();

  const [itineraries, setItineraries] = useState([]);
  const [expandedId, setExpandedId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [deletingId, setDeletingId] = useState(null);
  const [error, setError] = useState("");
  const [deleteError, setDeleteError] = useState("");

  useEffect(() => {
    const loadItineraries = async () => {
      setLoading(true);
      setError("");

      try {
        const data =
          await itineraryService.getAllItineraries();

        setItineraries(data);
      } catch (err) {
        console.error(
          "Failed to load itineraries:",
          err
        );

        setError(
          err.response?.data?.message ||
            "Failed to load your itineraries. Please try again."
        );
      } finally {
        setLoading(false);
      }
    };

    loadItineraries();
  }, []);

  const handleToggle = (id) => {
    setDeleteError("");

    setExpandedId((current) =>
      current === id ? null : id
    );
  };

  const handleEdit = (id) => {
    navigate(`/itinerary/${id}/edit`);
  };

  const handleDelete = async (id) => {
    const confirmed = window.confirm(
      "Are you sure you want to delete this itinerary?"
    );

    if (!confirmed) {
      return;
    }

    setDeletingId(id);
    setDeleteError("");

    try {
      await itineraryService.deleteItinerary(id);

      setItineraries((current) =>
        current.filter(
          (itinerary) => itinerary.id !== id
        )
      );

      setExpandedId((current) =>
        current === id ? null : current
      );
    } catch (err) {
      console.error(
        "Failed to delete itinerary:",
        err
      );

      setDeleteError(
        err.response?.data?.message ||
          "Failed to delete the itinerary. Please try again."
      );
    } finally {
      setDeletingId(null);
    }
  };

  const formatDate = (date) => {
    if (!date) return "";

    return new Date(date).toLocaleDateString(
      "en-IN",
      {
        day: "numeric",
        month: "short",
        year: "numeric",
      }
    );
  };

  const getActivityTypeLabel = (
    activityType
  ) => {
    if (!activityType) return "Activity";

    return activityType
      .toLowerCase()
      .replace(/_/g, " ")
      .replace(/\b\w/g, (letter) =>
        letter.toUpperCase()
      );
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gray-50">
        <div className="mx-auto max-w-5xl px-4 py-12">
          <div className="rounded-2xl border border-gray-200 bg-white p-8 text-center shadow-sm">
            <p className="text-gray-600">
              Loading your itineraries...
            </p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="mx-auto max-w-5xl px-4 py-10 sm:px-6 lg:px-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900">
            My Itineraries
          </h1>

          <p className="mt-2 text-gray-600">
            View and manage your saved travel plans.
          </p>
        </div>

        {error && (
          <div className="mb-6 rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
            {error}
          </div>
        )}

        {deleteError && (
          <div className="mb-6 rounded-xl border border-red-200 bg-red-50 p-4 text-sm text-red-700">
            {deleteError}
          </div>
        )}

        {!error && itineraries.length === 0 && (
          <div className="rounded-2xl border border-gray-200 bg-white p-10 text-center shadow-sm">
            <h2 className="text-xl font-semibold text-gray-900">
              No saved itineraries yet
            </h2>

            <p className="mt-2 text-gray-500">
              Create an itinerary to see it here.
            </p>

            <div className="mt-6">
              <Button to="/itinerary">
                Plan My Trip
              </Button>
            </div>
          </div>
        )}

        <div className="space-y-4">
          {itineraries.map((itinerary) => {
            const isExpanded =
              expandedId === itinerary.id;

            return (
              <div
                key={itinerary.id}
                className="overflow-hidden rounded-2xl border border-gray-200 bg-white shadow-sm"
              >
                {/* Compact itinerary header */}
                <button
                  type="button"
                  onClick={() =>
                    handleToggle(itinerary.id)
                  }
                  className="
                    w-full
                    px-5
                    py-5
                    text-left
                    transition-colors
                    hover:bg-gray-50
                    sm:px-6
                  "
                >
                  <div className="flex items-start justify-between gap-4">
                    <div className="min-w-0">
                      <h2 className="truncate text-lg font-semibold text-gray-900">
                        {itinerary.title}
                      </h2>

                      <p className="mt-1 text-sm text-gray-500">
                        {formatDate(
                          itinerary.startDate
                        )}{" "}
                        →{" "}
                        {formatDate(
                          itinerary.endDate
                        )}
                      </p>

                      <div className="mt-3 flex flex-wrap gap-x-5 gap-y-1 text-sm text-gray-600">
                        <span>
                          {
                            itinerary.numberOfTravelers
                          }{" "}
                          traveler
                          {itinerary.numberOfTravelers !==
                          1
                            ? "s"
                            : ""}
                        </span>

                        <span>
                          ₹
                          {Number(
                            itinerary.estimatedBudget
                          ).toLocaleString(
                            "en-IN"
                          )}
                        </span>
                      </div>
                    </div>

                    <div className="shrink-0 rounded-full bg-gray-100 p-2 text-gray-600">
                      {isExpanded ? (
                        <HiOutlineChevronUp
                          size={20}
                        />
                      ) : (
                        <HiOutlineChevronDown
                          size={20}
                        />
                      )}
                    </div>
                  </div>
                </button>

                {/* Expanded itinerary */}
                {isExpanded && (
                  <div className="border-t border-gray-200 px-5 pb-6 pt-5 sm:px-6">
                    {itinerary.description && (
                      <p className="mb-6 text-sm leading-6 text-gray-600">
                        {itinerary.description}
                      </p>
                    )}

                    <div className="space-y-6">
                      {itinerary.items?.length >
                      0 ? (
                        Object.entries(
                          itinerary.items.reduce(
                            (days, item) => {
                              if (
                                !days[
                                  item.dayNumber
                                ]
                              ) {
                                days[
                                  item.dayNumber
                                ] = [];
                              }

                              days[
                                item.dayNumber
                              ].push(item);

                              return days;
                            },
                            {}
                          )
                        ).map(
                          ([
                            dayNumber,
                            dayItems,
                          ]) => (
                            <div
                              key={dayNumber}
                            >
                              <h3 className="mb-3 text-lg font-semibold text-gray-900">
                                Day {dayNumber}
                              </h3>

                              <div className="space-y-3">
                                {dayItems
                                  .sort(
                                    (a, b) =>
                                      a.activityOrder -
                                      b.activityOrder
                                  )
                                  .map(
                                    (item) => (
                                      <div
                                        key={
                                          item.id
                                        }
                                        className="rounded-xl bg-gray-50 p-4"
                                      >
                                        <div className="flex flex-col gap-2 sm:flex-row sm:items-start sm:justify-between">
                                          <div>
                                            <p className="font-medium text-gray-900">
                                              {
                                                item.referenceName
                                              }
                                            </p>

                                            <p className="mt-1 text-sm text-gray-500">
                                              {
                                                item.time
                                              }{" "}
                                              ·{" "}
                                              {getActivityTypeLabel(
                                                item.activityType
                                              )}
                                            </p>
                                          </div>

                                          {item.notes && (
                                            <p className="text-sm text-gray-600 sm:max-w-sm sm:text-right">
                                              {
                                                item.notes
                                              }
                                            </p>
                                          )}
                                        </div>
                                      </div>
                                    )
                                  )}
                              </div>
                            </div>
                          )
                        )
                      ) : (
                        <p className="text-sm text-gray-500">
                          No activities have been
                          added to this itinerary.
                        </p>
                      )}
                    </div>

                    {/* Actions */}
                    <div className="mt-6 flex flex-col gap-3 border-t border-gray-200 pt-5 sm:flex-row sm:justify-end">
                      <Button
                        type="button"
                        variant="outline"
                        onClick={(event) => {
                          event.stopPropagation();
                          handleEdit(
                            itinerary.id
                          );
                        }}
                      >
                        Edit
                      </Button>

                      <Button
                        type="button"
                        variant="outline"
                        disabled={
                          deletingId ===
                          itinerary.id
                        }
                        onClick={(event) => {
                          event.stopPropagation();
                          handleDelete(
                            itinerary.id
                          );
                        }}
                      >
                        {deletingId ===
                        itinerary.id
                          ? "Deleting..."
                          : "Delete"}
                      </Button>
                    </div>
                  </div>
                )}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

export default MyItineraries;