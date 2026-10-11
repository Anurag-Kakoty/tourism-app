import Navbar from "./navbar/Navbar";
import Footer from "./Footer";
import OnboardingModal from "../preferences/OnboardingModal";

export default function Layout({ children }) {
  return (
    <div className="min-h-screen flex flex-col bg-[var(--color-background)]">
      <Navbar />
      <OnboardingModal />

      <main className="flex-1">
        {children}
      </main>

      <Footer />
    </div>
  );
}