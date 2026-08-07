import Card from "../common/display/Card";
import Button from "../common/inputs/Button";

export default function StateCard({ state }) {
  return (
    <Card className="overflow-hidden">
      <img
        src={state.thumbnailUrl}
        alt={state.name}
        className="h-56 w-full object-cover"
      />

      <div className="p-5">
        <h3 className="text-2xl font-bold">
          {state.name}
        </h3>

        <p className="mt-2 text-slate-500">
          Capital: {state.capital}
        </p>

        <p className="mt-4 line-clamp-3 text-slate-600">
          {state.description}
        </p>

        <Button
          to={`/states/${state.id}`}
          className="mt-6 w-full"
        >
          Explore State
        </Button>
      </div>
    </Card>
  );
}