import type { NextConfig } from "next";

// Where the Spring Boot backend runs. `next dev` reads it at start; `next build` bakes it into
// the build (rewrites are stored in the routes manifest), so setting it only for `next start`
// has no effect.
const backendUrl = process.env.BACKEND_URL ?? "http://localhost:8080";

const nextConfig: NextConfig = {
  turbopack: {
    rules: {
      "*.css": {
        loaders: ["@tailwindcss/turbopack"],
        as: "*.css",
      },
    },
  },
  // The browser only talks to this app; /api/* is proxied to the backend (docs/architecture.md, section 5).
  async rewrites() {
    return [{ source: "/api/:path*", destination: `${backendUrl}/api/:path*` }];
  },
};

export default nextConfig;
