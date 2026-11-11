"use client";

import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import type { ReactNode } from "react";

function makeQueryClient() {
  return new QueryClient({
    defaultOptions: {
      // One quick retry, so "server unavailable" shows up in about a second, not after ~7 s.
      queries: { retry: 1 },
    },
  });
}

// Pattern from the Next.js 16 docs (guides/client-side-data-fetching/tanstack-query):
// a fresh client per server render, one shared client in the browser.
let browserQueryClient: QueryClient | undefined;

function getQueryClient() {
  if (typeof window === "undefined") {
    return makeQueryClient();
  }
  browserQueryClient ??= makeQueryClient();
  return browserQueryClient;
}

export function Providers({ children }: { children: ReactNode }) {
  return (
    <QueryClientProvider client={getQueryClient()}>
      {children}
    </QueryClientProvider>
  );
}
