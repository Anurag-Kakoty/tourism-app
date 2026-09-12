import { NavLink } from "react-router-dom";
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
        ${isOpen ? "max-h-96" : "max-h-0"}
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
          <>
            <div className="mt-4 rounded-lg bg-slate-50 px-4 py-3 text-center">
              <p className="text-sm text-slate-500">
                Signed in as
              </p>

              <p className="font-medium text-slate-700">
                {user.name}
              </p>
            </div>

            <Button
              variant="outline"
              className="mt-3 w-full"
              onClick={onLogout}
            >
              Logout
            </Button>
          </>
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