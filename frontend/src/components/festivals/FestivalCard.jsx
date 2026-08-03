import Card from "../common/display/Card";
import Button from "../common/inputs/Button";

export default function FestivalCard({ festival }) {
  return (
    <Card className="p-6">

      <div className="text-5xl">
        {festival.icon}
      </div>

      <h3 className="mt-5 text-xl font-bold">
        {festival.name}
      </h3>

      <p className="mt-2 text-slate-600">
        {festival.state}
      </p>

      <span className="mt-4 inline-block rounded-full bg-orange-100 px-3 py-1 text-sm font-medium text-orange-700">
        {festival.month}
      </span>

      <Button
        to="/festivals"
        className="mt-6 w-full"
      >
        View Festival
      </Button>

    </Card>
  );
}