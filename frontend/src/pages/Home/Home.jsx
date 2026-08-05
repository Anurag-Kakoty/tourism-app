import Hero from "../../components/hero/Hero";
import SearchSection from "../../components/home/SearchSection";
import StatesSection from "../../components/home/StatesSection";
import FeaturedDestinations from "../../components/home/FeaturedDestinations";
import ExperiencesSection from "../../components/home/ExperiencesSection";
import FestivalsSection from "../../components/home/FestivalsSection";
import WhyChooseUsSection from "../../components/home/WhyChooseUsSection";
import CTASection from "../../components/home/CTASection";

export default function Home() {
  return (
    <>
      <Hero />

      <SearchSection />

      <StatesSection />

      <FeaturedDestinations />

      <ExperiencesSection />

      <FestivalsSection />

      <WhyChooseUsSection />

      <CTASection />
    </>
  );
}