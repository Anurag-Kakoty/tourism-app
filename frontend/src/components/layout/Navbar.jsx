import { Link, NavLink } from "react-router-dom";

const navLinks = [
  { name: "Home", path: "/" },
  { name: "Places", path: "/places" },
  { name: "Experiences", path: "/experiences" },
  { name: "Festivals", path: "/festivals" },
  { name: "Stay", path: "/stay" },
  { name: "Guides", path: "/guides" },
  { name: "Transport", path: "/transport" },
  { name: "Plan Trip", path: "/itinerary" },
];

export default function Navbar() {
  return (
    <header className="sticky top-0 z-50 bg-white shadow-md">
      <nav className="mx-auto flex h-[72px] max-w-7xl items-center justify-between px-6">
        <Link
          to="/"
          className="text-2xl font-bold text-[var(--color-primary)]"
        >
          Incredible India
        </Link>

        <div className="flex items-center gap-6">
          {navLinks.map((link) => (
            <NavLink
              key={link.path}
              to={link.path}
              className={({ isActive }) =>
                `font-medium transition-colors ${
                  isActive
                    ? "text-[var(--color-primary)]"
                    : "text-slate-600 hover:text-[var(--color-primary)]"
                }`
              }
            >
              {link.name}
            </NavLink>
          ))}
        </div>
      </nav>
    </header>
  );
}