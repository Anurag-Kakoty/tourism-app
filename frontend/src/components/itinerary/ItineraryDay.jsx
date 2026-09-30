import ItineraryItem from "./ItineraryItem";

function ItineraryDay({
  dayNumber,
  items,
  onRemoveItem,
  onMoveItemUp,
  onMoveItemDown,
}) {
  return (
    <section className="rounded-2xl border border-gray-200 bg-gray-50 p-5">
      <div className="mb-5">
        <h3 className="text-xl font-bold text-gray-900">
          Day {dayNumber}
        </h3>

        <p className="mt-1 text-sm text-gray-500">
          {items.length}{" "}
          {items.length === 1 ? "activity" : "activities"}
        </p>
      </div>

      <div className="space-y-4">
        {items.map((item, index) => (
          <ItineraryItem
            key={`${item.dayNumber}-${item.activityOrder}-${item.referenceId}`}
            item={item}
            isFirst={index === 0}
            isLast={index === items.length - 1}
            onRemove={() =>
              onRemoveItem(dayNumber, index)
            }
            onMoveUp={() =>
              onMoveItemUp(dayNumber, index)
            }
            onMoveDown={() =>
              onMoveItemDown(dayNumber, index)
            }
          />
        ))}
      </div>
    </section>
  );
}

export default ItineraryDay;