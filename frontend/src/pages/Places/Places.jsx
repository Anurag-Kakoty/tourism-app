import { useEffect, useState } from "react";

import Section from "../../components/common/layout/Section";
import PlaceCard from "../../components/places/PlaceCard";

import placeService from "../../services/placeService";

export default function Places() {
  const [places, setPlaces] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    loadPlaces();
  }, []);

  async function loadPlaces() {
    try {
      setLoading(true);

      const data = await placeService.getAll();
      console.log(data);

      setPlaces(data);
    } catch (err) {
      console.error(err);
      setError("Unable to load attractions.");
    } finally {
      setLoading(false);
    }
  }

  if (loading) {
    return (
      <Section title="Places">
        <p>Loading attractions...</p>
      </Section>
    );
  }

  if (error) {
    return (
      <Section title="Places">
        <p className="text-red-600">{error}</p>
      </Section>
    );
  }

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