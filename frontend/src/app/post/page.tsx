"use client";

import { useRouter } from "next/navigation";
import { useAuth } from "@/components/AuthProvider";
import { JobForm, toJobBody } from "@/components/JobForm";
import { RequireAuth } from "@/components/RequireAuth";
import { PageTitle } from "@/components/ui";
import { api } from "@/lib/api";
import type { Job } from "@/lib/types";

function PostWork() {
  const { user } = useAuth();
  const router = useRouter();

  return (
    <div className="mx-auto max-w-3xl px-4 py-10">
      <PageTitle>Post work</PageTitle>
      <p className="-mt-3 mb-6 text-muted max-w-[60ch]">
        Fill in what you need. Your ad goes live right away and you&apos;ll get a notification for every applicant.
      </p>
      <JobForm
        initial={{
          title: "",
          category: "",
          location: user?.address ?? "",
          salaryAmount: "",
          salaryPeriod: "MONTH",
          description: "",
          requirements: "",
          startDate: "",
          openings: 1,
          contactPhone: user?.phone ?? "",
          contactEmail: user?.email ?? "",
        }}
        submitLabel="Post ad"
        busyLabel="Posting…"
        onSubmit={async (data) => {
          const job = await api<Job>("/api/jobs", "POST", toJobBody(data));
          router.push(`/my-ads/${job.id}?posted=1`);
        }}
      />
    </div>
  );
}

export default function PostPage() {
  return (
    <RequireAuth>
      <PostWork />
    </RequireAuth>
  );
}
