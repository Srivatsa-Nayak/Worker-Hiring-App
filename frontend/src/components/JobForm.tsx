"use client";

import { useEffect, useState } from "react";
import { api, ApiError } from "@/lib/api";
import type { CategoryOption, JobInput } from "@/lib/types";
import { todayIso } from "@/lib/format";
import { Alert, Button, Field, Select, TextArea } from "./ui";

interface Props {
  initial: JobInput;
  submitLabel: string;
  busyLabel: string;
  onSubmit: (data: JobInput) => Promise<void>;
  onCancel?: () => void;
}

/** The work-details form from the original app: name, location, salary, description, requirements, start date, contact. */
export function JobForm({ initial, submitLabel, busyLabel, onSubmit, onCancel }: Props) {
  const [form, setForm] = useState<JobInput>(initial);
  const [categories, setCategories] = useState<CategoryOption[]>([]);
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    api<CategoryOption[]>("/api/jobs/categories").then(setCategories).catch(() => {});
  }, []);

  const set =
    <K extends keyof JobInput>(k: K, numeric = false) =>
    (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement>) => {
      const v = e.target.value;
      setForm((f) => ({ ...f, [k]: numeric ? (v === "" ? "" : Number(v)) : v }));
    };

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setError("");
    setErrors({});
    try {
      await onSubmit(form);
    } catch (err) {
      if (err instanceof ApiError) {
        setErrors(err.fieldErrors);
        setError(err.message);
      } else setError("Could not save the ad");
      window.scrollTo({ top: 0, behavior: "smooth" });
    } finally {
      setBusy(false);
    }
  };

  return (
    <form onSubmit={submit} className="grid gap-6" noValidate>
      {error && <Alert>{error}</Alert>}

      <fieldset className="grid gap-4 rounded-md border border-line bg-surface p-5">
        <legend className="sign-mid text-xl text-heading px-1">The work</legend>
        <Field label="Work name" placeholder="e.g. Grocery shop helper" value={form.title} onChange={set("title")} error={errors.title} />
        <div className="grid gap-4 sm:grid-cols-2">
          <Select label="Category" value={form.category} onChange={set("category")} error={errors.category}>
            <option value="">Choose one</option>
            {categories.map((c) => (
              <option key={c.value} value={c.value}>
                {c.label}
              </option>
            ))}
          </Select>
          <Field label="Work location" placeholder="Street and area" value={form.location} onChange={set("location")} error={errors.location} />
        </div>
        <TextArea
          label="Work description"
          placeholder="What will the worker do each day? What are the hours?"
          value={form.description}
          onChange={set("description")}
          error={errors.description}
        />
        <TextArea
          label="Requirements"
          placeholder="Skills, languages, licence, experience…"
          value={form.requirements}
          onChange={set("requirements")}
          error={errors.requirements}
        />
      </fieldset>

      <fieldset className="grid gap-4 rounded-md border border-line bg-surface p-5">
        <legend className="sign-mid text-xl text-heading px-1">Pay and start</legend>
        <div className="grid gap-4 sm:grid-cols-2">
          <Field
            label="Salary (₹)"
            type="number"
            inputMode="numeric"
            min={1}
            value={form.salaryAmount}
            onChange={set("salaryAmount", true)}
            error={errors.salaryAmount}
          />
          <Select label="Paid" value={form.salaryPeriod} onChange={set("salaryPeriod")} error={errors.salaryPeriod}>
            <option value="HOUR">Per hour</option>
            <option value="DAY">Per day</option>
            <option value="WEEK">Per week</option>
            <option value="MONTH">Per month</option>
          </Select>
          <Field label="Start date" type="date" min={todayIso()} value={form.startDate} onChange={set("startDate")} error={errors.startDate} />
          <Field
            label="Openings"
            type="number"
            inputMode="numeric"
            min={1}
            value={form.openings}
            onChange={set("openings", true)}
            error={errors.openings}
            hint="How many people you want to hire"
          />
        </div>
      </fieldset>

      <fieldset className="grid gap-4 rounded-md border border-line bg-surface p-5">
        <legend className="sign-mid text-xl text-heading px-1">How to reach you</legend>
        <p className="text-sm text-muted -mt-1">Only shown to people who are logged in.</p>
        <div className="grid gap-4 sm:grid-cols-2">
          <Field label="Phone number" type="tel" value={form.contactPhone} onChange={set("contactPhone")} error={errors.contactPhone} />
          <Field label="Email" type="email" value={form.contactEmail} onChange={set("contactEmail")} error={errors.contactEmail} />
        </div>
      </fieldset>

      <div className="flex flex-wrap gap-3">
        <Button type="submit" disabled={busy}>
          {busy ? busyLabel : submitLabel}
        </Button>
        {onCancel && (
          <Button type="button" variant="secondary" onClick={onCancel}>
            Cancel
          </Button>
        )}
      </div>
    </form>
  );
}

/** JobInput -> request body (empty numeric fields become null so the API reports them as missing). */
export function toJobBody(f: JobInput) {
  return {
    ...f,
    category: f.category || null,
    salaryAmount: f.salaryAmount === "" ? null : f.salaryAmount,
    openings: f.openings === "" ? null : f.openings,
    startDate: f.startDate || null,
  };
}
