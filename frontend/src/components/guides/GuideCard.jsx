import Card from "../common/display/Card";
import Badge from "../common/display/Badge";
import Button from "../common/inputs/Button";

export default function GuideCard({ guide }) {
  return (
    <Card className="overflow-hidden">
      {guide.imageUrl && (
        <img
          src={guide.imageUrl}
          alt={guide.name}
          className="h-56 w-full object-cover"
        />
      )}

      <div className="p-5">
        <div className="flex items-start justify-between gap-3">
          <h3 className="text-xl font-bold">
            {guide.name}
          </h3>

          {guide.available && (
            <Badge className="shrink-0">
              Available
            </Badge>
          )}
        </div>

        <p className="mt-3 text-slate-500">
          {guide.destinationName}, {guide.stateName}
        </p>

        {guide.rating != null && (
          <div className="mt-3 flex items-center gap-2">
            <span className="font-semibold">
              ★ {guide.rating}
            </span>

            <span className="text-sm text-slate-500">
              Rating
            </span>
          </div>
        )}

        <p className="mt-3 text-sm text-slate-600">
          {guide.yearsOfExperience}{" "}
          {guide.yearsOfExperience === 1
            ? "year"
            : "years"}{" "}
          of experience
        </p>

        {guide.languages?.length > 0 && (
          <div className="mt-4 flex flex-wrap gap-2">
            {guide.languages.map((language) => (
              <Badge
                key={language}
                className="bg-sky-100 text-sky-700"
              >
                {language}
              </Badge>
            ))}
          </div>
        )}

        <div className="mt-4">
          <span className="text-2xl font-bold text-[var(--color-primary)]">
            ₹{Number(guide.pricePerDay).toLocaleString("en-IN")}
          </span>

          <span className="ml-1 text-sm text-slate-500">
            / day
          </span>
        </div>

        {guide.providesTransport && (
          <p className="mt-3 text-sm font-medium text-slate-600">
            ✓ Transport provided
          </p>
        )}

        <Button
          to={`/guides/${guide.id}`}
          className="mt-6 w-full"
        >
          View Guide
        </Button>
      </div>
    </Card>
  );
}