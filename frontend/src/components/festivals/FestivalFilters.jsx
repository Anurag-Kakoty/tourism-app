import { useEffect, useState } from "react";

import stateService from "../../services/stateService";
import Button from "../common/inputs/Button";

export default function FestivalFilters({
  selectedState,
  selectedYear,
  years,
  onStateChange,
  onYearChange,
  onReset,
}) {
  const [states, setStates] = useState([]);

  useEffect(() => {
    loadStates();
  }, []);

  async function loadStates() {
    try {
      const data = await stateService.getAll();
      setStates(data);
    } catch (err) {
      console.error("Unable to load states for festival filters:", err);
    }
  }

  return (
    <div className="mb-10 rounded-2xl border border-[var(--color-border)] bg-white p-6 shadow-sm">
      <div className="grid gap-6 md:grid-cols-3 md:items-end">
        {/* State */}
        <div>
          <label
            htmlFor="festival-state"
            className="mb-2 block text-sm font-semibold text-slate-700"
          >
            State
          </label>

          <select
            id="festival-state"
            value={selectedState}
            onChange={(event) => onStateChange(event.target.value)}
            className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-slate-700 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
          >
            <option value="">All States</option>

            {states.map((state) => (
              <option key={state.id} value={state.id}>
                {state.name}
              </option>
            ))}
          </select>
        </div>

        {/* Year */}
        <div>
          <label
            htmlFor="festival-year"
            className="mb-2 block text-sm font-semibold text-slate-700"
          >
            Year
          </label>

          <select
            id="festival-year"
            value={selectedYear}
            onChange={(event) => onYearChange(event.target.value)}
            className="w-full rounded-xl border border-slate-300 bg-white px-4 py-3 text-slate-700 outline-none transition focus:border-[var(--color-primary)] focus:ring-2 focus:ring-[var(--color-primary)]/20"
          >
            <option value="">All Years</option>

            {years.map((year) => (
              <option key={year} value={year}>
                {year}
              </option>
            ))}
          </select>
        </div>

        {/* Reset */}
        <Button
          type="button"
          variant="outline"
          onClick={onReset}
          className="w-full"
        >
          Reset Filters
        </Button>
      </div>
    </div>
  );
}