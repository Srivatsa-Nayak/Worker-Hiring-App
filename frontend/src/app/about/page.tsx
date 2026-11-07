import Link from "next/link";

const steps = [
  { title: "Sign up", body: "One account works for both sides. Add your phone, address and skills." },
  { title: "Post or browse", body: "Shop owners post the work they need. Workers search by kind of work and area." },
  { title: "Apply", body: "Workers apply in one step with an optional message. The shop owner is notified." },
  { title: "Hear back", body: "The shop owner marks each applicant call later, interview, hired or not selected, and the worker sees it straight away." },
];

export default function AboutPage() {
  return (
    <div className="mx-auto max-w-3xl px-4 py-10">
      <h1 className="sign text-5xl text-heading">About Worker Hiring</h1>
      <div className="mt-5 grid gap-4 text-lg max-w-[62ch]">
        <p>
          Small shops often hire by word of mouth or a paper notice in the window. Worker Hiring puts those notices online, so
          a bakery that needs a cashier and a person looking for counter work can find each other quickly, with no agent in
          between.
        </p>
        <p>
          It started as a college mini project (an Android app built at Sahyadri College of Engineering &amp; Management,
          Mangaluru) and has been rebuilt as a web app.
        </p>
      </div>

      <h2 className="sign-mid text-3xl text-heading mt-10">How it works</h2>
      <ol className="mt-4 grid gap-3">
        {steps.map((s, i) => (
          <li key={s.title} className="flex gap-4 rounded-md border border-line bg-surface p-4">
            <span className="sign text-4xl text-on-sign-soft bg-sign-soft rounded-sm w-12 h-12 grid place-items-center shrink-0">{i + 1}</span>
            <div>
              <h3 className="font-semibold text-lg">{s.title}</h3>
              <p className="text-muted">{s.body}</p>
            </div>
          </li>
        ))}
      </ol>

      <p className="mt-8">
        <Link href="/" className="font-semibold underline underline-offset-4">
          See open work
        </Link>
      </p>
    </div>
  );
}
