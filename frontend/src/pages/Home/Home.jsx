import Section from "../../components/common/layout/Section";
import Card from "../../components/common/display/Card";
import Button from "../../components/common/inputs/Button";

export default function Home() {
  return (
    <>
      <Section
        title="Discover Incredible India"
        subtitle="Find destinations, festivals, experiences and unforgettable journeys."
      >
        <Card className="p-8">

          <h3 className="text-2xl font-semibold">
            Welcome!
          </h3>

          <p className="mt-4 text-slate-600">
            This is our first reusable card.
          </p>

          <div className="mt-6 flex gap-4">

            <Button>
              Explore
            </Button>

            <Button variant="outline">
              Learn More
            </Button>

          </div>

        </Card>
      </Section>
    </>
  );
}