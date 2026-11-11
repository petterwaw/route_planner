"use client";

import { CircleCheck } from "lucide-react";
import { ErrorState } from "@/components/feedback/error-state";
import { LoadingState } from "@/components/feedback/loading-state";
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from "@/components/ui/card";
import { ApiError } from "@/lib/api/client";
import { useHealth } from "@/lib/api/queries";
import { formatTime } from "@/lib/time";

function describeProvider(provider: string): string {
  return provider === "mock" ? "Test data (mock)" : `Live (${provider})`;
}

/** Backend status from `GET /api/health`: loading, error with retry, or the providers in use. */
export function BackendStatus() {
  const health = useHealth();

  if (health.isPending) {
    return <LoadingState label="Checking the server…" />;
  }

  if (health.isError) {
    const message =
      health.error instanceof ApiError
        ? health.error.detail
        : "Something went wrong. Please try again.";
    return (
      <ErrorState
        title="Server unavailable"
        message={message}
        onRetry={() => void health.refetch()}
        retrying={health.isFetching}
      />
    );
  }

  const { now, routingProvider, aiProvider } = health.data;
  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex items-center gap-2">
          <CircleCheck aria-hidden="true" className="size-4 text-emerald-600" />
          Server is running
        </CardTitle>
        <CardDescription>Server time: {formatTime(now)}</CardDescription>
      </CardHeader>
      <CardContent>
        <dl className="grid grid-cols-[auto_1fr] gap-x-4 gap-y-1 text-sm">
          <dt className="text-muted-foreground">Routes</dt>
          <dd>{describeProvider(routingProvider)}</dd>
          <dt className="text-muted-foreground">Schedule import</dt>
          <dd>{describeProvider(aiProvider)}</dd>
        </dl>
      </CardContent>
    </Card>
  );
}
