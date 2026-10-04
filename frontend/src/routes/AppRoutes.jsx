import { Routes, Route } from "react-router-dom";

import Login from "../pages/Auth/Login/Login";
import Register from "../pages/Auth/Register/Register";
import Home from "../pages/Home/Home";
import Places from "../pages/Places/Places";
import PlaceDetails from "../pages/Places/PlaceDetails";
import Experiences from "../pages/Experiences/Experiences";
import ExperienceDetails from "../pages/experiences/ExperienceDetails";
import Festivals from "../pages/Festivals/Festivals";
import FestivalDetails from "../pages/Festivals/FestivalDetails";
import FestivalCalendar from "../pages/Festivals/FestivalCalendar";
import Stay from "../pages/Stay/Stay";
import StayDetails from "../pages/Stay/StayDetails";
import Guides from "../pages/Guides/Guides";
import GuideDetails from "../pages/Guides/GuideDetails";
import Transport from "../pages/Transport/Transport";
import TransportDetails from "../pages/Transport/TransportDetails";
import Itinerary from "../pages/Itinerary/Itinerary";
import NotFound from "../pages/NotFound/NotFound";
import States from "../pages/States/States";
import StateDetails from "../pages/States/StateDetails";
import Destinations from "../pages/Destinations/Destinations";
import DestinationDetails from "../pages/Destinations/DestinationDetails";
import MyItineraries from "../pages/MyItineraries/MyItineraries";

import AdminDashboard from "../pages/Admin/AdminDashboard";
import StatesAdmin from "../pages/Admin/StatesAdmin";
import DestinationsAdmin from "../pages/Admin/DestinationsAdmin";
import AttractionsAdmin from "../pages/Admin/AttractionsAdmin";
import FestivalsAdmin from "../pages/Admin/FestivalsAdmin";

import AdminRoute from "./AdminRoute";

export default function AppRoutes() {
  return (
    <Routes>
      <Route path="/" element={<Home />} />

      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />

      <Route path="/places" element={<Places />} />
      <Route path="/places/:id" element={<PlaceDetails />} />

      <Route path="/states" element={<States />} />
      <Route path="/states/:id" element={<StateDetails />} />

      <Route path="/destinations" element={<Destinations />} />
      <Route
        path="/destinations/:id"
        element={<DestinationDetails />}
      />

      <Route path="/experiences" element={<Experiences />} />
      <Route
        path="/experiences/:id"
        element={<ExperienceDetails />}
      />

      <Route path="/festivals" element={<Festivals />} />
      <Route
        path="/festivals/:id"
        element={<FestivalDetails />}
      />

      <Route
        path="/festival-calendar"
        element={<FestivalCalendar />}
      />

      <Route path="/stay" element={<Stay />} />
      <Route path="/stay/:id" element={<StayDetails />} />

      <Route path="/guides" element={<Guides />} />
      <Route path="/guides/:id" element={<GuideDetails />} />

      <Route path="/transport" element={<Transport />} />
      <Route
        path="/transport/:id"
        element={<TransportDetails />}
      />

      <Route path="/itinerary" element={<Itinerary />} />
      <Route
        path="/itinerary/:id/edit"
        element={<Itinerary />}
      />

      <Route
        path="/my-itineraries"
        element={<MyItineraries />}
      />

      {/* Protected admin routes */}
      <Route element={<AdminRoute />}>
        <Route
          path="/admin"
          element={<AdminDashboard />}
        />

        <Route
          path="/admin/states"
          element={<StatesAdmin />}
        />

        <Route
          path="/admin/destinations"
          element={<DestinationsAdmin />}
        />

        <Route
          path="/admin/attractions"
          element={<AttractionsAdmin />}
        />

        <Route
          path="/admin/festivals"
          element={<FestivalsAdmin />}
        />

      </Route>

      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}