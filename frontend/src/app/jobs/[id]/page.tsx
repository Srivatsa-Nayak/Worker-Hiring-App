"use client";

import Link from "next/link";
import { useParams } from "next/navigation";
import { useCallback, useEffect, useState } from "react";
import { useAuth } from "@/components/AuthProvider";
import { Alert, Button, LinkButton, PayTag, StatusBadge, TextArea } from "@/components/ui";
import { api, ApiError } from "@/lib/api";
import { formatDate, startsLabel, timeAgo } from "@/lib/format";
import type { Job } from "@/lib/types";

export default function JobDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { status } = useAuth();
  const [job, setJob] = useState<Job | null>(null);
  const [loadError, setLoadError] = useState("");

  const load = useCallback(() => {
    api<Job>(`/api/jobs/${id}`)
      .then(setJob)
      .catch((e) => setLoadError(e instanceof ApiError && e.status === 404 ? "This ad doesn't exist or was removed." : "Could not load this ad."));
  }, [id]);

  useEffect(() => {
    if (status !== "loading") load();
  }, [status, load]);

  if (loadError)
    return (
      <div className="mx-auto max-w-3xl px-4 py-12 grid gap-4">
        <Alert>{loadError}</Alert>
        <Link href="/" className="font-semibold underline underline-offset-4">
          See all open work
        </Link>
      </div>
    );
  if (!job) return <div className="mx-auto max-w-3xl px-4 py-16 text-muted">Loading…</div>;

  return (
    <div className="mx-auto max-w-3xl px-4 py-8">
      <Link href="/" className="text-sm font-semibold underline underline-offset-4">
        All work
      </Link>

      <article className="mt-4 rounded-md border border-line bg-surface">
        <div className="p-5 sm:p-7 border-b border-line flex flex-col sm:flex-row gap-5 sm:items-start sm:justify-between">
          <div>
            <p className="text-muted">
              {job.categoryLabel}, posted {timeAgo(job.createdAt)} by {job.postedBy.name}
            </p>
            <h1 className="sign text-5xl text-heading mt-2">{job.title}</h1>
            <p className="mt-2 text-lg font-medium">{job.location}</p>
            {job.status === "CLOSED" && (
              <p className="mt-3 inline-block rounded bg-stop-soft text-stop px-2 py-0.5 font-semibold">This ad is closed</p>
            )}
          </div>
          <PayTag amount={job.salaryAmount} period={job.salaryPeriod} size="lg" />
        </div>

        <dl className="grid grid-cols-2 sm:grid-cols-3 gap-4 p-5 sm:px-7 border-b border-line">
          <Fact label="Start date" value={`${formatDate(job.startDate)}`} sub={startsLabel(job.startDate)} />
          <Fact label="Openings" value={String(job.openings)} />
          <Fact label="Applicants so far" value={String(job.applicationCount)} />
        </dl>

        <div className="p-5 sm:p-7 grid gap-6">
          <Section title="About the work">{job.description}</Section>
          <Section title="Requirements">{job.requirements}</Section>
          <section>
            <h2 className="sign-mid text-xl text-heading">Contact</h2>
            {job.contactPhone ? (
              <ul className="mt-2 grid gap-1">
                <li>
                  Phone:{" "}
                  <a href={`tel:${job.contactPhone.replace(/\s/g, "")}`} className="font-semibold underline underline-offset-4">
                    {job.contactPhone}
                  </a>
                </li>
                <li>
                  Email:{" "}
                  <a href={`mailto:${job.contactEmail}`} className="font-semibold underline underline-offset-4 break-all">
                    {job.contactEmail}
                  </a>
                </li>
              </ul>
            ) : (
              <p className="mt-2 text-muted">
                <Link href={`/login?next=/jobs/${job.id}`} className="font-semibold text-text underline underline-offset-4">
                  Log in
                </Link>{" "}
                to see the phone number and email.
              </p>
            )}
          </section>
        </div>
      </article>

      <div className="mt-6">
        <ApplyPanel job={job} onChange={load} />
      </div>
    </div>
  );
}

function ApplyPanel({ job, onChange }: { job: Job; onChange: () => void }) {
  const { status, refreshUnread } = useAuth();
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);
  const [justApplied, setJustApplied] = useState(false);

  if (job.owner) {
    return (
      <div className="rounded-md border border-line bg-surface p-5 flex flex-wrap items-center justify-between gap-3">
        <p>This is your ad. {job.applicationCount === 1 ? "1 person has" : `${job.applicationCount} people have`} applied.</p>
        <LinkButton href={`/my-ads/${job.id}`}>See applicants</LinkButton>
      </div>
    );
  }

  if (job.myApplicationStatus) {
    return (
      <div className="rounded-md border border-line bg-surface p-5 grid gap-3">
        {justApplied && <Alert kind="success">Application sent. You&apos;ll get a notification when the recruiter responds.</Alert>}
        <div className="flex flex-wrap items-center justify-between gap-3">
          <p className="flex items-center gap-2">
            Your application: <StatusBadge status={job.myApplicationStatus} viewer="seeker" />
          </p>
          <LinkButton href="/applications" variant="secondary">
            Go to my applications
          </LinkButton>
        </div>
      </div>
    );
  }

  if (job.status === "CLOSED") return null;

  if (status !== "authed") {
    return (
      <div className="rounded-md bg-sign text-on-sign p-5 flex flex-wrap items-center justify-between gap-3">
        <p className="font-semibold">Log in or sign up to apply for this work.</p>
        <div className="flex gap-2">
          <Link href={`/login?next=/jobs/${job.id}`} className="rounded-md bg-[#1f2a6b] text-white font-semibold px-4 py-2.5">
            Log in
          </Link>
          <Link href={`/register?next=/jobs/${job.id}`} className="rounded-md border-2 border-[#1f2a6b] font-semibold px-4 py-2">
            Sign up
          </Link>
        </div>
      </div>
    );
  }

  const apply = async (e: React.FormEvent) => {
    e.preventDefault();
    setBusy(true);
    setError("");
    try {
      await api(`/api/jobs/${job.id}/applications`, "POST", { message });
      setJustApplied(true);
      refreshUnread();
      onChange();
    } catch (err) {
      setError(err instanceof ApiError ? err.message : "Could not send the application");
    } finally {
      setBusy(false);
    }
  };

  return (
    <form onSubmit={apply} className="rounded-md border-2 border-brand bg-surface p-5 grid gap-4">
      <h2 className="sign-mid text-2xl text-heading">Apply for this work</h2>
      {error && <Alert>{error}</Alert>}
      <TextArea
        label="Message to the recruiter (optional)"
        placeholder="When can you start? Any experience with this kind of work?"
        value={message}
        onChange={(e) => setMessage(e.target.value)}
        rows={3}
        hint="Your name, phone, address and the skills from your profile are shared with the recruiter."
      />
      <div>
        <Button type="submit" disabled={busy}>
          {busy ? "Sending…" : "Send application"}
        </Button>
      </div>
    </form>
  );
}

function Fact({ label, value, sub }: { label: string; value: string; sub?: string }) {
  return (
    <div>
      <dt className="text-sm text-muted">{label}</dt>
      <dd className="sign-mid text-xl">{value}</dd>
      {sub && <dd className="text-sm text-muted">{sub}</dd>}
    </div>
  );
}

function Section({ title, children }: { title: string; children: React.ReactNode }) {
  return (
    <section>
      <h2 className="sign-mid text-xl text-heading">{title}</h2>
      <p className="mt-2 whitespace-pre-line max-w-[68ch]">{children}</p>
    </section>
  );
}
