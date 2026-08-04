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
    <section className={`py-14 lg:py-20 ${className}`}>
      <Container>
        <div className="flex flex-col gap-4 md:flex-row md:items-end md:justify-between">
          <div>
            {title && (
              <h2 className="text-2xl font-bold text-slate-900 sm:text-3xl">
                {title}
              </h2>
            )}

            {subtitle && (
              <p className="mt-3 max-w-2xl leading-7 text-slate-600">
                {subtitle}
              </p>
            )}
          </div>

          {action && (
            <Link
              to={action.to}
              className="inline-flex items-center gap-1 font-semibold text-[var(--color-primary)] transition-all duration-300 hover:underline"
            >
              {action.label}
              <span>→</span>
            </Link>
          )}
        </div>

        <div className="mt-12">
          {children}
        </div>
      </Container>
    </section>
  );
}