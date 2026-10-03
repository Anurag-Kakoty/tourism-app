import { Navigate, Outlet, useLocation } from "react-router-dom";

import authService from "../services/authService";

export default function AdminRoute() {
  const user = authService.getCurrentUser();
  const location = useLocation();

  if (!user) {
    return (
      <Navigate
        to="/login"
        replace
        state={{ from: location }}
      />
    );
  }

  if (user.role !== "ADMIN") {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}