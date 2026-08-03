import Section from "../common/layout/Section";
import FestivalCard from "../festivals/FestivalCard";

import festivals from "../../data/home/festivals";

export default function FestivalsSection() {
  return (
    <Section
      title="Upcoming Festivals"
      subtitle="Experience India's vibrant celebrations throughout the year."
      action={{
        label: "View All",
        to: "/festivals",
      }}
    >
      <div className="grid gap-8 md:grid-cols-2 xl:grid-cols-4">

        {festivals.map((festival) => (
          <FestivalCard
            key={festival.id}
            festival={festival}
          />
        ))}

      </div>
    </Section>
  );
}