import { Link } from "react-router-dom";

export default function Button({
  children,
  variant = "primary",
  type = "button",
  onClick,
  to,
  className = "",
}) {
  const baseClasses =
    "inline-flex items-center justify-center rounded-lg px-5 py-3 font-medium transition-all duration-200";

  const variants = {
    primary:
      "bg-[var(--color-primary)] text-white hover:bg-[var(--color-primary-hover)]",

    secondary:
      "bg-[var(--color-secondary)] text-white hover:bg-[var(--color-secondary-hover)]",

    outline:
      "border border-[var(--color-primary)] text-[var(--color-primary)] hover:bg-[var(--color-primary)] hover:text-white",
  };

  const classes = `${baseClasses} ${variants[variant]} ${className}`;

  if (to) {
    return (
      <Link to={to} className={classes}>
        {children}
      </Link>
    );
  }

  return (
    <button type={type} onClick={onClick} className={classes}>
      {children}
    </button>
  );
}