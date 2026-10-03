import { HiOutlineXMark } from "react-icons/hi2";

export default function AdminModal({
  isOpen,
  onClose,
  title,
  description,
  children,
  size = "lg",
}) {
  if (!isOpen) {
    return null;
  }

  const sizes = {
    sm: "max-w-md",
    md: "max-w-xl",
    lg: "max-w-2xl",
    xl: "max-w-4xl",
  };

  return (
    <div
      className="
        fixed
        inset-0
        z-[60]
        flex
        items-center
        justify-center
        bg-slate-900/50
        p-4
        backdrop-blur-sm
      "
      onMouseDown={onClose}
    >
      <div
        className={`
          ${sizes[size] || sizes.lg}
          max-h-[90vh]
          w-full
          overflow-y-auto
          rounded-2xl
          bg-white
          shadow-2xl
        `}
        onMouseDown={(event) => event.stopPropagation()}
      >
        <div className="flex items-start justify-between border-b border-slate-200 px-6 py-5">
          <div>
            <h2 className="text-xl font-semibold text-[var(--color-text)]">
              {title}
            </h2>

            {description && (
              <p className="mt-1 text-sm text-slate-500">
                {description}
              </p>
            )}
          </div>

          <button
            type="button"
            onClick={onClose}
            className="
              rounded-lg
              p-2
              text-slate-500
              transition-colors
              hover:bg-slate-100
              hover:text-slate-700
            "
            aria-label="Close dialog"
          >
            <HiOutlineXMark size={24} />
          </button>
        </div>

        <div className="p-6">
          {children}
        </div>
      </div>
    </div>
  );
}