import Container from "./Container";

export default function Section({
  title,
  subtitle,
  children,
  className = "",
}) {
  return (
    <section className={`py-16 ${className}`}>
      <Container>

        {title && (
          <h2 className="text-3xl font-bold text-slate-900">
            {title}
          </h2>
        )}

        {subtitle && (
          <p className="mt-3 text-slate-600 max-w-2xl">
            {subtitle}
          </p>
        )}

        <div className="mt-10">
          {children}
        </div>

      </Container>
    </section>
  );
}