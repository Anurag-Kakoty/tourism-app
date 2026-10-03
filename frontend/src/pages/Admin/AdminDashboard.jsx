import {
  HiOutlineMap,
  HiOutlineGlobeAlt,
  HiOutlineSparkles,
  HiOutlineCalendarDays,
  HiOutlineBuildingOffice2,
  HiOutlineUserGroup,
  HiOutlineTruck,
  HiOutlineHomeModern,
} from "react-icons/hi2";

const managementSections = [
  {
    title: "States",
    description: "Manage Indian states and their information.",
    icon: HiOutlineMap,
    path: "/states",
  },
  {
    title: "Destinations",
    description: "Manage tourism destinations across India.",
    icon: HiOutlineGlobeAlt,
    path: "/destinations",
  },
  {
    title: "Attractions",
    description: "Manage places and tourist attractions.",
    icon: HiOutlineSparkles,
    path: "/places",
  },
  {
    title: "Festivals",
    description: "Manage festivals and festival occurrences.",
    icon: HiOutlineCalendarDays,
    path: "/festivals",
  },
  {
    title: "Accommodations",
    description: "Manage hotels, stays, and homestays.",
    icon: HiOutlineBuildingOffice2,
    path: "/stay",
  },
  {
    title: "Guides",
    description: "Manage registered tour guides.",
    icon: HiOutlineUserGroup,
    path: "/guides",
  },
  {
    title: "Transport",
    description: "Manage transport providers and services.",
    icon: HiOutlineTruck,
    path: "/transport",
  },
];

export default function AdminDashboard() {
  return (
    <main className="min-h-screen bg-slate-50">
      <section className="border-b border-slate-200 bg-white">
        <div className="mx-auto max-w-7xl px-6 py-12">
          <p className="text-sm font-semibold uppercase tracking-wide text-[var(--color-primary)]">
            Administration
          </p>

          <h1 className="mt-2 text-3xl font-bold text-[var(--color-text)] md:text-4xl">
            Admin Dashboard
          </h1>

          <p className="mt-3 max-w-2xl text-slate-600">
            Manage the tourism information used throughout the platform.
          </p>
        </div>
      </section>

      <section className="mx-auto max-w-7xl px-6 py-10">
        <div className="grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
          {managementSections.map((section) => {
            const Icon = section.icon;

            return (
              <a
                key={section.title}
                href={section.path}
                className="
                  group
                  rounded-2xl
                  border
                  border-slate-200
                  bg-white
                  p-6
                  shadow-sm
                  transition-all
                  duration-200
                  hover:-translate-y-1
                  hover:shadow-md
                "
              >
                <div
                  className="
                    flex
                    h-12
                    w-12
                    items-center
                    justify-center
                    rounded-xl
                    bg-emerald-50
                    text-[var(--color-primary)]
                    transition-colors
                    group-hover:bg-emerald-100
                  "
                >
                  <Icon size={25} />
                </div>

                <h2 className="mt-5 text-lg font-semibold text-[var(--color-text)]">
                  {section.title}
                </h2>

                <p className="mt-2 text-sm leading-6 text-slate-500">
                  {section.description}
                </p>

                <span className="mt-5 inline-block text-sm font-semibold text-[var(--color-primary)]">
                  Manage →
                </span>
              </a>
            );
          })}
        </div>
      </section>
    </main>
  );
}