import { Link } from "react-router-dom";

export default function Button({
  children,
  variant = "primary",
  type = "button",
  onClick,
  to,
  disabled = false,
  className = "",
}) {
  const baseClasses =
    "inline-flex items-center justify-center rounded-xl px-5 py-3 font-medium transition-all duration-300 focus:outline-none focus:ring-2 focus:ring-[var(--color-primary)] focus:ring-offset-2";

  const variants = {
    primary:
      "bg-[var(--color-primary)] text-white hover:bg-[var(--color-primary-hover)]",

    secondary:
      "bg-[var(--color-secondary)] text-white hover:bg-[var(--color-secondary-hover)]",

    outline:
      "border border-[var(--color-primary)] text-[var(--color-primary)] hover:bg-[var(--color-primary)] hover:text-white",

    outlineLight:
      "bg-white text-slate-900 border border-white hover:bg-slate-100 hover:border-slate-100",
  };

  const classes = `
    ${baseClasses}
    ${variants[variant] || variants.primary}
    ${disabled ? "cursor-not-allowed opacity-50" : ""}
    ${className}
  `;

  if (to) {
    return (
      <Link to={to} className={classes}>
        {children}
      </Link>
    );


  }

  return (
    <button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={classes}
    >
      {children}
    </button>
  );
}