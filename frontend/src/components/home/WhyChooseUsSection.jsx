import Section from "../common/layout/Section";

const features = [
  {
    title: "Discover Hidden Gems",
    description:
      "Explore destinations beyond the usual tourist spots.",
    icon: "🧭",
  },
  {
    title: "Festival Calendar",
    description:
      "Plan your trip around India's cultural celebrations.",
    icon: "🎊",
  },
  {
    title: "Trip Planning",
    description:
      "Organize destinations, transport and stays in one place.",
    icon: "🧳",
  },
  {
    title: "AI Recommendations",
    description:
      "Receive personalized travel suggestions based on your interests.",
    icon: "🤖",
  },
];

export default function WhyChooseUsSection() {
  return (
    <Section
      title="Why Choose Our Platform?"
      subtitle="Everything you need to discover and plan memorable journeys across India."
    >
      <div className="grid gap-8 md:grid-cols-2 xl:grid-cols-4">

        {features.map((feature) => (
          <div
            key={feature.title}
            className="rounded-xl bg-white p-6 text-center shadow-md"
          >
            <div className="text-5xl">
              {feature.icon}
            </div>

            <h3 className="mt-5 text-xl font-bold">
              {feature.title}
            </h3>

            <p className="mt-3 text-slate-600">
              {feature.description}
            </p>
          </div>
        ))}

      </div>
    </Section>
  );
}