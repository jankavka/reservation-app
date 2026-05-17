import { createClient } from "@hey-api/openapi-ts";

try {
  await createClient({
    input: "./docs/api-docs.yaml",
    output: "./src/api",
    plugins: [
      {
        auth: true,
        name: "@hey-api/sdk",
      },
    ],
  });
} catch (e) {
  console.error("Error generating api ", e);
}
