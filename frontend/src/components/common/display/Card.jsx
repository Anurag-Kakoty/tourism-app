export default function Card({
  children,
  className = "",
}) {
  return (
    <div
      className={`
        overflow-hidden
        rounded-2xl
        bg-white
        shadow-md
        transition-all
        duration-300
        hover:-translate-y-2
        hover:shadow-2xl
        ${className}
      `}
    >
      {children}
    </div>
  );
}