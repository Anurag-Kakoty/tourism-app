import Card from "../common/display/Card";
import Badge from "../common/display/Badge";
import Button from "../common/inputs/Button";

export default function TransportCard({ transport }) {
  return (
    <Card className="p-5">
      <div className="flex items-start justify-between gap-3">
        <h3 className="text-xl font-bold">
          {transport.providerName}
        </h3>

        <Badge className="shrink-0">
          {transport.type.replaceAll("_", " ")}
        </Badge>
      </div>

      <p className="mt-3 text-slate-500">
        {transport.destinationName}, {transport.stateName}
      </p>

      <div className="mt-5 space-y-3">
        <div>
          <p className="text-sm text-slate-500">
            Pickup
          </p>

          <p className="font-medium">
            {transport.pickupLocation}
          </p>
        </div>

        <div>
          <p className="text-sm text-slate-500">
            Drop
          </p>

          <p className="font-medium">
            {transport.dropLocation}
          </p>
        </div>

        {transport.estimatedDuration && (
          <div>
            <p className="text-sm text-slate-500">
              Duration
            </p>

            <p className="font-medium">
              {transport.estimatedDuration}
            </p>
          </div>
        )}
      </div>

      {transport.estimatedFare != null && (
        <div className="mt-5">
          <span className="text-2xl font-bold text-[var(--color-primary)]">
            ₹{Number(transport.estimatedFare).toLocaleString("en-IN")}
          </span>

          <span className="ml-1 text-sm text-slate-500">
            estimated fare
          </span>
        </div>
      )}

      <div className="mt-4">
        {transport.available ? (
          <Badge>
            Available
          </Badge>
        ) : (
          <Badge className="bg-slate-100 text-slate-600">
            Currently Unavailable
          </Badge>
        )}
      </div>

      <Button
        to={`/transport/${transport.id}`}
        className="mt-6 w-full"
      >
        View Transport
      </Button>
    </Card>
  );
}