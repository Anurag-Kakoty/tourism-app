import Button from "../common/inputs/Button";
import Container from "../common/layout/Container";

export default function Hero() {
  return (
    <section className="bg-gradient-to-r from-emerald-700 to-sky-700 py-24">
      <Container>
        <div className="max-w-3xl">

          <p className="mb-4 text-sm font-semibold uppercase tracking-[0.3em] text-emerald-100">
            Tourism Discovery Platform
          </p>

          <h1 className="text-5xl font-bold leading-tight text-white md:text-6xl">
            Discover Incredible India
          </h1>

          <p className="mt-6 text-lg leading-8 text-slate-100">
            Explore destinations, festivals, culture, and unforgettable
            experiences from every corner of India.
          </p>

          <div className="mt-10 flex gap-4">
            <Button>
              Explore Destinations
            </Button>

            <Button variant="outline">
              Learn More
            </Button>
          </div>

        </div>
      </Container>
    </section>
  );
}