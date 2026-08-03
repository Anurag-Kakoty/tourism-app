export default function Footer() {
  return (
    <footer className="border-t border-[var(--color-border)] bg-white">
      <div className="mx-auto flex max-w-7xl flex-col items-center justify-between gap-4 px-6 py-8 md:flex-row">
        <div>
          <h3 className="text-lg font-semibold text-[var(--color-primary)]">
            Tourism Discovery Platform
          </h3>

          <p className="text-sm text-slate-500">
            Discover destinations, festivals and experiences across India.
          </p>
        </div>

        <p className="text-sm text-slate-500">
          © {new Date().getFullYear()} Tourism Discovery Platform
        </p>
      </div>
    </footer>
  );
}