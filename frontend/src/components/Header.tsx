"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useAuth } from "./AuthProvider";

const signedInLinks = [
  { href: "/", label: "Find work" },
  { href: "/post", label: "Post work" },
  { href: "/my-ads", label: "My ads" },
  { href: "/applications", label: "Applications" },
];

export function Header() {
  const { status, user, unread } = useAuth();
  const pathname = usePathname();

  const isActive = (href: string) => (href === "/" ? pathname === "/" || pathname.startsWith("/jobs") : pathname.startsWith(href));

  const navLink = (href: string, label: string) => (
    <Link
      key={href}
      href={href}
      aria-current={isActive(href) ? "page" : undefined}
      className="whitespace-nowrap rounded px-2.5 py-1.5 text-[0.95rem] font-medium text-on-header hover:text-white hover:bg-white/10 aria-[current=page]:text-sign aria-[current=page]:font-semibold"
    >
      {label}
    </Link>
  );

  return (
    <header className="bg-header">
      <div className="mx-auto max-w-5xl px-4 py-3 flex flex-wrap items-center gap-x-4 gap-y-2">
        <Link href="/" className="sign text-[1.9rem] text-sign leading-none mr-auto" aria-label="Worker Hiring, home">
          Worker Hiring
        </Link>

        {status === "authed" && user ? (
          <div className="flex items-center gap-1 order-2 sm:order-3">
            <Link
              href="/notifications"
              className="relative rounded p-2 text-on-header hover:bg-white/10"
              aria-label={unread ? `Notifications, ${unread} unread` : "Notifications"}
            >
              <BellIcon />
              {unread > 0 && (
                <span className="absolute -top-0.5 -right-0.5 min-w-5 h-5 px-1 rounded-full bg-sign text-on-sign text-xs font-bold grid place-items-center">
                  {unread > 9 ? "9+" : unread}
                </span>
              )}
            </Link>
            <Link
              href="/profile"
              className="rounded-full w-9 h-9 grid place-items-center bg-sign text-on-sign font-bold text-sm"
              aria-label="Your profile"
              title={`${user.firstName} ${user.lastName}`}
            >
              {user.firstName[0]}
              {user.lastName[0]}
            </Link>
          </div>
        ) : status === "anon" ? (
          <div className="flex items-center gap-2 order-2 sm:order-3">
            <Link href="/login" className="rounded px-3 py-1.5 font-medium text-on-header hover:bg-white/10">
              Log in
            </Link>
            <Link href="/register" className="rounded px-3 py-1.5 font-semibold bg-sign text-on-sign hover:brightness-105">
              Sign up
            </Link>
          </div>
        ) : null}

        {status === "authed" && (
          <nav aria-label="Main" className="order-3 sm:order-2 w-full sm:w-auto -mx-2.5 sm:mx-0 flex gap-1 overflow-x-auto">
            {signedInLinks.map((l) => navLink(l.href, l.label))}
          </nav>
        )}
      </div>
    </header>
  );
}

function BellIcon() {
  return (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" aria-hidden="true">
      <path d="M6 8a6 6 0 1 1 12 0c0 7 3 9 3 9H3s3-2 3-9" strokeLinecap="round" strokeLinejoin="round" />
      <path d="M10.3 21a1.94 1.94 0 0 0 3.4 0" strokeLinecap="round" />
    </svg>
  );
}
