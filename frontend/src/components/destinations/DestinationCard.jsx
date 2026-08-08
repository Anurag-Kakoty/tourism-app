import Card from "../common/display/Card";
import Button from "../common/inputs/Button";

export default function DestinationCard({ destination }) {
  return (
    <Card className="overflow-hidden">
      <img
        src={destination.thumbnailUrl}
        alt={destination.name}
        className="h-56 w-full object-cover"
      />

      <div className="p-5">
        <div className="flex items-start justify-between gap-3">
          <h3 className="text-2xl font-bold">
            {destination.name}
          </h3>

          {destination.featured && (
            <span className="shrink-0 rounded-full bg-emerald-100 px-3 py-1 text-xs font-semibold text-emerald-700">
              Featured
            </span>
          )}
        </div>

        <p className="mt-2 text-sm font-medium text-[var(--color-primary)]">
          {destination.tagline}
        </p>

        <p className="mt-2 text-slate-500">
          {destination.stateName}
        </p>

        <span className="mt-4 inline-block rounded-full bg-sky-100 px-3 py-1 text-sm font-medium text-sky-700">
          {destination.type.replaceAll("_", " ")}
        </span>

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