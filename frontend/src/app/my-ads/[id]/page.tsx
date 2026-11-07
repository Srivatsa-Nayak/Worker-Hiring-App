"use client";

import Link from "next/link";
import { useParams, useRouter, useSearchParams } from "next/navigation";
import { Suspense, useCallback, useEffect, useState } from "react";
import { RequireAuth } from "@/components/RequireAuth";
import { Alert, Button, Empty, Field, LinkButton, StatusBadge, TextArea } from "@/components/ui";
import { api, ApiError } from "@/lib/api";
import { formatDateTime, formatPay, PERIOD_LABEL, statusLabel, timeAgo } from "@/lib/format";
import type { Application, ApplicationStatus, Job } from "@/lib/types";

const FILTERS: (ApplicationStatus | "ALL")[] = ["ALL", "APPLIED", "CALL_LATER", "INTERVIEW", "HIRED", "REJECTED"];

function ManageAd() {
  const { id } = useParams<{ id: string }>();
  const router = useRouter();
  const posted = useSearchParams().get("posted") === "1";
  const [job, setJob] = useState<Job | null>(null);
  const [apps, setApps] = useState<Application[] | null>(null);
  const [filter, setFilter] = useState<ApplicationStatus | "ALL">("ALL");
  const [error, setError] = useState("");
  const [confirmDelete, setConfirmDelete] = useState(false);
  const [busy, setBusy] = useState(false);

  const load = useCallback(async () => {
    try {
      const [j, a] = await Promise.all([api<Job>(`/api/jobs/${id}`), api<Application[]>(`/api/jobs/${id}/applications`)]);
      setJob(j);
      setApps(a);
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "Could not load this ad");
    }
  }, [id]);

  useEffect(() => {
    load();
  }, [load]);

  const toggleStatus = async () => {
    if (!job) return;
    setBusy(true);
    try {
      setJob(await api<Job>(`/api/jobs/${job.id}/status`, "PATCH", { status: job.status === "OPEN" ? "CLOSED" : "OPEN" }));
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "Could not update the ad");
    } finally {
      setBusy(false);
    }
  };

  const remove = async () => {
    if (!job) return;
    setBusy(true);
    try {
      await api(`/api/jobs/${job.id}`, "DELETE");
      router.replace("/my-ads");
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "Could not delete the ad");
      setBusy(false);
    }
  };

  if (error && !job)
    return (
      <div className="mx-auto max-w-4xl px-4 py-12 grid gap-4">
        <Alert>{error}</Alert>
        <Link href="/my-ads" className="font-semibold underline underline-offset-4">
          Back to my ads
        </Link>
      </div>
    );
  if (!job || !apps) return <div className="mx-auto max-w-4xl px-4 py-16 text-muted">Loading…</div>;

  const counts = Object.fromEntries(FILTERS.map((f) => [f, f === "ALL" ? apps.length : apps.filter((a) => a.status === f).length]));
  const shown = filter === "ALL" ? apps : apps.filter((a) => a.status === filter);

  return (
    <div className="mx-auto max-w-4xl px-4 py-8">
      <Link href="/my-ads" className="text-sm font-semibold underline underline-offset-4">
        My ads
      </Link>

      {posted && (
        <div className="mt-4">
          <Alert kind="success">
            Your ad is live. <Link href={`/jobs/${job.id}`} className="underline font-semibold">See it as workers do</Link>.
          </Alert>
        </div>
      )}
      {error && (
        <div className="mt-4">
          <Alert>{error}</Alert>
        </div>
      )}

      <div className="mt-4 rounded-md border border-line bg-surface p-5 grid gap-4">
        <div className="flex flex-wrap items-start justify-between gap-4">
          <div>
            <h1 className="sign text-4xl sm:text-5xl text-heading">{job.title}</h1>
            <p className="mt-1 text-muted">
              {job.location}, {formatPay(job.salaryAmount)} {PERIOD_LABEL[job.salaryPeriod]}, {job.openings}{" "}
              {job.openings === 1 ? "opening" : "openings"}
            </p>
          </div>
          <span className={`font-semibold rounded px-2 py-0.5 ${job.status === "OPEN" ? "bg-go-soft text-go" : "bg-line/60 text-muted"}`}>
            {job.status === "OPEN" ? "Open, taking applications" : "Closed"}
          </span>
        </div>
        <div className="flex flex-wrap gap-2">
          <LinkButton href={`/my-ads/${job.id}/edit`} variant="secondary">
            Edit ad
          </LinkButton>
          <Button variant="secondary" onClick={toggleStatus} disabled={busy}>
            {job.status === "OPEN" ? "Close ad" : "Reopen ad"}
          </Button>
          <LinkButton href={`/jobs/${job.id}`} variant="ghost">
            View public page
          </LinkButton>
          {!confirmDelete ? (
            <Button variant="danger" onClick={() => setConfirmDelete(true)} className="sm:ml-auto">
              Delete ad
            </Button>
          ) : (
            <div className="flex flex-wrap items-center gap-2 sm:ml-auto">
              <span className="text-sm">Delete this ad and its {apps.length} applications?</span>
              <Button variant="danger" onClick={remove} disabled={busy}>
                Yes, delete
              </Button>
              <Button variant="ghost" onClick={() => setConfirmDelete(false)}>
                Keep it
              </Button>
            </div>
          )}
        </div>
      </div>

      <h2 className="sign-mid text-3xl text-heading mt-10">Applicants</h2>
      <div className="mt-3 flex gap-2 overflow-x-auto pb-1" role="tablist" aria-label="Filter applicants">
        {FILTERS.map((f) => (
          <button
            key={f}
            role="tab"
            aria-selected={filter === f}
            onClick={() => setFilter(f)}
            className="whitespace-nowrap rounded-full border border-line px-3 py-1.5 text-sm font-semibold aria-selected:bg-brand aria-selected:text-on-brand aria-selected:border-brand"
          >
            {f === "ALL" ? "All" : statusLabel(f, "recruiter")} ({counts[f]})
          </button>
        ))}
      </div>

      <div className="mt-4 grid gap-3">
        {apps.length === 0 ? (
          <Empty title="No one has applied yet">
            <p>You&apos;ll get a notification as soon as someone applies.</p>
          </Empty>
        ) : shown.length === 0 ? (
          <p className="text-muted">No applicants in this group.</p>
        ) : (
          shown.map((a) => (
            <ApplicantCard key={a.id} app={a} onSaved={(u) => setApps((prev) => prev!.map((x) => (x.id === u.id ? u : x)))} />
          ))
        )}
      </div>
    </div>
  );
}

