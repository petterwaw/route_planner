"use client";

import { CalendarDays, Settings } from "lucide-react";
import Link from "next/link";
import { usePathname } from "next/navigation";
import { cn } from "@/lib/utils";

const tabs = [
  {
    href: "/",
    label: "Today",
    icon: CalendarDays,
    matches: (path: string) => path === "/",
  },
  {
    href: "/settings",
    label: "Settings",
    icon: Settings,
    // Sub-pages such as /settings/import keep the Settings tab active.
    matches: (path: string) =>
      path === "/settings" || path.startsWith("/settings/"),
  },
];

export function BottomNav() {
  const pathname = usePathname();

  return (
    <nav
      aria-label="Main"
      className="fixed inset-x-0 bottom-0 border-t bg-background pb-[env(safe-area-inset-bottom)]"
    >
      <ul className="mx-auto grid max-w-md grid-cols-2">
        {tabs.map(({ href, label, icon: Icon, matches }) => {
          const active = matches(pathname);
          return (
            <li key={href}>
              <Link
                href={href}
                aria-current={active ? "page" : undefined}
                className={cn(
                  "flex min-h-14 flex-col items-center justify-center gap-1 text-xs font-medium outline-none focus-visible:ring-3 focus-visible:ring-ring/50",
                  active
                    ? "text-foreground"
                    : "text-muted-foreground hover:text-foreground",
                )}
              >
                <Icon aria-hidden="true" className="size-5" />
                <span>{label}</span>
                {/* Shape, not only colour, marks the active tab. */}
                <span
                  aria-hidden="true"
                  className={cn(
                    "h-0.5 w-8 rounded-full",
                    active ? "bg-primary" : "bg-transparent",
                  )}
                />
              </Link>
            </li>
          );
        })}
      </ul>
    </nav>
  );
}
