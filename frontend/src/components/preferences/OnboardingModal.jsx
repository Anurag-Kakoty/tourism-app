import { useState, useEffect, useRef } from "react";
import { useLocation } from "react-router-dom";
import {
  HiOutlineSparkles,
  HiOutlineXMark,
} from "react-icons/hi2";

import InterestPicker from "./InterestPicker";
import Button from "../common/inputs/Button";
import LoadingSpinner from "../common/feedback/LoadingSpinner";
import ErrorMessage from "../common/feedback/ErrorMessage";

import authService from "../../services/authService";
import preferenceService from "../../services/preferenceService";
import tagService from "../../services/tagService";

export default function OnboardingModal() {
  const location = useLocation();
  const [isOpen, setIsOpen] = useState(false);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [actionType, setActionType] = useState(null); // 'save' | 'skip'
  const [error, setError] = useState("");

  const [tags, setTags] = useState([]);
  const [selectedIds, setSelectedIds] = useState([]);

  // Track if we've already checked onboarding status for this auth session
  const checkedRef = useRef(false);

  useEffect(() => {
    const pathname = location.pathname;
    if (
      !authService.isAuthenticated() ||
      pathname === "/login" ||
      pathname === "/register" ||
      pathname === "/preferences"
    ) {
      return;
    }

    if (checkedRef.current) {
      return;
    }

    let isMounted = true;

    async function checkStatus() {
      try {
        setLoading(true);
        setError("");

        const prefs = await preferenceService.getPreferences();
        if (!isMounted) return;

        if (prefs && prefs.onboardingCompleted === false) {
          const allTags = await tagService.getAll();
          if (!isMounted) return;
          setTags(allTags || []);

          const initialSelected = (prefs.interests || []).map(
            (t) => t.id
          );
          setSelectedIds(initialSelected);
          setIsOpen(true);
        } else {
          checkedRef.current = true;
        }
      } catch (err) {
        console.warn("Failed to check onboarding status:", err);
        checkedRef.current = true;
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    }

    checkStatus();

    return () => {
      isMounted = false;
    };
  }, [location.pathname]);

  useEffect(() => {
    let isMounted = true;

    const handleAuthChange = async () => {
      checkedRef.current = false;
      const pathname = location.pathname;
      if (
        !authService.isAuthenticated() ||
        pathname === "/login" ||
        pathname === "/register" ||
        pathname === "/preferences"
      ) {
        if (isMounted) setIsOpen(false);
        return;
      }

      try {
        setLoading(true);
        setError("");

        const prefs = await preferenceService.getPreferences();
        if (!isMounted) return;

        if (prefs && prefs.onboardingCompleted === false) {
          const allTags = await tagService.getAll();
          if (!isMounted) return;
          setTags(allTags || []);

          const initialSelected = (prefs.interests || []).map(
            (t) => t.id
          );
          setSelectedIds(initialSelected);
          setIsOpen(true);
        } else {
          checkedRef.current = true;
          setIsOpen(false);
        }
      } catch (err) {
        console.warn("Failed to check onboarding status:", err);
        checkedRef.current = true;
        if (isMounted) setIsOpen(false);
      } finally {
        if (isMounted) {
          setLoading(false);
        }
      }
    };

    window.addEventListener("authChange", handleAuthChange);

    return () => {
      isMounted = false;
      window.removeEventListener("authChange", handleAuthChange);
    };
  }, [location.pathname]);

  const handleToggle = (tagId) => {
    setSelectedIds((prev) =>
      prev.includes(tagId)
        ? prev.filter((id) => id !== tagId)
        : [...prev, tagId]
    );
  };

  const handleSave = async () => {
    try {
      setSaving(true);
      setActionType("save");
      setError("");

      await preferenceService.updatePreferences(selectedIds);

      checkedRef.current = true;
      setIsOpen(false);
      window.dispatchEvent(new Event("preferencesUpdated"));
    } catch (err) {
      console.error("Error saving preferences:", err);
      setError(
        err.response?.data?.message ||
          "Unable to save your preferences. Please try again."
      );
    } finally {
      setSaving(false);
      setActionType(null);
    }
  };

  const handleSkip = async () => {
    try {
      setSaving(true);
      setActionType("skip");
      setError("");

      await preferenceService.skipPreferences();

      checkedRef.current = true;
      setIsOpen(false);
      window.dispatchEvent(new Event("preferencesUpdated"));
    } catch (err) {
      console.error("Error skipping onboarding:", err);
      // Even if the skip call fails, close the modal so user is not blocked
      checkedRef.current = true;
      setIsOpen(false);
    } finally {
      setSaving(false);
      setActionType(null);
    }
  };

  if (!isOpen) {
    return null;
  }

  return (
    <div
      className="
        fixed
        inset-0
        z-[70]
        flex
        items-center
        justify-center
        bg-slate-900/60
        p-4
        backdrop-blur-sm
      "
      onMouseDown={handleSkip}
    >
      <div
        className="
          flex
          max-h-[90vh]
          w-full
          max-w-3xl
          flex-col
          overflow-hidden
          rounded-3xl
          bg-white
          shadow-2xl
        "
        onMouseDown={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-start justify-between border-b border-slate-200 px-6 py-5 sm:px-8">
          <div className="flex items-start gap-3.5">
            <span className="flex h-11 w-11 shrink-0 items-center justify-center rounded-2xl bg-emerald-100 text-[var(--color-primary)]">
              <HiOutlineSparkles size={24} />
            </span>
            <div>
              <h2 className="text-xl font-bold text-slate-900 sm:text-2xl">
                Personalize Your Travel Experience
              </h2>
              <p className="mt-1 text-sm text-slate-600">
                Choose the activities and sights you love most. We'll tailor
                attraction recommendations just for you.
              </p>
            </div>
          </div>

          <button
            type="button"
            onClick={handleSkip}
            disabled={saving}
            className="
              rounded-xl
              p-2
              text-slate-400
              transition
              hover:bg-slate-100
              hover:text-slate-600
              disabled:opacity-40
            "
            aria-label="Skip for now"
          >
            <HiOutlineXMark size={24} />
          </button>
        </div>

        {/* Content */}
        <div className="flex-1 overflow-y-auto p-6 sm:p-8">
          {error && (
            <div className="mb-5">
              <ErrorMessage message={error} />
            </div>
          )}

          {loading ? (
            <div className="py-12">
              <LoadingSpinner message="Loading travel interests..." />
            </div>
          ) : (
            <InterestPicker
              tags={tags}
              selectedIds={selectedIds}
              onToggle={handleToggle}
              disabled={saving}
              compact
            />
          )}
        </div>

        {/* Footer */}
        <div className="flex flex-col-reverse items-center justify-between gap-3 border-t border-slate-200 bg-slate-50/80 px-6 py-4 sm:flex-row sm:px-8">
          <button
            type="button"
            onClick={handleSkip}
            disabled={saving}
            className="
              text-sm
              font-semibold
              text-slate-600
              transition
              hover:text-slate-900
              disabled:opacity-50
            "
          >
            {saving && actionType === "skip"
              ? "Skipping..."
              : "Skip for now"}
          </button>

          <div className="flex items-center gap-3 w-full sm:w-auto">
            <Button
              type="button"
              onClick={handleSave}
              disabled={saving || loading}
              className="w-full sm:w-auto"
            >
              {saving && actionType === "save" ? (
                <span className="flex items-center gap-2">
                  <LoadingSpinner />
                  Saving preferences...
                </span>
              ) : (
                `Save preferences ${
                  selectedIds.length > 0
                    ? `(${selectedIds.length})`
                    : ""
                }`
              )}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}
