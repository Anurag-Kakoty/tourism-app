import { NavLink } from "react-router-dom";
import {
  HiOutlineUserCircle,
} from "react-icons/hi2";

import Button from "../../common/inputs/Button";
import navigation from "../../../constants/navigation";

export default function MobileNav({
  isOpen,
  onClose,
  user,
  onLogout,
}) {
  if (!isOpen) return null;

  return (
    <div
      className={`
        lg:hidden
        overflow-hidden
        border-t
        border-slate-200
        bg-white
        transition-all
        duration-300
        ${isOpen ? "max-h-[calc(100vh-5rem)]" : "max-h-0"}
      `}
    >
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
          onClick={onClose}
        >
          Plan My Trip
        </Button>

        {user ? (
          <div className="mt-4 border-t border-slate-200 pt-4">
            <div className="flex items-center gap-3 rounded-lg bg-slate-50 px-4 py-3">
              <HiOutlineUserCircle
                size={36}
                className="shrink-0 text-slate-600"
              />

              <div className="min-w-0">
                <p className="truncate font-medium text-[var(--color-text)]">
                  {user.name}
                </p>

                <p className="truncate text-sm text-slate-500">
                  {user.email}
                </p>
              </div>
            </div>

            <button
              type="button"
              onClick={onLogout}
              className="
                mt-3
                w-full
                rounded-lg
                border
                border-slate-200
                px-4
                py-3
                text-sm
                font-medium
                text-slate-700
                transition-colors
                hover:bg-slate-100
              "
            >
              Logout
            </button>
          </div>
        ) : (
          <Button
            to="/login"
            variant="outline"
            className="mt-3 w-full"
            onClick={onClose}
          >
            Login
          </Button>
        )}

      </nav>
    </div>
  );
}