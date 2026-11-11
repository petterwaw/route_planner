import { Settings } from "lucide-react";
import type { Metadata } from "next";
import { EmptyState } from "@/components/feedback/empty-state";

export const metadata: Metadata = { title: "Settings" };

// Stage 1 placeholder. Stage 2 adds Home, Places, Schedule and Data sources.
export default function SettingsPage() {
  return (
    <>
      <h1 className="mb-6 text-2xl font-semibold tracking-tight">Settings</h1>
      <EmptyState
        icon={<Settings />}
        title="Nothing here yet"
        description="Your home address, places and class schedule will be set up here."
      />
    </>
  );
}
