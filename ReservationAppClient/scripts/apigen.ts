import { createClient } from "@hey-api/openapi-ts";

try{
    await createClient({
        //client: "@hey-api/client-fetch",
        input: "./docs/api-docs.yaml",
        output: "./src/api"
    })
} catch(e ){
    console.error("Error generating api ", e);
}
