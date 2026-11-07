"use client";

import { useEffect } from "react";
import { usePathname, useRouter } from "next/navigation";
import { useAuth } from "./AuthProvider";

/** Renders children only for signed-in users; otherwise sends them to login and back. */
export function RequireAuth({ children }: { children: React.ReactNode }) {
  const { status } = useAuth();
  const router = useRouter();
  const pathname = usePathname();

  useEffect(() => {
    if (status === "anon") router.replace(`/login?next=${encodeURIComponent(pathname)}`);
  }, [status, router, pathname]);

  if (status !== "authed") {
    return <div className="mx-auto max-w-5xl px-4 py-16 text-muted">Loading…</div>;
  }
  return <>{children}</>;
}
