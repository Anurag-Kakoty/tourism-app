export default function LoadingSpinner({
  message = "Loading..."
}) {
  return (
    <div className="flex flex-col items-center justify-center py-20">
      <div className="h-10 w-10 animate-spin rounded-full border-4 border-[var(--color-border)] border-t-[var(--color-primary)]"></div>

      <p className="mt-4 text-[var(--color-text-light)]">
        {message}
      </p>
    </div>
  );
}