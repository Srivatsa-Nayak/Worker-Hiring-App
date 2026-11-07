"use client";

import { MyAdsList } from "@/components/MyAdsList";
import { RequireAuth } from "@/components/RequireAuth";
import { LinkButton, PageTitle } from "@/components/ui";

export default function MyAdsPage() {
  return (
    <RequireAuth>
      <div className="mx-auto max-w-5xl px-4 py-10">
        <PageTitle aside={<LinkButton href="/post">Post work</LinkButton>}>My ads</PageTitle>
        <MyAdsList />
      </div>
    </RequireAuth>
  );
}
