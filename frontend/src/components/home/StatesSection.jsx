import Section from "../common/layout/Section";
import StateCard from "../states/StateCard";
import states from "../../data/home/states";

export default function StatesSection() {
  return (
    <Section
        title="Explore by State"
        subtitle="Each state has its own culture, festivals, cuisine and unforgettable destinations."
        action={{
            label: "View All",
            to: "/places",
        }}
    >
      <div className="grid gap-8 md:grid-cols-2 xl:grid-cols-4">
        {states.map((state) => (
          <StateCard
            key={state.id}
            state={state}
          />
        ))}
      </div>
    </Section>
  );
}