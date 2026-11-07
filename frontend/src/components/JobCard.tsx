import Link from "next/link";
import type { Job } from "@/lib/types";
import { startsLabel, timeAgo } from "@/lib/format";
import { PayTag, StatusBadge } from "./ui";

/** A job ad, laid out like a notice slip pinned on a board. */
export function JobCard({ job }: { job: Job }) {
  return (
    <article className="relative rounded-md bg-surface border border-line hover:border-muted/60 transition-colors">
      <div className="p-4 sm:p-5 flex flex-col sm:flex-row gap-4 sm:items-start">
        <div className="flex-1 min-w-0">
          <p className="text-sm text-muted">
            {job.categoryLabel}, posted {timeAgo(job.createdAt)}
          </p>
          <h2 className="sign-mid text-2xl text-heading mt-1">
            <Link href={`/jobs/${job.id}`} className="after:absolute after:inset-0 hover:underline underline-offset-4">
              {job.title}
            </Link>
          </h2>
          <p className="mt-1 font-medium">{job.location}</p>
          <p className="mt-2 text-muted line-clamp-2 max-w-[65ch]">{job.description}</p>
          <div className="mt-3 flex flex-wrap items-center gap-x-4 gap-y-1 text-sm">
            <span>{startsLabel(job.startDate)}</span>
            <span>
              {job.openings} {job.openings === 1 ? "opening" : "openings"}
            </span>
            <span className="text-muted">by {job.postedBy.name}</span>
            {job.myApplicationStatus && <StatusBadge status={job.myApplicationStatus} viewer="seeker" />}
            {job.owner && <span className="font-semibold text-info">Your ad</span>}
          </div>
        </div>
        <PayTag amount={job.salaryAmount} period={job.salaryPeriod} />
      </div>
    </article>
  );
}
