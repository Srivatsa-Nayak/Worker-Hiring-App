"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { useAuth } from "@/components/AuthProvider";
import { RequireAuth } from "@/components/RequireAuth";
import { Alert, Button, Field, PageTitle } from "@/components/ui";
import { api, ApiError } from "@/lib/api";

type Theme = "system" | "light" | "dark";

function readTheme(): Theme {
  try {
    const t = localStorage.getItem("wh_theme");
    return t === "light" || t === "dark" ? t : "system";
  } catch {
    return "system";
  }
}

function applyTheme(t: Theme) {
  try {
    if (t === "system") localStorage.removeItem("wh_theme");
    else localStorage.setItem("wh_theme", t);
  } catch {
    /* ignore */
  }
  if (t === "system") delete document.documentElement.dataset.theme;
  else document.documentElement.dataset.theme = t;
}

function Settings() {
  const { logout, clearSession } = useAuth();
  const router = useRouter();
  const [theme, setTheme] = useState<Theme>("system");

  useEffect(() => setTheme(readTheme()), []);

  return (
    <div className="mx-auto max-w-2xl px-4 py-10 grid gap-8">
      <PageTitle>Settings</PageTitle>

      <Panel title="Appearance">
        <fieldset>
          <legend className="text-sm font-semibold mb-2">Theme</legend>
          <div className="flex flex-wrap gap-2">
            {(["system", "light", "dark"] as Theme[]).map((t) => (
              <label
                key={t}
                className="cursor-pointer rounded-md border border-line px-4 py-2 font-medium has-[:checked]:border-brand has-[:checked]:bg-brand has-[:checked]:text-on-brand has-[:focus-visible]:outline has-[:focus-visible]:outline-3"
              >
                <input
                  type="radio"
                  name="theme"
                  value={t}
                  checked={theme === t}
                  onChange={() => {
                    setTheme(t);
                    applyTheme(t);
                  }}
                  className="sr-only"
                />
                {t === "system" ? "Match device" : t === "light" ? "Light" : "Dark"}
              </label>
            ))}
          </div>
        </fieldset>
      </Panel>

      <ChangePassword />

      <Panel title="Log out">
        <p className="text-muted mb-3">Log out on this device.</p>
        <Button
          variant="secondary"
          onClick={async () => {
            await logout();
            router.replace("/");
          }}
        >
          Log out
        </Button>
      </Panel>

      <DeleteAccount
        onDeleted={() => {
          clearSession();
          router.replace("/");
        }}
      />
    </div>
  );
}

function ChangePassword() {
  const [current, setCurrent] = useState("");
  const [next, setNext] = useState("");
  const [errors, setErrors] = useState<Record<string, string>>({});
  const [msg, setMsg] = useState<{ kind: "error" | "success"; text: string } | null>(null);
  const [busy, setBusy] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setErrors({});
    setMsg(null);
    try {
      await api("/api/users/me/password", "PUT", { currentPassword: current, newPassword: next });
      setCurrent("");
      setNext("");
      setMsg({ kind: "success", text: "Password changed." });
    } catch (err) {
      if (err instanceof ApiError) {
        setErrors(err.fieldErrors);
        setMsg({ kind: "error", text: err.message });
      }
    } finally {
      setBusy(false);
    }
  };

  return (
    <Panel title="Change password">
      <form onSubmit={submit} className="grid gap-4" noValidate>
        {msg && <Alert kind={msg.kind}>{msg.text}</Alert>}
        <Field
          label="Current password"
          type="password"
          autoComplete="current-password"
          value={current}
          onChange={(e) => setCurrent(e.target.value)}
          error={errors.currentPassword}
        />
        <Field
          label="New password"
          type="password"
          autoComplete="new-password"
          value={next}
          onChange={(e) => setNext(e.target.value)}
          error={errors.newPassword}
          hint="At least 6 characters"
        />
        <div>
          <Button type="submit" disabled={busy}>
            {busy ? "Changing…" : "Change password"}
          </Button>
        </div>
      </form>
    </Panel>
  );
}

function DeleteAccount({ onDeleted }: { onDeleted: () => void }) {
  const [open, setOpen] = useState(false);
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setError("");
    try {
      await api("/api/users/me/delete", "POST", { password });
      onDeleted();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Could not delete the account");
      setBusy(false);
    }
  };

  return (
    <Panel title="Delete account" danger>
      <p className="text-muted mb-3">
        Removes your profile, all your ads and their applicants, and every application you&apos;ve sent. This can&apos;t be undone.
      </p>
      {!open ? (
        <Button variant="danger" onClick={() => setOpen(true)}>
          Delete my account
        </Button>
      ) : (
        <form onSubmit={submit} className="grid gap-4">
          {error && <Alert>{error}</Alert>}
          <Field
            label="Enter your password to confirm"
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
          <div className="flex gap-2">
            <Button variant="danger" type="submit" disabled={busy || !password}>
              {busy ? "Deleting…" : "Delete account forever"}
            </Button>
            <Button variant="ghost" type="button" onClick={() => setOpen(false)}>
              Cancel
            </Button>
          </div>
        </form>
      )}
    </Panel>
  );
}

function Panel({ title, danger, children }: { title: string; danger?: boolean; children: React.ReactNode }) {
  return (
    <section className={`rounded-md border bg-surface p-5 sm:p-6 ${danger ? "border-stop/40" : "border-line"}`}>
      <h2 className={`sign-mid text-2xl mb-3 ${danger ? "text-stop" : "text-heading"}`}>{title}</h2>
      {children}
    </section>
  );
}

export default function SettingsPage() {
  return (
    <RequireAuth>
      <Settings />
    </RequireAuth>
  );
}
