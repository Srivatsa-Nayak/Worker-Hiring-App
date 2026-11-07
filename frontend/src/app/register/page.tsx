"use client";

import Link from "next/link";
import { Suspense, useEffect, useState } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import { useAuth, type RegisterData } from "@/components/AuthProvider";
import { AuthCard, safeNext } from "@/components/AuthCard";
import { Alert, Button, Field } from "@/components/ui";
import { ApiError } from "@/lib/api";

const empty: RegisterData = { firstName: "", lastName: "", email: "", phone: "", address: "", password: "" };

function RegisterForm() {
  const { register, status } = useAuth();
  const router = useRouter();
  const next = safeNext(useSearchParams().get("next"));
  const [form, setForm] = useState(empty);
  const [confirm, setConfirm] = useState("");
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  useEffect(() => {
    if (status === "authed") router.replace(next);
  }, [status, router, next]);

  const set = (k: keyof RegisterData) => (e: React.ChangeEvent<HTMLInputElement>) => setForm({ ...form, [k]: e.target.value });

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError("");
    if (form.password !== confirm) {
      setErrors({ confirm: "Passwords don't match" });
      return;
    }
    setErrors({});
    setBusy(true);
    try {
      await register(form);
      router.replace(next === "/" ? "/profile?welcome=1" : next);
    } catch (err) {
      if (err instanceof ApiError) {
        setErrors(err.fieldErrors);
        setError(err.message);
      } else setError("Could not create the account");
    } finally {
      setBusy(false);
    }
  };

  return (
    <AuthCard title="Sign up" intro="One account lets you both post work and apply for work.">
      <form onSubmit={submit} className="grid gap-4" noValidate>
        {error && <Alert>{error}</Alert>}
        <div className="grid gap-4 sm:grid-cols-2">
          <Field label="First name" autoComplete="given-name" value={form.firstName} onChange={set("firstName")} error={errors.firstName} />
          <Field label="Last name" autoComplete="family-name" value={form.lastName} onChange={set("lastName")} error={errors.lastName} />
        </div>
        <Field label="Email" type="email" autoComplete="email" value={form.email} onChange={set("email")} error={errors.email} />
        <Field
          label="Phone number"
          type="tel"
          autoComplete="tel"
          inputMode="tel"
          value={form.phone}
          onChange={set("phone")}
          error={errors.phone}
          hint="Recruiters use this to call you"
        />
        <Field label="Address" autoComplete="street-address" value={form.address} onChange={set("address")} error={errors.address} />
        <Field
          label="Password"
          type="password"
          autoComplete="new-password"
          value={form.password}
          onChange={set("password")}
          error={errors.password}
          hint="At least 6 characters"
        />
        <Field
          label="Confirm password"
          type="password"
          autoComplete="new-password"
          value={confirm}
          onChange={(e) => setConfirm(e.target.value)}
          error={errors.confirm}
        />
        <Button type="submit" disabled={busy}>
          {busy ? "Creating account…" : "Create account"}
        </Button>
      </form>
      <p className="mt-5 text-sm text-muted">
        Already have an account?{" "}
        <Link href={`/login${next !== "/" ? `?next=${encodeURIComponent(next)}` : ""}`} className="font-semibold text-text underline underline-offset-4">
          Log in
        </Link>
      </p>
    </AuthCard>
  );
}

export default function RegisterPage() {
  return (
    <Suspense>
      <RegisterForm />
    </Suspense>
  );
}
