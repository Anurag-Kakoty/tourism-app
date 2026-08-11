import Card from "../common/display/Card";
import Badge from "../common/display/Badge";
import Button from "../common/inputs/Button";

export default function ExperienceCard({ experience }) {
  return (
    <Card className="p-6">
      <div className="flex items-start justify-between gap-4">
        <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-emerald-100 text-2xl text-[var(--color-primary)]">
          {experience.icon || "★"}
        </div>

        <Badge variant="secondary">
          Experience
        </Badge>
      </div>

      <h3 className="mt-6 text-2xl font-bold">
        {experience.name}
      </h3>

      <p className="mt-3 line-clamp-3 text-slate-600">
        {experience.description ||
          `Discover ${experience.name.toLowerCase()} experiences across India.`}
      </p>

      <Button
        to={`/experiences/${experience.id}`}
        className="mt-6 w-full"
      >
        Explore Experience
      </Button>
    </Card>
  );
}