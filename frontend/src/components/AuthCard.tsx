/** Shared frame for the login and sign-up pages. */
export function AuthCard({ title, intro, children }: { title: string; intro: string; children: React.ReactNode }) {
  return (
    <div className="mx-auto max-w-md px-4 py-10 sm:py-14">
      <h1 className="sign text-5xl text-heading">{title}</h1>
      <p className="mt-3 text-muted">{intro}</p>
      <div className="mt-6 rounded-md border border-line bg-surface p-5 sm:p-6">{children}</div>
    </div>
  );
}

/** Only allow redirects to in-app paths. */
export function safeNext(next: string | null) {
  return next && next.startsWith("/") && !next.startsWith("//") ? next : "/";
}
