import Link from "next/link";

export default function NotFound() {
  return (
    <div className="mx-auto max-w-3xl px-4 py-16">
      <h1 className="sign text-5xl text-heading">Page not found</h1>
      <p className="mt-3 text-muted">The link may be old, or the ad may have been removed.</p>
      <p className="mt-6">
        <Link href="/" className="font-semibold underline underline-offset-4">
          See open work
        </Link>
      </p>
    </div>
  );
}
