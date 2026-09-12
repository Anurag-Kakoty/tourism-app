import api from "./api";

const login = async (credentials) => {
  const response = await api.post("/auth/login", credentials);

  const {
    accessToken,
    tokenType,
    userId,
    name,
    email,
    role,
  } = response.data;

  localStorage.setItem("accessToken", accessToken);
  localStorage.setItem("tokenType", tokenType);
  localStorage.setItem("userId", userId);
  localStorage.setItem("userName", name);
  localStorage.setItem("userEmail", email);
  localStorage.setItem("userRole", role);

  window.dispatchEvent(new Event("authChange"));

  return response.data;
};

const register = async (userData) => {
  const response = await api.post("/auth/register", userData);

  return response.data;
};

const logout = () => {
  localStorage.removeItem("accessToken");
  localStorage.removeItem("tokenType");
  localStorage.removeItem("userId");
  localStorage.removeItem("userName");
  localStorage.removeItem("userEmail");
  localStorage.removeItem("userRole");

  window.dispatchEvent(new Event("authChange"));
};

const getCurrentUser = () => {
  const token = localStorage.getItem("accessToken");

  if (!token) {
    return null;
  }

  return {
    userId: localStorage.getItem("userId"),
    name: localStorage.getItem("userName"),
    email: localStorage.getItem("userEmail"),
    role: localStorage.getItem("userRole"),
  };
};

const isAuthenticated = () => {
  return !!localStorage.getItem("accessToken");
};

const authService = {
  login,
  register,
  logout,
  getCurrentUser,
  isAuthenticated,
};

export default authService;