import { useState, useEffect } from "react";
import { Link, useNavigate, useLocation } from "react-router-dom";
import {
  HiOutlineSparkles,
  HiOutlineCheckCircle,
  HiOutlineMapPin,
} from "react-icons/hi2";

import Container from "../../components/common/layout/Container";
import Section from "../../components/common/layout/Section";
import Button from "../../components/common/inputs/Button";
import LoadingSpinner from "../../components/common/feedback/LoadingSpinner";
import ErrorMessage from "../../components/common/feedback/ErrorMessage";
import InterestPicker from "../../components/preferences/InterestPicker";

import authService from "../../services/authService";
import preferenceService from "../../services/preferenceService";
import tagService from "../../services/tagService";

export default function Preferences() {
  const navigate = useNavigate();
  const location = useLocation();

  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [successMessage, setSuccessMessage] = useState("");

  const [tags, setTags] = useState([]);
  const [selectedIds, setSelectedIds] = useState([]);
  const [initialIds, setInitialIds] = useState([]);
  const [userProfile, setUserProfile] = useState(null);

  useEffect(() => {
    if (!authService.isAuthenticated()) {
      navigate("/login", {
        replace: true,
        state: { from: location },
      });
      return;
    }

    let isMounted = true;

    async function loadPreferencesData() {
      try {
        setLoading(true);
        setError("");

        const [prefData, tagData] = await Promise.all([
          preferenceService.getPreferences(),
          tagService.getAll(),
        ]);

        if (!isMounted) return;

        setUserProfile({
          name: prefData.name,
          email: prefData.email,
          onboardingCompleted: prefData.onboardingCompleted,
        });

        setTags(tagData || []);

        const savedTagIds = (prefData.interests || []).map(
          (tag) => tag.id
        );
        setSelectedIds(savedTagIds);
        setInitialIds(savedTagIds);
      } catch (err) {
        if (!isMounted) return;
        console.error("Error loading preferences data:", err);
        setError(
          "Unable to load your travel preferences. Please try again."
        );
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    }

    loadPreferencesData();

    return () => {
      isMounted = false;
    };
  }, [navigate, location]);

  const handleToggle = (tagId) => {
    setSuccessMessage("");
    setSelectedIds((prev) =>
      prev.includes(tagId)
        ? prev.filter((id) => id !== tagId)
        : [...prev, tagId]
    );
  };

  const handleReset = () => {
    setSelectedIds(initialIds);
    setSuccessMessage("");
    setError("");
  };

  const handleSave = async (e) => {
    if (e) e.preventDefault();

    try {
      setSaving(true);
      setError("");
      setSuccessMessage("");

      await preferenceService.updatePreferences(selectedIds);

      setInitialIds(selectedIds);
      setSuccessMessage("Your travel preferences have been successfully updated!");
      window.dispatchEvent(new Event("preferencesUpdated"));
    } catch (err) {
      console.error("Error saving preferences:", err);
      setError(
        err.response?.data?.message ||
          "Unable to update your preferences. Please try again."
      );
    } finally {
      setSaving(false);
    }
  };

  const hasChanges =
    selectedIds.length !== initialIds.length ||
    selectedIds.some((id) => !initialIds.includes(id));

  if (loading) {
    return (
      <Section title="Travel Preferences">
        <LoadingSpinner message="Loading your preferences..." />
      </Section>
    );
  }

  return (
    <section className="min-h-[calc(100vh-5rem)] bg-[var(--color-background)] py-12">
      <Container>
        <div className="mx-auto max-w-4xl space-y-8">
          {/* Header Card */}
          <div className="rounded-3xl border border-[var(--color-border)] bg-white p-8 shadow-sm">
            <div className="flex flex-col gap-6 md:flex-row md:items-center md:justify-between">
              <div className="flex items-start gap-4">
                <span className="flex h-14 w-14 shrink-0 items-center justify-center rounded-2xl bg-emerald-100 text-[var(--color-primary)]">
                  <HiOutlineSparkles size={32} />
                </span>

                <div>
                  <h1 className="text-2xl font-bold text-slate-900 sm:text-3xl">
                    Travel Preferences
                  </h1>
                  <p className="mt-1 text-sm text-slate-600">
                    Customize your interests to get personalized attraction
                    recommendations across India.
                  </p>

                  {userProfile && (
                    <div className="mt-3 flex flex-wrap items-center gap-2 text-xs text-slate-500">
                      <span className="font-semibold text-slate-700">
                        {userProfile.name}
                      </span>
                      <span>•</span>
                      <span>{userProfile.email}</span>
                    </div>
                  )}
                </div>
              </div>

              <Button
                to="/places?view=recommended"
                variant="outline"
                className="self-start md:self-auto text-sm"
              >
                <HiOutlineMapPin className="mr-2" size={18} />
                View Recommendations
              </Button>
            </div>
          </div>

          {/* Feedback messages */}
          {error && <ErrorMessage message={error} />}

          {successMessage && (
            <div className="flex items-center gap-3 rounded-2xl border border-emerald-200 bg-emerald-50 p-4 text-emerald-800">
              <HiOutlineCheckCircle
                className="shrink-0 text-emerald-600"
                size={22}
              />
              <div className="flex-1 text-sm font-medium">
                {successMessage}
              </div>
              <Link
                to="/places?view=recommended"
                className="text-xs font-bold text-emerald-900 underline hover:text-emerald-950"
              >
                Explore places &rarr;
              </Link>
            </div>
          )}

          {/* Main Interests Selection Card */}
          <div className="rounded-3xl border border-[var(--color-border)] bg-white p-8 shadow-sm">
            <div className="mb-6 flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <h2 className="text-lg font-bold text-slate-900">
                  Select Your Interests
                </h2>
                <p className="text-xs text-slate-500">
                  Click on any theme to add or remove it from your recommendation profile.
                </p>
              </div>

              <div className="flex items-center gap-3">
                {hasChanges && (
                  <button
                    type="button"
                    onClick={handleReset}
                    disabled={saving}
                    className="text-xs font-semibold text-slate-500 hover:text-slate-800 hover:underline"
                  >
                    Discard changes
                  </button>
                )}
                <Button
                  type="button"
                  onClick={handleSave}
                  disabled={saving || !hasChanges}
                >
                  {saving ? (
                    <span className="flex items-center gap-2">
                      <LoadingSpinner />
                      Saving...
                    </span>
                  ) : (
                    "Save Changes"
                  )}
                </Button>
              </div>
            </div>

            <InterestPicker
              tags={tags}
              selectedIds={selectedIds}
              onToggle={handleToggle}
              disabled={saving}
            />

            <div className="mt-8 flex items-center justify-between border-t border-slate-100 pt-6">
              <span className="text-xs text-slate-500">
                Tip: Changes here immediately update the "Recommended for you" tab on the Places page.
              </span>

              <Button
                type="button"
                onClick={handleSave}
                disabled={saving || !hasChanges}
              >
                {saving ? "Saving..." : "Save Preferences"}
              </Button>
            </div>
          </div>
        </div>
      </Container>
    </section>
  );
}
