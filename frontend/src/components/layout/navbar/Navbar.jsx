import { useEffect, useRef, useState } from "react";
import {
  HiOutlineBars3,
  HiOutlineXMark,
  HiOutlineUserCircle,
} from "react-icons/hi2";

import Logo from "./Logo";
import DesktopNav from "./DesktopNav";
import MobileNav from "./MobileNav";

import Button from "../../common/inputs/Button";
import Container from "../../common/layout/Container";
import authService from "../../../services/authService";

export default function Navbar() {
  const [isOpen, setIsOpen] = useState(false);
  const [scrolled, setScrolled] = useState(false);
  const [user, setUser] = useState(authService.getCurrentUser());
  const [accountOpen, setAccountOpen] = useState(false);

  const accountRef = useRef(null);

  useEffect(() => {
    const handleScroll = () => {
      setScrolled(window.scrollY > 10);
    };

    window.addEventListener("scroll", handleScroll);

    return () => window.removeEventListener("scroll", handleScroll);
  }, []);

  useEffect(() => {
    const handleAuthChange = () => {
      setUser(authService.getCurrentUser());
      setAccountOpen(false);
    };

    window.addEventListener("authChange", handleAuthChange);

    return () => window.removeEventListener("authChange", handleAuthChange);
  }, []);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (
        accountRef.current &&
        !accountRef.current.contains(event.target)
      ) {
        setAccountOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const handleLogout = () => {
    authService.logout();
    setUser(null);
    setAccountOpen(false);
    setIsOpen(false);
  };

  return (
    <header
      className={`
        sticky top-0 z-50
        border-b border-slate-200
        bg-white/90 backdrop-blur-md
        transition-shadow duration-300
        ${scrolled ? "shadow-md" : ""}
      `}
    >
      <Container className="flex h-20 items-center justify-between">
        <Logo />

        <DesktopNav />

        <div className="hidden items-center gap-3 lg:flex">
          <Button to="/itinerary">
            Plan My Trip
          </Button>

          {user ? (
            <div className="relative" ref={accountRef}>
              <button
                type="button"
                onClick={() => setAccountOpen(!accountOpen)}
                className="
                  flex
                  items-center
                  justify-center
                  rounded-full
                  p-1
                  text-slate-600
                  transition-colors
                  hover:bg-slate-100
                  hover:text-[var(--color-primary)]
                "
                aria-label="Open account menu"
                aria-expanded={accountOpen}
              >
                <HiOutlineUserCircle size={38} />
              </button>

              {accountOpen && (
                <div
                  className="
                    absolute
                    right-0
                    top-full
                    mt-3
                    w-64
                    overflow-hidden
                    rounded-xl
                    border
                    border-[var(--color-border)]
                    bg-white
                    shadow-lg
                  "
                >
                  <div className="border-b border-slate-200 px-4 py-4">
                    <p className="font-semibold text-[var(--color-text)]">
                      {user.name}
                    </p>

                    <p className="mt-1 truncate text-sm text-slate-500">
                      {user.email}
                    </p>
                  </div>

                  <div className="p-2">
                    <button
                      type="button"
                      onClick={handleLogout}
                      className="
                        w-full
                        rounded-lg
                        px-3
                        py-2.5
                        text-left
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
                </div>
              )}
            </div>
          ) : (
            <Button
              to="/login"
              variant="outline"
            >
              Login
            </Button>
          )}
        </div>

        <button
          onClick={() => setIsOpen(!isOpen)}
          className="rounded-lg p-2 transition-colors hover:bg-slate-100 lg:hidden"
          aria-label={isOpen ? "Close menu" : "Open menu"}
        >
          {isOpen ? (
            <HiOutlineXMark size={28} />
          ) : (
            <HiOutlineBars3 size={28} />
          )}
        </button>
      </Container>

      <MobileNav
        isOpen={isOpen}
        onClose={() => setIsOpen(false)}
        user={user}
        onLogout={handleLogout}
      />
    </header>
  );
}