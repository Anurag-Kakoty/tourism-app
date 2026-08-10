import Card from "../common/display/Card";
import Badge from "../common/display/Badge";
import Button from "../common/inputs/Button";

function formatDate(dateString) {
  return new Date(`${dateString}T00:00:00`).toLocaleDateString(
    "en-IN",
    {
      day: "numeric",
      month: "short",
      year: "numeric",
    }
  );
}

export default function FestivalOccurrenceCard({ occurrence }) {
  return (
    <Card className="p-5">
      <div className="flex items-start justify-between gap-4">
        <div>
          <h3 className="text-xl font-bold">
            {occurrence.festivalName}
          </h3>

          <p className="mt-2 text-slate-500">
            {occurrence.stateName}
          </p>
        </div>

        {occurrence.confirmed && (
          <Badge className="shrink-0 text-xs">
            Confirmed
          </Badge>
        )}
      </div>

      <div className="mt-5">
        <p className="text-sm text-slate-500">
          Dates
        </p>

        <p className="mt-1 font-semibold text-slate-900">
          {formatDate(occurrence.startDate)}
          {occurrence.endDate !== occurrence.startDate &&
            ` – ${formatDate(occurrence.endDate)}`}
        </p>
      </div>

      {occurrence.notes && (
        <p className="mt-4 line-clamp-3 text-slate-600">
          {occurrence.notes}
        </p>
      )}

      <Button
        to={`/festival-occurrences/${occurrence.id}`}
        variant="outline"
        className="mt-6 w-full"
      >
        View Details
      </Button>
    </Card>
  );
}