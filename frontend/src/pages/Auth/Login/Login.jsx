import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import Container from "../../../components/common/layout/Container";
import Button from "../../../components/common/inputs/Button";
import ErrorMessage from "../../../components/common/feedback/ErrorMessage";
import LoadingSpinner from "../../../components/common/feedback/LoadingSpinner";

import authService from "../../../services/authService";

export default function Login() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    email: "",
    password: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setError("");
    setLoading(true);

    try {
      await authService.login(formData);

      navigate("/");
    } catch (err) {
      if (err.response?.status === 401) {
        setError("Invalid email or password.");
      } else if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError("Unable to login. Please try again.");
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <section className="min-h-[calc(100vh-5rem)] bg-[var(--color-background)] py-16">
      <Container>
        <div className="mx-auto max-w-md">
          <div className="rounded-2xl border border-[var(--color-border)] bg-[var(--color-surface)] p-8 shadow-[var(--shadow-md)] sm:p-10">

            <div className="mb-8 text-center">
              <h1 className="text-3xl font-bold text-[var(--color-text)]">
                Welcome Back
              </h1>

              <p className="mt-2 text-sm text-slate-600">
                Login to continue your travel journey.
              </p>
            </div>

            {error && (
              <div className="mb-6">
                <ErrorMessage message={error} />
              </div>
            )}

            <form
              onSubmit={handleSubmit}
              className="space-y-5"
            >
              <div>
                <label
                  htmlFor="email"
                  className="mb-2 block text-sm font-medium text-[var(--color-text)]"
                >
                  Email
                </label>

                <input
                  id="email"
                  name="email"
                  type="email"
                  value={formData.email}
                  onChange={handleChange}
                  placeholder="Enter your email"
                  required
                  className="
                    w-full
                    rounded-lg
                    border
                    border-[var(--color-border)]
                    bg-white
                    px-4
                    py-3
                    text-sm
                    text-[var(--color-text)]
                    outline-none
                    transition
                    focus:border-[var(--color-primary)]
                    focus:ring-2
                    focus:ring-emerald-100
                  "
                />
              </div>

              <div>
                <label
                  htmlFor="password"
                  className="mb-2 block text-sm font-medium text-[var(--color-text)]"
                >
                  Password
                </label>

                <input
                  id="password"
                  name="password"
                  type="password"
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="Enter your password"
                  required
                  className="
                    w-full
                    rounded-lg
                    border
                    border-[var(--color-border)]
                    bg-white
                    px-4
                    py-3
                    text-sm
                    text-[var(--color-text)]
                    outline-none
                    transition
                    focus:border-[var(--color-primary)]
                    focus:ring-2
                    focus:ring-emerald-100
                  "
                />
              </div>

              <Button
                type="submit"
                className="w-full"
                disabled={loading}
              >
                {loading ? (
                  <span className="flex items-center justify-center gap-2">
                    <LoadingSpinner />
                    Logging in...
                  </span>
                ) : (
                  "Login"
                )}
              </Button>
            </form>

            <div className="mt-6 text-center text-sm text-slate-600">
              <span>Don't have an account? </span>

              <Link
                to="/register"
                className="font-medium text-[var(--color-primary)] transition-colors hover:text-[var(--color-primary-hover)]"
              >
                Register
              </Link>
            </div>

          </div>
        </div>
      </Container>
    </section>
  );
}