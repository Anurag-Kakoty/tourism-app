import Section from "../common/layout/Section";
import PlaceCard from "../places/PlaceCard";

import featuredDestinations from "../../data/featuredDestinations";

export default function FeaturedDestinations() {
  return (
    <Section
        title="Featured Destinations"
        subtitle="Explore some of India's most loved travel destinations."
        action={{
            label: "View All",
            to: "/places",
        }}
    >
      <div className="grid gap-8 md:grid-cols-2 xl:grid-cols-4">

        {featuredDestinations.map((place) => (
          <PlaceCard
            key={place.id}
            place={place}
          />
        ))}

      </div>

    </Section>
  );
}