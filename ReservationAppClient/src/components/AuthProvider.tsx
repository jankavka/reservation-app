import { useState } from "react";
import { client } from "../api/client.gen";
import { useRefresh } from "../hooks/useRefresh";

const AuthProvider = ({ children }) => {
  const [token, _] = useState<string>(localStorage.getItem("token"));
  const { doRefresh } = useRefresh();

  //   client.interceptors.request.use((request, options) => {
  //     request.headers.set('Authorization', 'Bearer ' + token);
  //     return request;
  //   });

  client.interceptors.response.use(
    (res: Response): Response | Promise<Response> => {
      if (!res?.ok && res?.status === 401) {
        doRefresh();
      }
      return res;
    }
  );

  client.setConfig({
    baseUrl: "http://localhost:8080/",
    headers: {
      Authorization: token ? "Bearer " + token : null,
    },
    querySerializer: (params) => {
      const flat: Record<string, string> = {};
      for (const [key, value] of Object.entries(params)) {
        if (typeof value === "object" && value !== null) {
          for (const [subKey, subVal] of Object.entries(value)) {
            flat[subKey] = String(subVal);
          }
        } else {
          flat[key] = String(value);
        }
      }
      return new URLSearchParams(flat).toString();
    },
  });

  return <div>{children}</div>;
};

export default AuthProvider;
