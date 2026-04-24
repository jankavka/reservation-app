import * as services from "../api/sdk.gen.ts";
export * from "../api/types.gen.ts";

export const useApi = () => {
    return services;
}
