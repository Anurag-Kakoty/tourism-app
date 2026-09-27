import { useState } from "react";

import Button from "../common/inputs/Button";
import Container from "../common/layout/Container";
import Section from "../common/layout/Section";

const initialForm = {
  startDate: "",
  endDate: "",
  numberOfTravelers: 1,
  budget: "",
  destinationId: "",
  experienceIds: [],
  festivalId: "",
};

function AiItineraryForm({ onGenerate, loading }) {
  const [form, setForm] = useState(initialForm);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setForm((current) => ({
      ...current,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    const request = {
      startDate: form.startDate,
      endDate: form.endDate,
      numberOfTravelers: Number(form.numberOfTravelers),
      budget: Number(form.budget),
      destinationId: Number(form.destinationId),
      experienceIds: form.experienceIds,
      festivalId: form.festivalId
        ? Number(form.festivalId)
        : null,
    };

    await onGenerate(request);
  };

  return (
    <Section>
      <Container>
        <div className="mx-auto max-w-3xl">
          <div className="mb-8 text-center">
            <h1 className="text-3xl font-bold text-gray-900">
              AI Trip Planner
            </h1>

            <p className="mt-2 text-gray-600">
              Tell us about your trip and let AI create a
              personalized itinerary using our tourism data.
            </p>
          </div>

          <form
            onSubmit={handleSubmit}
            className="space-y-6 rounded-2xl border border-gray-200 bg-white p-6 shadow-sm"
          >
            <div className="grid gap-6 md:grid-cols-2">
              <div>
                <label
                  htmlFor="startDate"
                  className="mb-2 block text-sm font-medium text-gray-700"
                >
                  Start Date
                </label>

                <input
                  id="startDate"
                  name="startDate"
                  type="date"
                  value={form.startDate}
                  onChange={handleChange}
                  required
                  className="w-full rounded-xl border border-gray-300 px-4 py-3 outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
                />
              </div>

              <div>
                <label
                  htmlFor="endDate"
                  className="mb-2 block text-sm font-medium text-gray-700"
                >
                  End Date
                </label>

                <input
                  id="endDate"
                  name="endDate"
                  type="date"
                  value={form.endDate}
                  onChange={handleChange}
                  required
                  className="w-full rounded-xl border border-gray-300 px-4 py-3 outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
                />
              </div>
            </div>

            <div className="grid gap-6 md:grid-cols-2">
              <div>
                <label
                  htmlFor="numberOfTravelers"
                  className="mb-2 block text-sm font-medium text-gray-700"
                >
                  Number of Travelers
                </label>

                <input
                  id="numberOfTravelers"
                  name="numberOfTravelers"
                  type="number"
                  min="1"
                  value={form.numberOfTravelers}
                  onChange={handleChange}
                  required
                  className="w-full rounded-xl border border-gray-300 px-4 py-3 outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
                />
              </div>

              <div>
                <label
                  htmlFor="budget"
                  className="mb-2 block text-sm font-medium text-gray-700"
                >
                  Budget (₹)
                </label>

                <input
                  id="budget"
                  name="budget"
                  type="number"
                  min="0"
                  step="0.01"
                  value={form.budget}
                  onChange={handleChange}
                  required
                  placeholder="e.g. 15000"
                  className="w-full rounded-xl border border-gray-300 px-4 py-3 outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
                />
              </div>
            </div>

            <div>
              <label
                htmlFor="destinationId"
                className="mb-2 block text-sm font-medium text-gray-700"
              >
                Destination
              </label>

              <select
                id="destinationId"
                name="destinationId"
                value={form.destinationId}
                onChange={handleChange}
                required
                className="w-full rounded-xl border border-gray-300 bg-white px-4 py-3 outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
              >
                <option value="">
                  Select a destination
                </option>

                <option value="1">
                  Cherrapunji
                </option>

                <option value="2">
                  Shillong
                </option>

                <option value="3">
                  Mawlynnong
                </option>

                <option value="4">
                  Dawki
                </option>

                <option value="5">
                  Guwahati
                </option>

                <option value="6">
                  Kaziranga
                </option>
              </select>
            </div>

            <div>
              <label
                htmlFor="festivalId"
                className="mb-2 block text-sm font-medium text-gray-700"
              >
                Festival (Optional)
              </label>

              <select
                id="festivalId"
                name="festivalId"
                value={form.festivalId}
                onChange={handleChange}
                className="w-full rounded-xl border border-gray-300 bg-white px-4 py-3 outline-none transition focus:border-primary focus:ring-2 focus:ring-primary/20"
              >
                <option value="">
                  No specific festival
                </option>

                <option value="1">
                  Wangala Festival
                </option>

                <option value="2">
                  Nongkrem Dance Festival
                </option>

                <option value="3">
                  Shad Suk Mynsiem
                </option>

                <option value="4">
                  Rongali Bihu
                </option>

                <option value="5">
                  Ambubachi Mela
                </option>
              </select>
            </div>

            <div className="pt-2">
              <Button
                type="submit"
                disabled={loading}
                className="w-full"
              >
                {loading
                  ? "Generating itinerary..."
                  : "Generate AI Itinerary"}
              </Button>
            </div>
          </form>
        </div>
      </Container>
    </Section>
  );
}

export default AiItineraryForm;