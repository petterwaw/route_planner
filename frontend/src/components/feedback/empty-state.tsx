import type { ReactNode } from "react";
import {
  Empty,
  EmptyContent,
  EmptyDescription,
  EmptyHeader,
  EmptyMedia,
  EmptyTitle,
} from "@/components/ui/empty";

type EmptyStateProps = {
  /** Decorative icon; hidden from screen readers. */
  icon?: ReactNode;
  title: string;
  description?: string;
  /** Optional call to action, e.g. a link to Settings. */
  action?: ReactNode;
};

export function EmptyState({
  icon,
  title,
  description,
  action,
}: EmptyStateProps) {
  return (
    <Empty className="border">
      <EmptyHeader>
        {icon && (
          <EmptyMedia variant="icon" aria-hidden="true">
            {icon}
          </EmptyMedia>
        )}
        <EmptyTitle>{title}</EmptyTitle>
        {description && <EmptyDescription>{description}</EmptyDescription>}
      </EmptyHeader>
      {action && <EmptyContent>{action}</EmptyContent>}
    </Empty>
  );
}
