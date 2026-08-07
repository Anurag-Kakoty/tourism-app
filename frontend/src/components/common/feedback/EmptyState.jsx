export default function EmptyState({
  message = "Nothing to display."
}) {
  return (
    <div className="rounded-xl border border-dashed border-slate-300 p-12 text-center">
      <p className="text-slate-500">
        {message}
      </p>
    </div>
  );
}