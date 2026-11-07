"use client";

import Link from "next/link";
import { useEffect, useState } from "react";
import { Alert, Empty, LinkButton } from "@/components/ui";
import { api, ApiError } from "@/lib/api";
import { formatPay, PERIOD_LABEL, timeAgo } from "@/lib/format";
import type { Job } from "@/lib/types";

export function MyAdsList({ compact = false }: { compact?: boolean }) {
  const [jobs, setJobs] = useState<Job[] | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    api<Job[]>("/api/jobs/mine")
      .then(setJobs)
      .catch((e) => setError(e instanceof ApiError ? e.message : "Could not load your ads"));
  }, []);

  if (error) return <Alert>{error}</Alert>;
  if (!jobs) return <p className="text-muted">Loading…</p>;
  if (jobs.length === 0)
    return (
      <Empty title="You haven't posted any work">
        <p className="mb-4">Post an ad and workers nearby can apply straight away.</p>
        <LinkButton href="/post">Post work</LinkButton>
      </Empty>
    );

  const shown = compact ? jobs.slice(0, 3) : jobs;
  return (
    <div className="grid gap-3">
      <ul className="divide-y divide-line rounded-md border border-line bg-surface">
        {shown.map((j) => (
          <li key={j.id} className="relative p-4 sm:px-5 flex flex-wrap items-center gap-x-6 gap-y-2 hover:bg-bg/60">
            <div className="flex-1 min-w-[14rem]">
              <Link href={`/my-ads/${j.id}`} className="sign-mid text-xl text-heading after:absolute after:inset-0 hover:underline underline-offset-4">
                {j.title}
              </Link>
              <p className="text-sm text-muted">
                {j.location}, {formatPay(j.salaryAmount)} {PERIOD_LABEL[j.salaryPeriod]}, posted {timeAgo(j.createdAt)}
              </p>
            </div>
            <span
              className={`text-sm font-semibold rounded px-2 py-0.5 ${j.status === "OPEN" ? "bg-go-soft text-go" : "bg-line/60 text-muted"}`}
            >
              {j.status === "OPEN" ? "Open" : "Closed"}
            </span>
            <span className="sm:text-right sm:min-w-[7rem]">
              <span className="sign-mid text-2xl">{j.applicationCount}</span>{" "}
              <span className="text-sm text-muted">{j.applicationCount === 1 ? "applicant" : "applicants"}</span>
            </span>
          </li>
        ))}
      </ul>
      {compact && jobs.length > 3 && (
        <Link href="/my-ads" className="font-semibold underline underline-offset-4">
          See all {jobs.length} ads
        </Link>
      )}
    </div>
  );
}
