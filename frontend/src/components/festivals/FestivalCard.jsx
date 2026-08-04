import Card from "../common/display/Card";
import Button from "../common/inputs/Button";
import Badge from "../common/display/Badge";

export default function FestivalCard({ festival }) {
  return (
    <Card className="p-6 text-center">
      <div className="mx-auto flex h-20 w-20 items-center justify-center rounded-full bg-orange-100 text-5xl">
        {festival.icon}
      </div>

      <h3 className="mt-6 text-xl font-bold leading-tight">
        {festival.name}
      </h3>

      <p className="mt-2 text-slate-600">
        {festival.state}
      </p>

      <div className="mt-4">
        <Badge className="bg-orange-100 text-orange-700">
          {festival.month}
        </Badge>
      </div>

      <Button
        to="/festivals"
        className="mt-6 w-full"
      >
        View Festival
      </Button>
    </Card>
  );
}