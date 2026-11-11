// The only place that talks to the backend. Paths are relative (/api/...); next.config.ts
// proxies them to the backend.
import { syncServerClock } from "@/lib/time";
import type { ErrorCode, FieldError, ProblemDetail } from "./types";

/** Backend down, proxy error or a response that is not problem+json. */
export const UNAVAILABLE = "UNAVAILABLE";

const UNAVAILABLE_MESSAGE =
  "Cannot reach the server. Check that the backend is running and try again.";
const UNEXPECTED_MESSAGE =
  "The server sent an unexpected response. Please try again.";

/** An error the UI can show as is: `detail` is a user-facing English sentence. */
export class ApiError extends Error {
  readonly code: ErrorCode | typeof UNAVAILABLE;
  readonly detail: string;
  readonly status: number | null;
  readonly errors: FieldError[];

  constructor(
    code: ErrorCode | typeof UNAVAILABLE,
    detail: string,
    status: number | null,
    errors: FieldError[] = [],
  ) {
    super(detail);
    this.name = "ApiError";
    this.code = code;
    this.detail = detail;
    this.status = status;
    this.errors = errors;
  }
}

export async function apiGet<T>(path: string): Promise<T> {
  let response: Response;
  try {
    response = await fetch(path, { headers: { Accept: "application/json" } });
  } catch {
    throw new ApiError(UNAVAILABLE, UNAVAILABLE_MESSAGE, null);
  }
  if (!response.ok) {
    throw await toApiError(response);
  }
  let body: unknown;
  try {
    body = await response.json();
  } catch {
    throw new ApiError(UNAVAILABLE, UNEXPECTED_MESSAGE, response.status);
  }
  if (hasServerTime(body)) {
    syncServerClock(body.now);
  }
  return body as T;
}

async function toApiError(response: Response): Promise<ApiError> {
  const contentType = response.headers.get("content-type") ?? "";
  if (contentType.includes("application/problem+json")) {
    try {
      const problem = (await response.json()) as ProblemDetail;
      return new ApiError(
        problem.code,
        problem.detail,
        response.status,
        problem.errors ?? [],
      );
    } catch {
      // Broken body: fall through to the generic error.
    }
  }
  // The Next.js proxy answers a plain-text 500 when the backend is not running.
  return new ApiError(UNAVAILABLE, UNAVAILABLE_MESSAGE, response.status);
}

function hasServerTime(body: unknown): body is { now: string } {
  return (
    typeof body === "object" &&
    body !== null &&
    typeof (body as { now?: unknown }).now === "string"
  );
}
