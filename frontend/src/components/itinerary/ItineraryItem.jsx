import Button from "../common/inputs/Button";
import { formatTime } from "../../utils/schedule";

function ItineraryItem({
  item,
  onRemove,
  onMoveUp,
  onMoveDown,
  isFirst,
  isLast,
}) {
  return (
    <div className="rounded-xl border border-gray-200 bg-white p-4 shadow-sm">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-start sm:justify-between">
        <div className="min-w-0 flex-1">
          <div className="flex flex-wrap items-center gap-2">
            <span className="rounded-full bg-primary/10 px-3 py-1 text-xs font-medium text-primary">
              {item.activityType}
            </span>

            <span className="text-sm font-medium text-gray-500">
              {formatTime(item.time) || item.time}
            </span>
          </div>

          <h4 className="mt-3 text-lg font-semibold text-gray-900">
            {item.referenceName}
          </h4>

          {item.notes && (
            <p className="mt-2 text-sm leading-6 text-gray-600">
              {item.notes}
            </p>
          )}
        </div>

        <div className="flex shrink-0 gap-2 sm:flex-col">
          <Button
            type="button"
            variant="outline"
            onClick={onMoveUp}
            disabled={isFirst}
          >
            ↑
          </Button>

          <Button
            type="button"
            variant="outline"
            onClick={onMoveDown}
            disabled={isLast}
          >
            ↓
          </Button>

          <Button
            type="button"
            variant="outline"
            onClick={onRemove}
          >
            Remove
          </Button>
        </div>
      </div>
    </div>
  );
}

export default ItineraryItem;