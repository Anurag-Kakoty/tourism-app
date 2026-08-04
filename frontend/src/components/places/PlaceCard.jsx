import Card from "../common/display/Card";
import Button from "../common/inputs/Button";
import Badge from "../common/display/Badge";

export default function PlaceCard({ place }) {
  return (
    <Card>
      <div className="overflow-hidden">
        <img
          src={place.image}
          alt={place.name}
          className="h-56 w-full object-cover transition-transform duration-300 hover:scale-105"
        />
      </div>

      <div className="p-6">
        <div className="flex items-start justify-between gap-4">
          <h3 className="text-xl font-bold leading-tight">
            {place.name}
          </h3>

          <div className="flex items-center gap-1 whitespace-nowrap font-semibold text-yellow-500">
            <span>★</span>
            <span>{place.rating}</span>
          </div>
        </div>

        <p className="mt-2 text-slate-500">
          {place.state}
        </p>

        <div className="mt-4">
          <Badge>
            {place.category}
          </Badge>
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