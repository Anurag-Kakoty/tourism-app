import Section from "../common/layout/Section";
import ExperienceCard from "../experiences/ExperienceCard";
import experiences from "../../data/home/experiences";

export default function ExperiencesSection() {
  return (
    <Section
      title="Popular Experiences"
      subtitle="Choose the kind of journey you're looking for."
      action={{
        label: "View All",
        to: "/experiences",
      }}
    >
      <div className="grid gap-8 md:grid-cols-2 xl:grid-cols-4">
        {experiences.map((experience) => (
          <ExperienceCard
            key={experience.id}
            experience={experience}
          />
        ))}
      </div>
    </Section>
  );
}