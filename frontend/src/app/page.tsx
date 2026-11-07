"use client";

import Link from "next/link";
import { useCallback, useEffect, useState } from "react";
import { api, ApiError } from "@/lib/api";
import type { CategoryOption, Job } from "@/lib/types";
import { useAuth } from "@/components/AuthProvider";
import { JobCard } from "@/components/JobCard";
import { Alert, Empty } from "@/components/ui";

export default function HomePage() {
  const { status } = useAuth();
  const [categories, setCategories] = useState<CategoryOption[]>([]);
  const [q, setQ] = useState("");
  const [location, setLocation] = useState("");
  const [category, setCategory] = useState("");
  const [jobs, setJobs] = useState<Job[] | null>(null);
  const [error, setError] = useState("");

  const load = useCallback(async (filters: { q: string; location: string; category: string }) => {
    const params = new URLSearchParams();
    if (filters.q.trim()) params.set("q", filters.q.trim());
    if (filters.location.trim()) params.set("location", filters.location.trim());
    if (filters.category) params.set("category", filters.category);
    try {
      setError("");
      setJobs(await api<Job[]>(`/api/jobs?${params}`));
    } catch (e) {
      setError(e instanceof ApiError ? e.message : "Could not load jobs");
      setJobs([]);
    }
  }, []);

  useEffect(() => {
    api<CategoryOption[]>("/api/jobs/categories").then(setCategories).catch(() => {});
  }, []);

  // Reload when auth settles so contact info and "applied" badges match the viewer.
  useEffect(() => {
    if (status !== "loading") load({ q, location, category });
  }, [status, load]);

  const onSearch = (e: React.FormEvent) => {
    e.preventDefault();
    load({ q, location, category });
  };

  const clear = () => {
    setQ("");
    setLocation("");
    setCategory("");
    load({ q: "", location: "", category: "" });
  };

  const filtered = q || location || category;
  const fieldClass =
    "w-full min-h-12 rounded-md border-2 border-on-sign/80 bg-white text-[#1b1f33] px-3 placeholder:text-[#5b6178] focus:outline-none focus:ring-4 focus:ring-[#1f2a6b]/30";

  return (
    <>
      <section className="bg-sign text-on-sign border-b-4 border-on-sign">
        <div className="mx-auto max-w-5xl px-4 pt-10 pb-8 sm:pt-14">
          <h1 className="hang sign text-[3.4rem] sm:text-[5.5rem] max-w-[12ch]">Work at shops near you</h1>
          <p className="mt-4 text-lg max-w-[52ch] font-medium">
            Shop owners post the help they need. Workers apply in one step and hear back here.
          </p>

          <form onSubmit={onSearch} className="mt-7 grid gap-3 sm:grid-cols-[1.4fr_1fr_1fr_auto]" role="search">
            <label className="block">
              <span className="sr-only">What work</span>
              <input value={q} onChange={(e) => setQ(e.target.value)} placeholder="What work? e.g. cashier" className={fieldClass} />
            </label>
            <label className="block">
              <span className="sr-only">Where</span>
              <input value={location} onChange={(e) => setLocation(e.target.value)} placeholder="Where? e.g. Hampankatta" className={fieldClass} />
            </label>
            <label className="block">
              <span className="sr-only">Category</span>
              <select value={category} onChange={(e) => setCategory(e.target.value)} className={fieldClass}>
                <option value="">All kinds of work</option>
                {categories.map((c) => (
                  <option key={c.value} value={c.value}>
                    {c.label}
                  </option>
                ))}
              </select>
            </label>
            <button className="min-h-12 rounded-md bg-[#1f2a6b] text-white font-semibold px-6 hover:bg-[#2b3990]">Search</button>
          </form>

          {status === "anon" && (
            <p className="mt-5 font-medium">
              Need a helper for your shop?{" "}
              <Link href="/register?next=/post" className="underline underline-offset-4 font-semibold">
                Post work for free
              </Link>
            </p>
          )}
        </div>
      </section>

      <section className="mx-auto max-w-5xl px-4 py-8" aria-live="polite">
        <div className="flex flex-wrap items-baseline justify-between gap-2 mb-4">
          <h2 className="sign-mid text-2xl text-heading">
            {jobs === null ? "Loading work…" : `${jobs.length} open ${jobs.length === 1 ? "job" : "jobs"}${filtered ? " found" : ""}`}
          </h2>
          {filtered && (
            <button onClick={clear} className="text-sm font-semibold underline underline-offset-4">
              Clear search
            </button>
          )}
        </div>

        {error && <Alert>{error}</Alert>}

        {jobs && jobs.length === 0 && !error && (
          <Empty title={filtered ? "No work matches that search" : "No work posted yet"}>
            {filtered ? (
              <p>Try a different word or area, or clear the search to see everything.</p>
            ) : (
              <p>
                Be the first: <Link href="/post" className="underline font-semibold">post work</Link>.
              </p>
            )}
          </Empty>
        )}

        <div className="grid gap-3">
          {jobs?.map((j) => (
            <JobCard key={j.id} job={j} />
          ))}
        </div>
      </section>
    </>
  );
}
