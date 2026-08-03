import { Link } from "react-router-dom";
import Container from "./Container";

export default function Section({
  title,
  subtitle,
  children,
  className = "",
  action,
}) {
  return (
    <section className={`py-20 ${className}`}>
      <Container>
        <div className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div>
            {title && (
              <h2 className="text-3xl font-bold text-slate-900">
                {title}
              </h2>
            )}

            {subtitle && (
              <p className="mt-3 max-w-2xl text-slate-600">
                {subtitle}
              </p>
            )}
          </div>

          {action && (
            <Link
              to={action.to}
              className="font-semibold text-[var(--color-primary)] transition-colors hover:underline"
            >
              {action.label} →
            </Link>
          )}
        </div>

        <div className="mt-10">
          {children}
        </div>
      </Container>
    </section>
  );
}