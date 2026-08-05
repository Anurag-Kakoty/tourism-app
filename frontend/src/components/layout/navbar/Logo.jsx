import { Link } from "react-router-dom";
import { FaCompass } from "react-icons/fa";

export default function Logo() {
  return (
    <Link
      to="/"
      className="flex items-center gap-3 transition-opacity hover:opacity-90"
    >
      <div className="flex h-12 w-12 items-center justify-center rounded-full bg-[var(--color-primary)] text-xl text-white shadow-md">
        <FaCompass />
      </div>

      <div className="hidden sm:block">
        <h1 className="text-xl font-bold text-slate-900">
          Incredible India
        </h1>

        <p className="text-xs text-slate-500">
          Discover • Explore • Experience
        </p>
      </div>
    </Link>
  );
}