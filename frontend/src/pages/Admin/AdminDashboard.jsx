import {
  HiOutlineMap,
  HiOutlineGlobeAlt,
  HiOutlineSparkles,
  HiOutlineCalendarDays,
  HiOutlineBuildingOffice2,
  HiOutlineUserGroup,
  HiOutlineTruck,
} from "react-icons/hi2";
import { Link } from "react-router-dom";

import Container from "../../components/common/layout/Container";
import Card from "../../components/common/display/Card";
import AdminPageHeader from "../../components/admin/AdminPageHeader";

const managementSections = [
  {
    title: "States",
    description:
      "Manage Indian states and their basic information.",
    icon: HiOutlineMap,
    path: "/admin/states",
  },
  {
    title: "Destinations",
    description:
      "Manage tourism destinations across India.",
    icon: HiOutlineGlobeAlt,
    path: "/admin/destinations",
  },
  {
    title: "Attractions",
    description:
      "Manage tourist attractions and places to visit.",
    icon: HiOutlineSparkles,
    path: "/admin/attractions",
  },
  {
    title: "Festivals",
    description:
      "Manage festivals and festival information.",
    icon: HiOutlineCalendarDays,
    path: "/admin/festivals",
  },
  {
    title: "Accommodations",
    description:
      "Manage hotels, homestays, and other stays.",
    icon: HiOutlineBuildingOffice2,
    path: "/admin/accommodations",
  },
  {
    title: "Guides",
    description:
      "Manage registered tour guides.",
    icon: HiOutlineUserGroup,
    path: "/admin/guides",
  },
  {
    title: "Transport",
    description:
      "Manage transport services and providers.",
    icon: HiOutlineTruck,
    path: "/admin/transport",
  },
];

export default function AdminDashboard() {
  return (
    <main className="min-h-screen bg-slate-50">
      <AdminPageHeader
        title="Admin Dashboard"
        description="Manage the tourism information and content used throughout the platform."
      />

      <section className="py-10">
        <Container>
          <div>
            <h2 className="text-2xl font-bold text-[var(--color-text)]">
              Content Management
            </h2>

            <p className="mt-2 text-slate-600">
              Select a section to manage its information.
            </p>
          </div>

          <div className="mt-8 grid gap-6 sm:grid-cols-2 lg:grid-cols-3">
            {managementSections.map((section) => {
              const Icon = section.icon;

              return (
                <Link
                  key={section.title}
                  to={section.path}
                  className="group"
                >
                  <Card className="h-full p-6">
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

                    <h3 className="mt-5 text-lg font-semibold text-[var(--color-text)]">
                      {section.title}
                    </h3>

                    <p className="mt-2 text-sm leading-6 text-slate-500">
                      {section.description}
                    </p>

                    <span className="mt-5 inline-block text-sm font-semibold text-[var(--color-primary)]">
                      Manage →
                    </span>
                  </Card>
                </Link>
              );
            })}
          </div>
        </Container>
      </section>
    </main>
  );
}