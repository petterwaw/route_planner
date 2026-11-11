// Server time (docs/architecture.md, section 5). The device clock can be wrong, so every API
// response with a `now` field stores the offset between server and device time, and "now"
// in the app is always the device time plus that offset.

export const APP_TIME_ZONE = "Europe/Warsaw";

let offsetMs = 0;

/** Stores the offset from a server `now` (ISO 8601). Invalid values are ignored. */
export function syncServerClock(
  serverNow: string,
  deviceNowMs: number = Date.now(),
): void {
  const serverMs = Date.parse(serverNow);
  if (!Number.isNaN(serverMs)) {
    offsetMs = serverMs - deviceNowMs;
  }
}

/** Current server time in epoch milliseconds. */
export function serverNow(deviceNowMs: number = Date.now()): number {
  return deviceNowMs + offsetMs;
}

const timeFormat = new Intl.DateTimeFormat("en-GB", {
  timeZone: APP_TIME_ZONE,
  hour: "2-digit",
  minute: "2-digit",
  hourCycle: "h23",
});

/** "07:21": 24-hour time in Europe/Warsaw, whatever the device time zone is. */
export function formatTime(instant: string | number): string {
  return timeFormat.format(
    typeof instant === "string" ? new Date(instant) : instant,
  );
}
