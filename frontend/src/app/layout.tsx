import type { Metadata } from "next";
import { Geist, Geist_Mono } from "next/font/google";
import { BottomNav } from "@/components/layout/bottom-nav";
import { Providers } from "./providers";
import "./globals.css";

// globals.css (shadcn theme) expects the font in --font-sans; latin-ext covers Polish place names.
const geistSans = Geist({
  variable: "--font-sans",
  subsets: ["latin", "latin-ext"],
});

const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin", "latin-ext"],
});

export const metadata: Metadata = {
  title: { default: "Zdążę", template: "%s · Zdążę" },
  description:
    "When to leave and how to get to every class, and how much CO2 you save by not driving.",
};

export default function RootLayout({ children }: LayoutProps<"/">) {
  return (
    <html
      lang="en"
      className={`${geistSans.variable} ${geistMono.variable} h-full antialiased`}
    >
      <body className="flex min-h-full flex-col bg-background text-foreground">
        <Providers>
          {/* Bottom padding keeps content clear of the fixed navigation. */}
          <main
            id="main"
            className="mx-auto w-full max-w-md flex-1 px-4 pt-6 pb-28"
          >
            {children}
          </main>
          <BottomNav />
        </Providers>
      </body>
    </html>
  );
}
