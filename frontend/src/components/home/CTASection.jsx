import Button from "../common/inputs/Button";
import Container from "../common/layout/Container";

export default function CTASection() {
  return (
    <section className="bg-[var(--color-primary)] py-20">

      <Container>

        <div className="text-center">

          <h2 className="text-4xl font-bold text-white">
            Ready to Explore India?
          </h2>

          <p className="mx-auto mt-6 max-w-2xl text-lg text-emerald-100">
            Start discovering destinations, festivals and unforgettable experiences today.
          </p>

          <Button
            to="/places"
            className="mt-10 bg-white text-[var(--color-primary)] hover:bg-slate-100"
          >
            Explore Destinations
          </Button>

        </div>

      </Container>

    </section>
  );
}