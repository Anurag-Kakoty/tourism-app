import { useState } from "react";
import { HiOutlineBars3, HiOutlineXMark } from "react-icons/hi2";

import Logo from "./Logo";
import DesktopNav from "./DesktopNav";
import MobileNav from "./MobileNav";

import Button from "../../common/inputs/Button";
import Container from "../../common/layout/Container";

export default function Navbar() {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <header className="sticky top-0 z-50 border-b border-slate-200 bg-white/90 backdrop-blur-md">
      <Container className="flex h-20 items-center justify-between">
        <Logo />

        <DesktopNav />

        <div className="hidden lg:block">
          <Button to="/itinerary">
            Plan My Trip
          </Button>
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
      />
    </header>
  );
}