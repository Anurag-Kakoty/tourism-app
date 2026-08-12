import Card from "../common/display/Card";
import Badge from "../common/display/Badge";
import Button from "../common/inputs/Button";

export default function StayCard({ stay }) {
  return (
    <Card className="overflow-hidden">
      {stay.imageUrl && (
        <img
          src={stay.imageUrl}
          alt={stay.name}
          className="h-56 w-full object-cover"
        />
      )}

      <div className="p-5">
        <div className="flex items-start justify-between gap-3">
          <h3 className="text-xl font-bold">
            {stay.name}
          </h3>

          <Badge className="shrink-0">
            {stay.type.replaceAll("_", " ")}
          </Badge>
        </div>

        <p className="mt-3 text-slate-500">
          {stay.destinationName}, {stay.stateName}
        </p>

        {stay.rating != null && (
          <div className="mt-3 flex items-center gap-2">
            <span className="font-semibold">
              ★ {stay.rating}
            </span>

            <span className="text-sm text-slate-500">
              Rating
            </span>
          </div>
        )}

        <div className="mt-4">
          <span className="text-2xl font-bold text-[var(--color-primary)]">
            ₹{Number(stay.pricePerNight).toLocaleString("en-IN")}
          </span>

          <span className="ml-1 text-sm text-slate-500">
            / night
          </span>
        </div>

        <div className="mt-3">
          {stay.available ? (
            <Badge>
              Available
            </Badge>
          ) : (
            <Badge className="bg-slate-100 text-slate-600">
              Currently Unavailable
            </Badge>
          )}
        </div>

        <Button
          to={`/stay/${stay.id}`}
          className="mt-6 w-full"
        >
          View Stay
        </Button>
      </div>
    </Card>
  );
}