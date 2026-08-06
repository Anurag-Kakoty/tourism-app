import Section from "../../components/common/layout/Section";
import PlaceCard from "../../components/places/PlaceCard";

import placeService from "../../services/placeService";

export default function Places() {
  const places = placeService.getAll();

  return (
    <Section
      title="Discover Places"
      subtitle="Explore destinations from every corner of India."
    >
      <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
        {places.map((place) => (
          <PlaceCard
            key={place.id}
            place={place}
          />
        ))}
      </div>
    </Section>
  );
}