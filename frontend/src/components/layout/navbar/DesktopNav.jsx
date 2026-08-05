import { NavLink } from "react-router-dom";
import navigation from "../../../constants/navigation";

export default function DesktopNav() {
  return (
    <nav className="hidden lg:flex items-center gap-8">
      {navigation.map((item) => (
        <NavLink
          key={item.path}
          to={item.path}
          className={({ isActive }) =>
            `group relative text-sm font-medium transition-colors duration-300 ${
              isActive
                ? "text-[var(--color-primary)]"
                : "text-slate-600 hover:text-[var(--color-primary)]"
            }`
          }
        >
          {({ isActive }) => (
            <>
              {item.label}

              <span
                className={`
                  absolute
                  -bottom-2
                  left-0
                  h-0.5
                  rounded-full
                  bg-[var(--color-primary)]
                  transition-all
                  duration-300
                  ${
                    isActive
                      ? "w-full"
                      : "w-0 group-hover:w-full"
                  }
                `}
              />
            </>
          )}
        </NavLink>
      ))}
    </nav>
  );
}