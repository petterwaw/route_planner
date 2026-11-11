import { CircleAlert } from "lucide-react";
import { Alert, AlertDescription, AlertTitle } from "@/components/ui/alert";
import { Button } from "@/components/ui/button";

type ErrorStateProps = {
  title: string;
  message: string;
  onRetry?: () => void;
  retrying?: boolean;
};

/** Error box announced immediately to screen readers (role="alert" from Alert). */
export function ErrorState({
  title,
  message,
  onRetry,
  retrying = false,
}: ErrorStateProps) {
  return (
    <Alert variant="destructive">
      <CircleAlert aria-hidden="true" />
      <AlertTitle>{title}</AlertTitle>
      <AlertDescription>
        <p>{message}</p>
        {onRetry && (
          <Button
            variant="outline"
            size="lg"
            className="mt-3"
            onClick={onRetry}
            disabled={retrying}
          >
            {retrying ? "Trying again…" : "Try again"}
          </Button>
        )}
      </AlertDescription>
    </Alert>
  );
}
