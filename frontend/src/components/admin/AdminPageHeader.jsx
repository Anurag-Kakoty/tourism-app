import Container from "../common/layout/Container";
import Button from "../common/inputs/Button";

export default function AdminPageHeader({
  eyebrow = "Administration",
  title,
  description,
  action,
}) {
  return (
    <section className="border-b border-slate-200 bg-white">
      <Container>
        <div className="flex flex-col gap-5 py-10 sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p className="text-sm font-semibold uppercase tracking-wide text-[var(--color-primary)]">
              {eyebrow}
            </p>

            <h1 className="mt-2 text-3xl font-bold text-[var(--color-text)]">
              {title}
            </h1>

            {description && (
              <p className="mt-2 max-w-2xl text-slate-600">
                {description}
              </p>
            )}
          </div>

          {action && (
            <Button
              type="button"
              onClick={action.onClick}
              disabled={action.disabled}
              className="shrink-0"
            >
              {action.icon}

              {action.label}
            </Button>
          )}
        </div>
      </Container>
    </section>
  );
}