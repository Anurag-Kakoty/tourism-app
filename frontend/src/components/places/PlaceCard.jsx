import Card from "../common/display/Card";
import Button from "../common/inputs/Button";

export default function PlaceCard({ place }) {
  return (
    <Card className="overflow-hidden">

      <img
        src={place.image}
        alt={place.name}
        className="h-56 w-full object-cover"
      />

      <div className="p-5">

        <div className="flex items-center justify-between">

          <h3 className="text-xl font-bold">
            {place.name}
          </h3>

          <span className="text-yellow-500 font-semibold">
            ★ {place.rating}
          </span>

        </div>

        <p className="mt-1 text-slate-500">
          {place.state}
        </p>

        <span className="mt-4 inline-block rounded-full bg-emerald-100 px-3 py-1 text-sm font-medium text-emerald-700">
          {place.category}
        </span>

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