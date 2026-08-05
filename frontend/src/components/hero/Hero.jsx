import { useEffect, useState } from "react";

import Button from "../common/inputs/Button";
import Container from "../common/layout/Container";

import livingRootBridge from "../../assets/images/hero/living-root-bridge.jpg";
import kaziranga from "../../assets/images/hero/kaziranga.jpg";
import hampi from "../../assets/images/hero/hampi.jpg";
import alleppey from "../../assets/images/hero/alleppey.jpg";

const slides = [
  {
    title: "Discover Incredible India",
    subtitle: "Living Root Bridge • Meghalaya",
    description:
      "Explore destinations, festivals, culture and unforgettable experiences across India.",
    image: livingRootBridge,
    button: "Explore Meghalaya",
  },
  {
    title: "Experience Incredible Wildlife",
    subtitle: "Kaziranga National Park • Assam",
    description:
      "Witness India's rich biodiversity and breathtaking landscapes.",
    image: kaziranga,
    button: "Explore Assam",
  },
  {
    title: "Walk Through History",
    subtitle: "Hampi • Karnataka",
    description:
      "Explore centuries of history through India's magnificent heritage sites.",
    image: hampi,
    button: "Explore Karnataka",
  },
  {
    title: "Relax in Nature",
    subtitle: "Alleppey • Kerala",
    description:
      "Cruise through Kerala's beautiful backwaters and unwind in nature.",
    image: alleppey,
    button: "Explore Kerala",
  },
];

export default function Hero() {
  const [currentSlide, setCurrentSlide] = useState(0);

  useEffect(() => {
    const timer = setInterval(() => {
      setCurrentSlide((prev) => (prev + 1) % slides.length);
    }, 6000);

    return () => clearInterval(timer);
  }, []);

  const slide = slides[currentSlide];

  return (
    <section
      className="relative min-h-[80vh] bg-cover bg-center transition-all duration-700"
      style={{
        backgroundImage: `url(${slide.image})`,
      }}
    >
      <div className="absolute inset-0 bg-black/50"></div>

      <Container className="relative flex min-h-[80vh] items-center">
        <div className="max-w-3xl">
          <p className="mb-4 text-sm font-semibold uppercase tracking-[0.3em] text-emerald-200">
            {slide.subtitle}
          </p>

          <h1 className="text-4xl font-bold leading-tight text-white sm:text-5xl lg:text-6xl">
            {slide.title}
          </h1>

          <p className="mt-6 text-lg leading-8 text-slate-100">
            {slide.description}
          </p>

          <div className="mt-10 flex flex-col gap-4 sm:flex-row">
            <Button to="/places">
              {slide.button}
            </Button>

            <Button to="/itinerary" variant="outlineLight">
              Plan My Trip
            </Button>
          </div>

          <div className="mt-10 flex gap-3">
            {slides.map((_, index) => (
              <button
                key={index}
                onClick={() => setCurrentSlide(index)}
                className={`h-3 w-3 rounded-full transition-all ${
                  currentSlide === index
                    ? "bg-white"
                    : "bg-white/50"
                }`}
                aria-label={`Go to slide ${index + 1}`}
              />
            ))}
          </div>
        </div>
      </Container>
    </section>
  );
}