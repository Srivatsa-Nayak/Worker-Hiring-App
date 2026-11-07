"use client";

import Link from "next/link";
import { useSearchParams } from "next/navigation";
import { Suspense, useState } from "react";
import { useAuth } from "@/components/AuthProvider";
import { MyAdsList } from "@/components/MyAdsList";
import { RequireAuth } from "@/components/RequireAuth";
import { Alert, Button, Field, LinkButton, PageTitle, TextArea } from "@/components/ui";
import { api, ApiError } from "@/lib/api";
import { formatDate } from "@/lib/format";
import type { User } from "@/lib/types";

function Profile() {
  const { user, setUser } = useAuth();
  const welcome = useSearchParams().get("welcome") === "1";
  const [editing, setEditing] = useState(false);
  const [form, setForm] = useState({
    firstName: user!.firstName,
    lastName: user!.lastName,
    phone: user!.phone,
    address: user!.address,
    skills: user!.skills ?? "",
  });
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [error, setError] = useState("");
  const [saved, setSaved] = useState(false);
  const [busy, setBusy] = useState(false);

  if (!user) return null;

  const set = (k: keyof typeof form) => (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) =>
    setForm({ ...form, [k]: e.target.value });

  const save = async (e: React.FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setErrors({});
    setError("");
    try {
      setUser(await api<User>("/api/users/me", "PUT", form));
      setEditing(false);
      setSaved(true);
    } catch (err) {
      if (err instanceof ApiError) {
        setErrors(err.fieldErrors);
        setError(err.message);
      } else setError("Could not save your profile");
    } finally {
      setBusy(false);
    }
  };

  return (
    <div className="mx-auto max-w-4xl px-4 py-10">
      <PageTitle aside={<LinkButton href="/settings" variant="secondary">Settings</LinkButton>}>
        {user.firstName} {user.lastName}
      </PageTitle>

      {welcome && (
        <div className="mb-6">
          <Alert kind="success">
            Welcome! Add your skills below so recruiters know what you can do, then{" "}
            <Link href="/" className="underline font-semibold">find work</Link> or{" "}
            <Link href="/post" className="underline font-semibold">post work</Link>.
          </Alert>
        </div>
      )}
      {saved && !editing && (
        <div className="mb-6">
          <Alert kind="success">Profile saved.</Alert>
        </div>
      )}

      <section className="rounded-md border border-line bg-surface p-5 sm:p-6">
        <div className="flex flex-wrap items-center justify-between gap-3 mb-4">
          <h2 className="sign-mid text-2xl text-heading">Your details</h2>
          {!editing && (
            <Button variant="secondary" onClick={() => { setEditing(true); setSaved(false); }}>
              Edit details
            </Button>
          )}
        </div>

        {editing ? (
          <form onSubmit={save} className="grid gap-4" noValidate>
            {error && <Alert>{error}</Alert>}
            <div className="grid gap-4 sm:grid-cols-2">
              <Field label="First name" value={form.firstName} onChange={set("firstName")} error={errors.firstName} />
              <Field label="Last name" value={form.lastName} onChange={set("lastName")} error={errors.lastName} />
              <Field label="Phone number" type="tel" value={form.phone} onChange={set("phone")} error={errors.phone} />
              <Field label="Address" value={form.address} onChange={set("address")} error={errors.address} />
            </div>
            <TextArea
              label="Skills and experience"
              placeholder="e.g. 3 years in a bakery, can drive a two-wheeler, speak Kannada, Tulu and Hindi"
              value={form.skills}
              onChange={set("skills")}
              error={errors.skills}
              hint="Shown to recruiters when you apply"
            />
            <div className="flex gap-2">
              <Button type="submit" disabled={busy}>
                {busy ? "Saving…" : "Save details"}
              </Button>
              <Button type="button" variant="ghost" onClick={() => setEditing(false)}>
                Cancel
              </Button>
            </div>
          </form>
        ) : (
          <dl className="grid sm:grid-cols-2 gap-x-6 gap-y-4">
            <Detail label="Email" value={user.email} />
            <Detail label="Phone number" value={user.phone} />
            <Detail label="Address" value={user.address} />
            <Detail label="Member since" value={formatDate(user.createdAt)} />
            <div className="sm:col-span-2">
              <Detail label="Skills and experience" value={user.skills || "Not added yet. Recruiters see this when you apply."} muted={!user.skills} />
            </div>
          </dl>
        )}
      </section>

      <section className="mt-10">
        <div className="flex flex-wrap items-center justify-between gap-3 mb-3">
          <h2 className="sign-mid text-2xl text-heading">Your ads</h2>
          <Link href="/my-ads" className="font-semibold underline underline-offset-4">
            Manage ads
          </Link>
        </div>
        <MyAdsList compact />
      </section>
    </div>
  );
}

function Detail({ label, value, muted }: { label: string; value: string; muted?: boolean }) {
  return (
    <div>
      <dt className="text-sm text-muted">{label}</dt>
      <dd className={`mt-0.5 break-words ${muted ? "text-muted" : "font-medium"}`}>{value}</dd>
    </div>
  );
}

export default function ProfilePage() {
  return (
    <RequireAuth>
      <Suspense>
        <Profile />
      </Suspense>
    </RequireAuth>
  );
}
