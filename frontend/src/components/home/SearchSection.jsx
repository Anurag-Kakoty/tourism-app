import Section from "../common/layout/Section";
import Button from "../common/inputs/Button";

export default function SearchSection() {
  return (
    <Section
      title="Where would you like to explore?"
      subtitle="Search destinations, festivals, or experiences across India."
    >
      <div className="rounded-2xl bg-white p-6 shadow-md">

        <div className="grid gap-4 md:grid-cols-[1fr_auto]">

          <input
            type="text"
            placeholder="Search destinations..."
            className="rounded-lg border border-slate-300 px-4 py-3 outline-none focus:border-emerald-600"
          />

          <Button>
            Search
          </Button>

        </div>

      </div>
    </Section>
  );
}