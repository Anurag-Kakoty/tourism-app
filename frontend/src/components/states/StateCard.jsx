import Card from "../common/display/Card";
import Button from "../common/inputs/Button";

export default function StateCard({ state }) {
  return (
    <Card className="overflow-hidden">

      <div className="h-48 overflow-hidden">
        <img
          src={state.image}
          alt={state.name}
          className="h-full w-full object-cover transition-transform duration-300 hover:scale-105"
        />
      </div>

      <div className="p-6">

        <h3 className="text-xl font-bold">
          {state.name}
        </h3>

        <p className="mt-2 text-slate-600">
          {state.destinations} destinations
        </p>

        <Button
          to={`/places?state=${encodeURIComponent(state.name)}`}
          className="mt-6 w-full"
        >
          Explore
        </Button>

      </div>

    </Card>
  );
}