function toLocalInput(iso: string | null) {
  return iso ? iso.slice(0, 16) : "";
}

function ApplicantCard({ app, onSaved }: { app: Application; onSaved: (a: Application) => void }) {
  const a = app.applicant!;
  const [mode, setMode] = useState<"idle" | "interview" | "note">("idle");
  const [when, setWhen] = useState(toLocalInput(app.interviewAt));
  const [note, setNote] = useState(app.recruiterNote ?? "");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  const save = async (status: ApplicationStatus, extra: { interviewAt?: string | null } = {}) => {
    setBusy(true);
    setError("");
    try {
      const updated = await api<Application>(`/api/applications/${app.id}`, "PATCH", {
        status,
        interviewAt: extra.interviewAt ?? null,
        recruiterNote: note,
      });
      onSaved(updated);
      setMode("idle");
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "Could not update");
    } finally {
      setBusy(false);
    }
  };

  return (
    <article className="rounded-md border border-line bg-surface p-5 grid gap-3">
      <div className="flex flex-wrap items-start justify-between gap-3">
        <div>
          <h3 className="sign-mid text-2xl">{a.name}</h3>
          <p className="text-sm text-muted">Applied {timeAgo(app.createdAt)}</p>
        </div>
        <StatusBadge status={app.status} viewer="recruiter" />
      </div>

      <ul className="grid sm:grid-cols-3 gap-x-4 gap-y-1 text-[0.95rem]">
        <li>
          <a href={`tel:${a.phone.replace(/\s/g, "")}`} className="font-semibold underline underline-offset-4">
            {a.phone}
          </a>
        </li>
        <li className="break-all">
          <a href={`mailto:${a.email}`} className="underline underline-offset-4">
            {a.email}
          </a>
        </li>
        <li className="text-muted">{a.address}</li>
      </ul>

      {app.message && (
        <blockquote className="border-l-4 border-sign pl-3 whitespace-pre-line">{app.message}</blockquote>
      )}
      {a.skills && (
        <p className="text-[0.95rem]">
          <span className="font-semibold">Skills and experience: </span>
          {a.skills}
        </p>
      )}
      {app.status === "INTERVIEW" && app.interviewAt && (
        <p className="font-semibold">Interview: {formatDateTime(app.interviewAt)}</p>
      )}
      {app.recruiterNote && mode === "idle" && <p className="text-sm text-muted">Your note: {app.recruiterNote}</p>}

      {error && <Alert>{error}</Alert>}

      {mode === "interview" ? (
        <div className="grid gap-3 rounded-md bg-bg p-4">
          <Field label="Interview date and time" type="datetime-local" value={when} onChange={(e) => setWhen(e.target.value)} />
          <TextArea
            label="Note for the applicant (optional)"
            rows={2}
            placeholder="Where to come, what to bring"
            value={note}
            onChange={(e) => setNote(e.target.value)}
          />
          <div className="flex gap-2">
            <Button onClick={() => (when ? save("INTERVIEW", { interviewAt: when + ":00" }) : setError("Pick a date and time for the interview"))} disabled={busy}>
              Schedule interview
            </Button>
            <Button variant="ghost" onClick={() => setMode("idle")}>
              Cancel
            </Button>
          </div>
        </div>
      ) : mode === "note" ? (
        <div className="grid gap-3 rounded-md bg-bg p-4">
          <TextArea label="Note for the applicant" rows={2} value={note} onChange={(e) => setNote(e.target.value)} />
          <div className="flex gap-2">
            <Button onClick={() => save(app.status, { interviewAt: app.interviewAt })} disabled={busy}>
              Save note
            </Button>
            <Button variant="ghost" onClick={() => setMode("idle")}>
              Cancel
            </Button>
          </div>
        </div>
      ) : (
        <div className="flex flex-wrap gap-2 pt-1">
          {app.status !== "CALL_LATER" && (
            <Button variant="secondary" onClick={() => save("CALL_LATER")} disabled={busy}>
              Call later
            </Button>
          )}
          <Button variant="secondary" onClick={() => setMode("interview")} disabled={busy}>
            {app.status === "INTERVIEW" ? "Change interview" : "Schedule interview"}
          </Button>
          {app.status !== "HIRED" && (
            <Button onClick={() => save("HIRED")} disabled={busy}>
              Hire
            </Button>
          )}
          {app.status !== "REJECTED" && (
            <Button variant="danger" onClick={() => save("REJECTED")} disabled={busy}>
              Not selected
            </Button>
          )}
          <Button variant="ghost" onClick={() => setMode("note")} disabled={busy}>
            {app.recruiterNote ? "Edit note" : "Add note"}
          </Button>
        </div>
      )}
    </article>
  );
}

export default function ManageAdPage() {
  return (
    <RequireAuth>
      <Suspense>
        <ManageAd />
      </Suspense>
    </RequireAuth>
  );
}
