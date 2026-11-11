import { Spinner } from "@/components/ui/spinner";

/** Loading message announced politely to screen readers (role="status"). */
export function LoadingState({ label }: { label: string }) {
  return (
    <div
      role="status"
      className="flex items-center gap-2 py-4 text-sm text-muted-foreground"
    >
      <Spinner aria-hidden="true" />
      <span>{label}</span>
    </div>
  );
}
