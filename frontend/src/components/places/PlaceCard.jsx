import Card from "../common/display/Card";
import Badge from "../common/display/Badge";
import Button from "../common/inputs/Button";

export default function PlaceCard({ place }) {
  return (
    <Card className="overflow-hidden">
      {place.thumbnailUrl && (
        <img
          src={place.thumbnailUrl}
          alt={place.name}
          className="h-56 w-full object-cover"
        />
      )}

      <div className="p-5">
        <div className="flex items-center justify-between gap-3">
          <h3 className="text-xl font-bold">
            {place.name}
          </h3>

          {place.featured && (
            <Badge className="shrink-0 text-xs">
              Featured
            </Badge>
          )}
        </div>

        <p className="mt-2 text-slate-500">
          {place.destinationName}, {place.stateName}
        </p>

        {(place.tagNames ?? []).length > 0 && (
          <div className="mt-4 flex flex-wrap gap-2">
            {(place.tagNames ?? []).map((tag) => (
              <Badge key={tag}>
                {tag}
              </Badge>
            ))}
          </div>
        )}

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