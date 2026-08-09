import Card from "../common/display/Card";
import Badge from "../common/display/Badge";
import Button from "../common/inputs/Button";

export default function FestivalCard({ festival }) {
  return (
    <Card className="overflow-hidden">
      {festival.imageUrl && (
        <img
          src={festival.imageUrl}
          alt={festival.name}
          className="h-56 w-full object-cover"
        />
      )}

      <div className="p-5">
        <h3 className="text-2xl font-bold">
          {festival.name}
        </h3>

        <Badge variant="accent" className="mt-3">
          {festival.category}
        </Badge>

        <p className="mt-4 line-clamp-3 text-slate-600">
          {festival.description}
        </p>

        <Button
          to={`/festivals/${festival.id}`}
          className="mt-6 w-full"
        >
          View Festival
        </Button>
      </div>
    </Card>
  );
}