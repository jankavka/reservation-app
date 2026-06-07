import { useMutation } from "@tanstack/react-query";
import { refresh } from "../api";
import { client } from "../api/client.gen";
import { useState } from "react";

export const useRefresh = () => {
  const [token, setToken] = useState<string>();
  const { mutate: doRefresh } = useMutation({
    mutationFn: async () => {
      return await refresh({
        body: { accessToken: localStorage.getItem("token") },
      });
    },
    mutationKey: ["refreshToken"],
    onError: (error) => {
      console.log(error?.message);
    },
    onSuccess: (data) => {
      localStorage.removeItem("token");
      localStorage.setItem("token", data.data.token);
      setToken(data.data.token);
      client.interceptors.request.use((request) => {
        request.headers.set("Authorization", "Bearer " + data.data.token);
        return request;
      });
      return data.data.token;
    },
  });

  return { doRefresh, token };
};
