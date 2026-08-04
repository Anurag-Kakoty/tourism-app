import Button from "../common/inputs/Button";
import Container from "../common/layout/Container";

export default function Hero() {
  return (
    <section className="bg-gradient-to-r from-emerald-700 to-sky-700 py-16 lg:py-24">
      <Container>
        <div className="grid items-center gap-12 lg:grid-cols-2">

          {/* Left */}

          <div>

            <p className="mb-4 text-sm font-semibold uppercase tracking-[0.3em] text-emerald-100">
              Tourism Discovery Platform
            </p>

            <h1 className="text-4xl font-bold leading-tight text-white sm:text-5xl lg:text-6xl">
              Discover Incredible India
            </h1>

            <p className="mt-6 text-lg leading-8 text-slate-100">
              Explore destinations, festivals, culture and unforgettable experiences across India.
            </p>

            <div className="mt-10 flex flex-col gap-4 sm:flex-row">

              <Button to="/places">
                Explore Destinations
              </Button>

              <Button
                to="/experiences"
                variant="outline"
              >
                Explore Experiences
              </Button>

            </div>

          </div>

          {/* Right */}

          <div className="hidden lg:flex justify-center">

            <div className="h-96 w-full rounded-3xl bg-white/10 backdrop-blur-md border border-white/20 flex items-center justify-center">

              <span className="text-7xl">
                🇮🇳
              </span>

            </div>

          </div>

        </div>
      </Container>
    </section>
  );
}