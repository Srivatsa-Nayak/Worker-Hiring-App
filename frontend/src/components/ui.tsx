"use client";

import Link from "next/link";
import { useId } from "react";
import type { ApplicationStatus, SalaryPeriod } from "@/lib/types";
import { formatPay, PERIOD_LABEL, statusLabel } from "@/lib/format";

type Variant = "primary" | "secondary" | "danger" | "ghost";

const variants: Record<Variant, string> = {
  primary: "bg-brand text-on-brand hover:bg-brand-hover border border-transparent",
  secondary: "bg-surface text-text border border-line hover:border-muted",
  danger: "bg-surface text-stop border border-stop/50 hover:bg-stop-soft",
  ghost: "bg-transparent text-text border border-transparent hover:bg-line/40",
};

const base =
  "inline-flex items-center justify-center gap-2 rounded-md px-4 min-h-11 text-[0.95rem] font-semibold transition-colors disabled:opacity-60 disabled:cursor-not-allowed";

export function Button({
  variant = "primary",
  className = "",
  ...props
}: React.ButtonHTMLAttributes<HTMLButtonElement> & { variant?: Variant }) {
  return <button className={`${base} ${variants[variant]} ${className}`} {...props} />;
}

export function LinkButton({
  href,
  variant = "primary",
  className = "",
  children,
}: {
  href: string;
  variant?: Variant;
  className?: string;
  children: React.ReactNode;
}) {
  return (
    <Link href={href} className={`${base} ${variants[variant]} ${className}`}>
      {children}
    </Link>
  );
}

const inputClass =
  "w-full rounded-md border border-line bg-surface px-3 min-h-11 text-text placeholder:text-muted/70 focus:border-brand focus:outline-none focus-visible:outline-none focus:ring-2 focus:ring-[var(--focus)]/40 aria-[invalid=true]:border-stop";

interface FieldProps {
  label: string;
  error?: string;
  hint?: string;
  className?: string;
}

export function Field({
  label,
  error,
  hint,
  className = "",
  ...props
}: FieldProps & React.InputHTMLAttributes<HTMLInputElement>) {
  const id = useId();
  return (
    <div className={className}>
      <label htmlFor={id} className="block text-sm font-semibold mb-1.5">
        {label}
      </label>
      <input
        id={id}
        aria-invalid={error ? true : undefined}
        aria-describedby={error || hint ? `${id}-msg` : undefined}
        className={inputClass}
        {...props}
      />
      <FieldMessage id={`${id}-msg`} error={error} hint={hint} />
    </div>
  );
}

export function TextArea({
  label,
  error,
  hint,
  className = "",
  ...props
}: FieldProps & React.TextareaHTMLAttributes<HTMLTextAreaElement>) {
  const id = useId();
  return (
    <div className={className}>
      <label htmlFor={id} className="block text-sm font-semibold mb-1.5">
        {label}
      </label>
      <textarea
        id={id}
        rows={4}
        aria-invalid={error ? true : undefined}
        aria-describedby={error || hint ? `${id}-msg` : undefined}
        className={`${inputClass} py-2.5 leading-relaxed`}
        {...props}
      />
      <FieldMessage id={`${id}-msg`} error={error} hint={hint} />
    </div>
  );
}

export function Select({
  label,
  error,
  hint,
  className = "",
  children,
  ...props
}: FieldProps & React.SelectHTMLAttributes<HTMLSelectElement>) {
  const id = useId();
  return (
    <div className={className}>
      <label htmlFor={id} className="block text-sm font-semibold mb-1.5">
        {label}
      </label>
      <select id={id} aria-invalid={error ? true : undefined} className={inputClass} {...props}>
        {children}
      </select>
      <FieldMessage id={`${id}-msg`} error={error} hint={hint} />
    </div>
  );
}

function FieldMessage({ id, error, hint }: { id: string; error?: string; hint?: string }) {
  if (error)
    return (
      <p id={id} className="mt-1.5 text-sm text-stop">
        {error}
      </p>
    );
  if (hint)
    return (
      <p id={id} className="mt-1.5 text-sm text-muted">
        {hint}
      </p>
    );
  return null;
}

export function Alert({ kind = "error", children }: { kind?: "error" | "success" | "info"; children: React.ReactNode }) {
  const styles = {
    error: "bg-stop-soft text-stop border-stop/30",
    success: "bg-go-soft text-go border-go/30",
    info: "bg-info-soft text-info border-info/30",
  }[kind];
  return (
    <div role={kind === "error" ? "alert" : "status"} className={`rounded-md border px-4 py-3 text-[0.95rem] ${styles}`}>
      {children}
    </div>
  );
}

const statusStyle: Record<ApplicationStatus, string> = {
  APPLIED: "bg-info-soft text-info",
  CALL_LATER: "bg-wait-soft text-wait",
  INTERVIEW: "bg-sign-soft text-on-sign-soft",
  HIRED: "bg-go-soft text-go",
  REJECTED: "bg-stop-soft text-stop",
};

export function StatusBadge({ status, viewer }: { status: ApplicationStatus; viewer: "seeker" | "recruiter" }) {
  return (
    <span className={`inline-flex items-center rounded px-2 py-0.5 text-sm font-semibold ${statusStyle[status]}`}>
      {statusLabel(status, viewer)}
    </span>
  );
}

/** The pay, painted big like a price on a shop board. */
export function PayTag({ amount, period, size = "md" }: { amount: number; period: SalaryPeriod; size?: "md" | "lg" }) {
  return (
    <div className="inline-flex flex-col items-start self-start rounded-sm bg-sign text-on-sign px-3 py-1.5 shrink-0">
      <span className={`sign ${size === "lg" ? "text-5xl" : "text-3xl"}`}>{formatPay(amount)}</span>
      <span className="text-[0.8rem] font-semibold">{PERIOD_LABEL[period]}</span>
    </div>
  );
}

export function PageTitle({ children, aside }: { children: React.ReactNode; aside?: React.ReactNode }) {
  return (
    <div className="flex flex-wrap items-end justify-between gap-4 mb-6">
      <h1 className="sign text-5xl text-heading">{children}</h1>
      {aside}
    </div>
  );
}

export function Empty({ title, children }: { title: string; children?: React.ReactNode }) {
  return (
    <div className="rounded-md border border-dashed border-line px-6 py-10 text-center">
      <p className="sign-mid text-2xl text-heading">{title}</p>
      {children && <div className="mt-3 text-muted">{children}</div>}
    </div>
  );
}
