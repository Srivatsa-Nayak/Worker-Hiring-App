"use client";

import { useParams, useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { JobForm, toJobBody } from "@/components/JobForm";
import { RequireAuth } from "@/components/RequireAuth";
import { Alert, PageTitle } from "@/components/ui";
import { api, ApiError } from "@/lib/api";
import type { Job, JobInput } from "@/lib/types";

function EditAd() {
  const { id } = useParams<{ id: string }>();
  const router = useRouter();
  const [initial, setInitial] = useState<JobInput | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    api<Job>(`/api/jobs/${id}`)
      .then((j) => {
        if (!j.owner) {
          setError("Only the person who posted this ad can edit it.");
          return;
        }
        setInitial({
          title: j.title,
          category: j.category,
          location: j.location,
          salaryAmount: j.salaryAmount,
          salaryPeriod: j.salaryPeriod,
          description: j.description,
          requirements: j.requirements,
          startDate: j.startDate,
          openings: j.openings,
          contactPhone: j.contactPhone ?? "",
          contactEmail: j.contactEmail ?? "",
        });
      })
      .catch((e) => setError(e instanceof ApiError ? e.message : "Could not load this ad"));
  }, [id]);

  return (
    <div className="mx-auto max-w-3xl px-4 py-10">
      <PageTitle>Edit ad</PageTitle>
      {error && <Alert>{error}</Alert>}
      {!initial && !error && <p className="text-muted">Loading…</p>}
      {initial && (
        <JobForm
          initial={initial}
          submitLabel="Save changes"
          busyLabel="Saving…"
          onCancel={() => router.back()}
          onSubmit={async (data) => {
            await api<Job>(`/api/jobs/${id}`, "PUT", toJobBody(data));
            router.push(`/my-ads/${id}`);
          }}
        />
      )}
    </div>
  );
}

export default function EditAdPage() {
  return (
    <RequireAuth>
      <EditAd />
    </RequireAuth>
  );
}
