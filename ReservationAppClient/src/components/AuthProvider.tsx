import { useState } from "react";
import { client } from "../api/client.gen";

const AuthProvider = ({ children }) => {
  const [token, _] = useState<string>(localStorage.getItem("token"));

  //   client.interceptors.request.use((request, options) => {
  //     request.headers.set('Authorization', 'Bearer ' + token);
  //     return request;
  //   });

  client.setConfig({
    baseUrl: "http://localhost:8080/",
    headers: {
      Authorization: token? "Bearer " + token :null,
    },
  });

  return <div>{children}</div>;
};

export default AuthProvider;
