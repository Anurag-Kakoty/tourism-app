export default function Badge({
  children,
  variant = "primary",
  className = "",
}) {
  const variants = {
    primary:
      "bg-emerald-100 text-emerald-700",

    secondary:
      "bg-sky-100 text-sky-700",

    accent:
      "bg-orange-100 text-orange-700",

    neutral:
      "bg-slate-100 text-slate-700",
  };

  return (
    <span
      className={`
        inline-flex
        items-center
        rounded-full
        px-3
        py-1
        text-sm
        font-medium
        ${variants[variant] || variants.primary}
        ${className}
      `}
    >
      {children}
    </span>
  );
}