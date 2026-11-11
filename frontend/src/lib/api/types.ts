// Mirrors docs/api.md one to one. Change both in the same commit.

export type RoutingProvider = "mock" | "google";
export type AiProvider = "mock" | "openai";

/** `GET /api/health` */
export type HealthResponse = {
  status: "ok";
  /** Server time, ISO 8601 with the Europe/Warsaw offset. */
  now: string;
  routingProvider: RoutingProvider;
  aiProvider: AiProvider;
};

/** Values of `code` in error responses (docs/api.md, "Błędy"). */
export type ErrorCode =
  | "VALIDATION_ERROR"
  | "DATE_OUT_OF_RANGE"
  | "NOT_FOUND"
  | "LEG_NOT_FOUND"
  | "PLACE_NAME_TAKEN"
  | "PLACE_IN_USE"
  | "NOT_TODAY"
  | "OPTION_NOT_FOUND"
  | "FILE_INVALID"
  | "FILE_TOO_LARGE"
  | "AI_NO_RESULT"
  | "AI_LIMIT"
  | "AI_PROVIDER_ERROR"
  | "INTERNAL_ERROR";

/** One entry of `errors` in a validation error; `index` only for `POST /api/events/bulk`. */
export type FieldError = {
  field: string;
  message: string;
  index?: number;
};

/** Body of every error response (`application/problem+json`). */
export type ProblemDetail = {
  type?: string;
  title?: string;
  status: number;
  detail: string;
  instance?: string;
  code: ErrorCode;
  errors?: FieldError[];
};
