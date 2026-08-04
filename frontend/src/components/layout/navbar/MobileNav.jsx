import { NavLink } from "react-router-dom";
import Button from "../../common/inputs/Button";
import navigation from "../../../constants/navigation";

export default function MobileNav({ isOpen, onClose }) {
  if (!isOpen) return null;

  return (
    <div className="lg:hidden border-t border-slate-200 bg-white">
      <nav className="flex flex-col p-6">

        {navigation.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            onClick={onClose}
            className={({ isActive }) =>
              `rounded-lg px-4 py-3 font-medium transition-colors ${
                isActive
                  ? "bg-emerald-50 text-[var(--color-primary)]"
                  : "text-slate-700 hover:bg-slate-100"
              }`
            }
          >
            {item.label}
          </NavLink>
        ))}

        <Button
          to="/itinerary"
          className="mt-6 w-full"
        >
          Plan My Trip
        </Button>

      </nav>
    </div>
  );
}