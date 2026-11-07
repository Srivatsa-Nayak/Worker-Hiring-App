"use client";

import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { useAuth } from "@/components/AuthProvider";
import { RequireAuth } from "@/components/RequireAuth";
import { Alert, Button, Empty, PageTitle } from "@/components/ui";
import { api, ApiError } from "@/lib/api";
import { timeAgo } from "@/lib/format";
import type { NotificationItem } from "@/lib/types";

function Notifications() {
  const { refreshUnread } = useAuth();
  const router = useRouter();
  const [items, setItems] = useState<NotificationItem[] | null>(null);
  const [error, setError] = useState("");

  useEffect(() => {
    api<{ unread: number; items: NotificationItem[] }>("/api/notifications")
      .then((r) => setItems(r.items))
      .catch((e) => setError(e instanceof ApiError ? e.message : "Could not load notifications"));
  }, []);

  const open = async (n: NotificationItem) => {
    if (!n.read) {
      await api(`/api/notifications/${n.id}/read`, "POST").catch(() => {});
      refreshUnread();
    }
    if (n.link) router.push(n.link);
    else setItems((prev) => prev!.map((x) => (x.id === n.id ? { ...x, read: true } : x)));
  };

  const readAll = async () => {
    await api("/api/notifications/read-all", "POST");
    setItems((prev) => prev!.map((x) => ({ ...x, read: true })));
    refreshUnread();
  };

  const hasUnread = items?.some((n) => !n.read);

  return (
    <div className="mx-auto max-w-3xl px-4 py-10">
      <PageTitle
        aside={
          hasUnread ? (
            <Button variant="secondary" onClick={readAll}>
              Mark all as read
            </Button>
          ) : undefined
        }
      >
        Notifications
      </PageTitle>
      {error && <Alert>{error}</Alert>}
      {!items && !error && <p className="text-muted">Loading…</p>}
      {items && items.length === 0 && (
        <Empty title="Nothing yet">
          <p>You&apos;ll hear here when someone applies to your ads or a recruiter responds to you.</p>
        </Empty>
      )}
      {items && items.length > 0 && (
        <ul className="divide-y divide-line rounded-md border border-line bg-surface">
          {items.map((n) => (
            <li key={n.id}>
              <button onClick={() => open(n)} className="w-full text-left p-4 flex gap-3 items-start hover:bg-bg/60">
                <span
                  aria-hidden="true"
                  className={`mt-2 w-2.5 h-2.5 rounded-full shrink-0 ${n.read ? "bg-transparent" : "bg-sign ring-2 ring-on-sign/30"}`}
                />
                <span className="flex-1">
                  <span className={n.read ? "" : "font-semibold"}>{n.message}</span>
                  <span className="sr-only">{n.read ? "" : " (unread)"}</span>
                  <span className="block text-sm text-muted mt-0.5">{timeAgo(n.createdAt)}</span>
                </span>
              </button>
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export default function NotificationsPage() {
  return (
    <RequireAuth>
      <Notifications />
    </RequireAuth>
  );
}
