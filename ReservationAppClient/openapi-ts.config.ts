import { defineConfig } from "@hey-api/openapi-ts";

export default defineConfig({
  input: "./docs/api-docs.yaml",
  output: "./src/api",
  plugins: [
    {
      auth: true,
      name: "@hey-api/sdk",
    },
    {
      name: "@tanstack/react-query",
      queryOptions: true,
    },
  ],
});
