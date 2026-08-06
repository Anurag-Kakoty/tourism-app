import Card from "../common/display/Card";
import Button from "../common/inputs/Button";

export default function PlaceCard({ place }) {
  return (
    <Card className="overflow-hidden">
      <img
        src={place.thumbnailUrl}
        alt={place.name}
        className="h-56 w-full object-cover"
      />

      <div className="p-5">
        <div className="flex items-center justify-between">
          <h3 className="text-xl font-bold">
            {place.name}
          </h3>

          {place.featured && (
            <span className="rounded-full bg-emerald-100 px-2 py-1 text-xs font-semibold text-emerald-700">
              Featured
            </span>
          )}
        </div>

        <p className="mt-2 text-slate-500">
          {place.destinationName}, {place.stateName}
        </p>

        <div className="mt-4 flex flex-wrap gap-2">
          {(place.tagNames ?? []).map((tag) => (
            <span
              key={tag}
              className="rounded-full bg-emerald-100 px-3 py-1 text-sm font-medium text-emerald-700"
            >
              {tag}
            </span>
          ))}
        </div>

        <Button
          to={`/places/${place.id}`}
          className="mt-6 w-full"
        >
          View Details
        </Button>
      </div>
    </Card>
  );
}