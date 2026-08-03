export default function Card({
  children,
  className = "",
}) {
  return (
    <div
      className={`
        rounded-xl
        bg-white
        shadow-md
        transition-all
        duration-300
        hover:-translate-y-1
        hover:shadow-xl
        ${className}
      `}
    >
      {children}
    </div>
  );
}