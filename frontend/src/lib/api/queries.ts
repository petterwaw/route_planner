// TanStack Query hooks, one per backend resource.
import { useQuery } from "@tanstack/react-query";
import { apiGet } from "./client";
import type { HealthResponse } from "./types";

export const queryKeys = {
  health: ["health"] as const,
};

export function useHealth() {
  return useQuery({
    queryKey: queryKeys.health,
    queryFn: () => apiGet<HealthResponse>("/api/health"),
  });
}
