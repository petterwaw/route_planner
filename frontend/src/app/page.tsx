import type { Metadata } from "next";
import { BackendStatus } from "@/components/health/backend-status";

export const metadata: Metadata = { title: "Today" };

// Stage 1 placeholder for the Today screen: app name and backend status.
export default function TodayPage() {
  return (
    <>
      <header className="mb-6">
        <h1 className="text-2xl font-semibold tracking-tight">Zdążę</h1>
        <p className="text-sm text-muted-foreground">
          When to leave and how to get there.
        </p>
      </header>
      <BackendStatus />
    </>
  );
}
