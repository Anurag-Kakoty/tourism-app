export default function Button({
  children,
  onClick,
  variant = "primary",
  type = "button",
  className = "",
}) {

  const variants = {
    primary:
      "bg-[var(--color-primary)] text-white hover:bg-[var(--color-primary-hover)]",

    secondary:
      "bg-[var(--color-secondary)] text-white hover:bg-[var(--color-secondary-hover)]",

    outline:
      "border border-[var(--color-primary)] text-[var(--color-primary)] hover:bg-[var(--color-primary)] hover:text-white",
  };

  return (
    <button
      type={type}
      onClick={onClick}
      className={`
        rounded-lg
        px-5
        py-3
        font-medium
        transition
        duration-200
        cursor-pointer
        ${variants[variant]}
        ${className}
      `}
    >
      {children}
    </button>
  );
}