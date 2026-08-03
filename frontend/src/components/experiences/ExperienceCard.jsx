import Card from "../common/display/Card";
import Button from "../common/inputs/Button";

export default function ExperienceCard({ experience }) {
  return (
    <Card className="p-6 text-center">

      <div className="text-5xl">
        {experience.icon}
      </div>

      <h3 className="mt-4 text-xl font-bold">
        {experience.name}
      </h3>

      <p className="mt-2 text-slate-600">
        {experience.destinations} destinations
      </p>

      <Button
        to={`/experiences?category=${encodeURIComponent(experience.name)}`}
        className="mt-6 w-full"
      >
        Explore
      </Button>

    </Card>
  );
}