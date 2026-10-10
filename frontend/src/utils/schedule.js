const DAY_ORDER = [
  "MONDAY",
  "TUESDAY",
  "WEDNESDAY",
  "THURSDAY",
  "FRIDAY",
  "SATURDAY",
  "SUNDAY",
];

const DAY_LABELS = {
  MONDAY: "Monday",
  TUESDAY: "Tuesday",
  WEDNESDAY: "Wednesday",
  THURSDAY: "Thursday",
  FRIDAY: "Friday",
  SATURDAY: "Saturday",
  SUNDAY: "Sunday",
};

/**
 * Format a 24-hour time string (e.g. "09:00:00" or "14:00") to 12-hour format with AM/PM.
 * Returns an empty string if timeStr is null, undefined, or empty.
 */
export function formatTime(timeStr) {
  if (!timeStr || typeof timeStr !== "string") {
    return "";
  }

  const trimmed = timeStr.trim();
  if (!trimmed) {
    return "";
  }

  const parts = trimmed.split(":");
  if (parts.length < 2) {
    return trimmed;
  }

  const hours = parseInt(parts[0], 10);
  const minutes = parseInt(parts[1], 10);

  if (Number.isNaN(hours) || Number.isNaN(minutes)) {
    return trimmed;
  }

  const period = hours >= 12 ? "PM" : "AM";
  const displayHours = hours % 12 === 0 ? 12 : hours % 12;
  const displayMinutes = String(minutes).padStart(2, "0");

  return `${displayHours}:${displayMinutes} ${period}`;
}

/**
 * Format an opening and closing time pair.
 */
export function formatTimeRange(openingTime, closingTime) {
  const formattedOpen = formatTime(openingTime);
  const formattedClose = formatTime(closingTime);

  if (formattedOpen && formattedClose) {
    return `${formattedOpen} – ${formattedClose}`;
  }
  if (formattedOpen) {
    return `Opens at ${formattedOpen}`;
  }
  if (formattedClose) {
    return `Closes at ${formattedClose}`;
  }
  return "";
}

/**
 * Format a DayOfWeek enum string (e.g. "MONDAY") to a capitalized label ("Monday").
 */
export function formatDayOfWeek(day) {
  if (!day || typeof day !== "string") {
    return "";
  }
  const upper = day.toUpperCase();
  if (DAY_LABELS[upper]) {
    return DAY_LABELS[upper];
  }
  return day.charAt(0).toUpperCase() + day.slice(1).toLowerCase();
}

/**
 * Format an array or Set of DayOfWeek enum strings into a sorted, comma-separated list.
 * Returns null if closedDays is empty or unspecified.
 */
export function formatClosedDays(closedDays) {
  if (!closedDays) {
    return null;
  }

  const daysArray = Array.isArray(closedDays)
    ? closedDays
    : Array.from(closedDays);

  if (daysArray.length === 0) {
    return null;
  }

  const sortedDays = [...daysArray].sort((a, b) => {
    const indexA = DAY_ORDER.indexOf(String(a).toUpperCase());
    const indexB = DAY_ORDER.indexOf(String(b).toUpperCase());
    const safeA = indexA === -1 ? 999 : indexA;
    const safeB = indexB === -1 ? 999 : indexB;
    return safeA - safeB;
  });

  return sortedDays.map(formatDayOfWeek).join(", ");
}

/**
 * Get human-readable schedule text for an attraction.
 * Returns { hoursText: string, closedDaysText: string | null }.
 */
export function getAttractionSchedule(place) {
  if (!place) {
    return {
      hoursText: "Not specified",
      closedDaysText: null,
    };
  }

  let hoursText = "";
  if (place.openingTime || place.closingTime) {
    hoursText = formatTimeRange(place.openingTime, place.closingTime);
  } else if (place.openingHours && place.openingHours.trim()) {
    hoursText = place.openingHours.trim();
  }

  if (!hoursText) {
    hoursText = "Not specified";
  }

  const closedDaysText = formatClosedDays(place.closedDays);

  return {
    hoursText,
    closedDaysText,
  };
}

