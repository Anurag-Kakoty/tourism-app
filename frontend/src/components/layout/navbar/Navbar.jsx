import { useEffect, useState } from "react";
import { HiOutlineBars3, HiOutlineXMark } from "react-icons/hi2";

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
    };

    window.addEventListener("authChange", handleAuthChange);

    return () => {
      window.removeEventListener("authChange", handleAuthChange);
    };
  }, []);

  const handleLogout = () => {
    authService.logout();
    setUser(null);
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
            <>
              <span className="text-sm font-medium text-slate-700">
                Hi, {user.name}
              </span>

              <Button
                variant="outline"
                onClick={handleLogout}
              >
                Logout
              </Button>
            </>
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