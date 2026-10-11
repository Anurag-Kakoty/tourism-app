import { HiOutlineSparkles } from "react-icons/hi2";
import Card from "../common/display/Card";
import Badge from "../common/display/Badge";
import Button from "../common/inputs/Button";

export default function PlaceCard({ place, recommendation }) {
  const isFallback = recommendation?.isFallback;
  const isPersonalized = recommendation && !isFallback;
  const matchedTags = recommendation?.matchedTags || [];

  return (
    <Card className="flex flex-col justify-between overflow-hidden">
      <div>
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

          {/* Recommendation explanation or clean fallback */}
          {recommendation && (
            <div className="mt-3">
              {isPersonalized ? (
                <div className="flex items-start gap-2 rounded-xl border border-emerald-200/80 bg-emerald-50 px-3 py-2 text-xs font-medium text-emerald-800">
                  <HiOutlineSparkles
                    className="mt-0.5 shrink-0 text-emerald-600"
                    size={15}
                  />
                  <span>
                    {recommendation.explanation ||
                      (matchedTags.length > 0
                        ? `Matched: ${matchedTags.join(", ")}`
                        : "Recommended for you")}
                  </span>
                </div>
              ) : (
                <div className="flex items-center gap-1.5 rounded-xl border border-slate-200 bg-slate-50 px-3 py-1.5 text-xs font-medium text-slate-600">
                  <HiOutlineSparkles
                    className="shrink-0 text-amber-500"
                    size={14}
                  />
                  <span>Popular attraction</span>
                </div>
              )}
            </div>
          )}

          {(place.tagNames ?? []).length > 0 && (
            <div className="mt-4 flex flex-wrap gap-2">
              {(place.tagNames ?? []).map((tag) => {
                const isMatched =
                  isPersonalized && matchedTags.includes(tag);
                return (
                  <Badge
                    key={tag}
                    variant={isMatched ? "primary" : "neutral"}
                  >
                    {tag}
                  </Badge>
                );
              })}
            </div>
          )}
        </div>
      </div>

      <div className="p-5 pt-0">
        <Button
          to={`/places/${place.id}`}
          className="mt-2 w-full"
        >
          View Details
        </Button>
      </div>
    </Card>
  );
}