"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { RequireAuth } from "@/components/RequireAuth";
import { Alert, Button, Empty, LinkButton, PageTitle, StatusBadge } from "@/components/ui";
import { api, ApiError } from "@/lib/api";
import { formatDate, formatDateTime, formatPay, PERIOD_LABEL, timeAgo } from "@/lib/format";
import type { Application } from "@/lib/types";

function MyApplications() {
  const [apps, setApps] = useState<Application[] | null>(null);
  const [error, setError] = useState("");
  const [confirming, setConfirming] = useState<number | null>(null);

  useEffect(() => {
    api<Application[]>("/api/applications/mine")
      .then(setApps)
      .catch((e) => setError(e instanceof ApiError ? e.message : "Could not load your applications"));
  }, []);

  const withdraw = async (id: number) => {
    try {
      await api(`/api/applications/${id}`, "DELETE");
      setApps((prev) => prev!.filter((a) => a.id !== id));
      setConfirming(null);
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "Could not withdraw");
    }
  };

  return (
    <div className="mx-auto max-w-4xl px-4 py-10">
      <PageTitle>My applications</PageTitle>
      {error && <Alert>{error}</Alert>}
      {!apps && !error && <p className="text-muted">Loading…</p>}
      {apps && apps.length === 0 && (
        <Empty title="You haven't applied for any work yet">
          <p className="mb-4">Browse open work and apply in one step.</p>
          <LinkButton href="/">Find work</LinkButton>
        </Empty>
      )}
      <div className="grid gap-3">
        {apps?.map((a) => {
          const j = a.job!;
          return (
            <article key={a.id} className="rounded-md border border-line bg-surface p-5 grid gap-2">
              <div className="flex flex-wrap items-start justify-between gap-3">
                <div>
                  <Link href={`/jobs/${j.id}`} className="sign-mid text-2xl text-heading hover:underline underline-offset-4">
                    {j.title}
                  </Link>
                  <p className="text-sm text-muted">
                    {j.postedByName}, {j.location}, {formatPay(j.salaryAmount)} {PERIOD_LABEL[j.salaryPeriod]}
                  </p>
                </div>
                <StatusBadge status={a.status} viewer="seeker" />
              </div>

              {a.status === "INTERVIEW" && a.interviewAt && (
                <p className="rounded bg-sign text-on-sign px-3 py-2 font-semibold">Interview on {formatDateTime(a.interviewAt)}</p>
              )}
              {a.status === "HIRED" && (
                <p className="rounded bg-go-soft text-go px-3 py-2 font-semibold">
                  You&apos;re hired. Work starts {formatDate(j.startDate)}.
                </p>
              )}
              {a.recruiterNote && (
                <p>
                  <span className="font-semibold">Note from {j.postedByName}: </span>
                  {a.recruiterNote}
                </p>
              )}
              {(a.status === "INTERVIEW" || a.status === "HIRED" || a.status === "CALL_LATER") && (
                <p className="text-[0.95rem]">
                  Contact:{" "}
                  <a href={`tel:${j.contactPhone.replace(/\s/g, "")}`} className="font-semibold underline underline-offset-4">
                    {j.contactPhone}
                  </a>
                </p>
              )}
              <div className="flex flex-wrap items-center justify-between gap-2 text-sm text-muted">
                <span>
                  Applied {timeAgo(a.createdAt)}
                  {a.updatedAt !== a.createdAt && `, updated ${timeAgo(a.updatedAt)}`}
                  {j.status === "CLOSED" && ", ad now closed"}
                </span>
                {a.status !== "HIRED" &&
                  (confirming === a.id ? (
                    <span className="flex items-center gap-2 text-text">
                      Withdraw this application?
                      <Button variant="danger" className="min-h-9 px-3" onClick={() => withdraw(a.id)}>
                        Withdraw
                      </Button>
                      <Button variant="ghost" className="min-h-9 px-3" onClick={() => setConfirming(null)}>
                        Keep
                      </Button>
                    </span>
                  ) : (
                    <button onClick={() => setConfirming(a.id)} className="font-semibold text-text underline underline-offset-4">
                      Withdraw
                    </button>
                  ))}
              </div>
            </article>
          );
        })}
      </div>
    </div>
  );
}

export default function ApplicationsPage() {
  return (
    <RequireAuth>
      <MyApplications />
    </RequireAuth>
  );
}
