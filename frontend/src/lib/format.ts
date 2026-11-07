import type { ApplicationStatus, SalaryPeriod } from "./types";

const rupees = new Intl.NumberFormat("en-IN", { style: "currency", currency: "INR", maximumFractionDigits: 0 });

export function formatPay(amount: number) {
  return rupees.format(amount);
}

export const PERIOD_LABEL: Record<SalaryPeriod, string> = {
  HOUR: "per hour",
  DAY: "per day",
  WEEK: "per week",
  MONTH: "per month",
};

export function formatDate(iso: string) {
  // Dates from the API are plain "YYYY-MM-DD"; parse as local to avoid timezone shifts.
  const [y, m, d] = iso.slice(0, 10).split("-").map(Number);
  return new Date(y, m - 1, d).toLocaleDateString("en-IN", { day: "numeric", month: "short", year: "numeric" });
}

export function formatDateTime(iso: string) {
  return new Date(iso).toLocaleString("en-IN", {
    day: "numeric",
    month: "short",
    year: "numeric",
    hour: "numeric",
    minute: "2-digit",
  });
}

export function timeAgo(iso: string) {
  const diff = (Date.now() - new Date(iso).getTime()) / 1000;
  if (diff < 60) return "just now";
  if (diff < 3600) return `${Math.floor(diff / 60)} min ago`;
  if (diff < 86400) return `${Math.floor(diff / 3600)} h ago`;
  const days = Math.floor(diff / 86400);
  if (days < 30) return days === 1 ? "yesterday" : `${days} days ago`;
  return formatDate(iso);
}

/** Wording differs slightly depending on who is looking. */
export function statusLabel(status: ApplicationStatus, viewer: "seeker" | "recruiter") {
  switch (status) {
    case "APPLIED":
      return viewer === "seeker" ? "Applied" : "New";
    case "CALL_LATER":
      return "Call later";
    case "INTERVIEW":
      return "Interview";
    case "HIRED":
      return "Hired";
    case "REJECTED":
      return "Not selected";
  }
}

export function startsLabel(iso: string) {
  const [y, m, d] = iso.split("-").map(Number);
  const start = new Date(y, m - 1, d);
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const days = Math.round((start.getTime() - today.getTime()) / 86400000);
  if (days <= 0) return "Starts now";
  if (days === 1) return "Starts tomorrow";
  if (days < 14) return `Starts in ${days} days`;
  return `Starts ${formatDate(iso)}`;
}

export function todayIso() {
  const t = new Date();
  const pad = (n: number) => String(n).padStart(2, "0");
  return `${t.getFullYear()}-${pad(t.getMonth() + 1)}-${pad(t.getDate())}`;
}
