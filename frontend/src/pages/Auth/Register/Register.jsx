import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import Container from "../../../components/common/layout/Container";
import Button from "../../../components/common/inputs/Button";
import ErrorMessage from "../../../components/common/feedback/ErrorMessage";
import LoadingSpinner from "../../../components/common/feedback/LoadingSpinner";

import authService from "../../../services/authService";

export default function Register() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: "",
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
      await authService.register(formData);

      navigate("/login");
    } catch (err) {
      if (err.response?.status === 409) {
        setError("An account with this email already exists.");
      } else if (err.response?.data?.message) {
        setError(err.response.data.message);
      } else {
        setError("Unable to create your account. Please try again.");
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
                Create Your Account
              </h1>

              <p className="mt-2 text-sm text-slate-600">
                Join us and start planning your next journey.
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
                  htmlFor="name"
                  className="mb-2 block text-sm font-medium text-[var(--color-text)]"
                >
                  Name
                </label>

                <input
                  id="name"
                  name="name"
                  type="text"
                  value={formData.name}
                  onChange={handleChange}
                  placeholder="Enter your name"
                  required
                  maxLength={100}
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
                  maxLength={150}
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
                  placeholder="Enter a password"
                  required
                  minLength={8}
                  maxLength={100}
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

                <p className="mt-2 text-xs text-slate-500">
                  Password must be at least 8 characters.
                </p>
              </div>

              <Button
                type="submit"
                className="w-full"
                disabled={loading}
              >
                {loading ? (
                  <span className="flex items-center justify-center gap-2">
                    <LoadingSpinner />
                    Creating account...
                  </span>
                ) : (
                  "Create Account"
                )}
              </Button>
            </form>

            <div className="mt-6 text-center text-sm text-slate-600">
              <span>Already have an account? </span>

              <Link
                to="/login"
                className="font-medium text-[var(--color-primary)] transition-colors hover:text-[var(--color-primary-hover)]"
              >
                Login
              </Link>
            </div>

          </div>
        </div>
      </Container>
    </section>
  );
}