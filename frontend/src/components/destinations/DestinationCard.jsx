import Card from "../common/display/Card";
import Badge from "../common/display/Badge";
import Button from "../common/inputs/Button";

export default function DestinationCard({ destination }) {
  return (
    <Card className="overflow-hidden">
      {destination.thumbnailUrl && (
        <img
          src={destination.thumbnailUrl}
          alt={destination.name}
          className="h-56 w-full object-cover"
        />
      )}

      <div className="p-5">
        <div className="flex items-start justify-between gap-3">
          <h3 className="text-2xl font-bold">
            {destination.name}
          </h3>

          {destination.featured && (
            <Badge className="shrink-0 text-xs">
              Featured
            </Badge>
          )}
        </div>

        <p className="mt-2 text-sm font-medium text-[var(--color-primary)]">
          {destination.tagline}
        </p>

        <p className="mt-2 text-slate-500">
          {destination.stateName}
        </p>

        <Badge variant="secondary" className="mt-4">
          {destination.type.replaceAll("_", " ")}
        </Badge>

        <p className="mt-4 line-clamp-3 text-slate-600">
          {destination.description}
        </p>

        <Button
          to={`/destinations/${destination.id}`}
          className="mt-6 w-full"
        >
          Explore Destination
        </Button>
      </div>
    </Card>
  );
}