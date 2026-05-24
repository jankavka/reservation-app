import { useMutation } from "@tanstack/react-query";
import { useState } from "react";
import { type LoginResponseDto } from "../api";
import { useApi } from "./useApi";
import { useNavigate } from "react-router";
import { client } from "../api/client.gen";

export const useAuth = (props: any) => {
  const [authResponse, setAuthResponse] = useState<LoginResponseDto>();
  const api = useApi();
  const navigate = useNavigate();
  const callback = () => {
    props.setIsSubmitted(true);
    return api.authenticateAndGetToken({
      body: { username: props.username, password: props.password },
    });
  };

  const { mutate } = useMutation({
    mutationFn: callback,
    onSuccess: (data) => {
      if (data.error) {
        throw new Error(Object.values(data.error).join("") || "Some Error Occured");
      }
      setAuthResponse(data.data);
      localStorage.removeItem("token");
      localStorage.removeItem("currentUser")
      localStorage.setItem("token", data.data?.token);
      localStorage.setItem("currentUser", props.username)
      client.setConfig({
        baseUrl: "http://localhost:8080/",
        headers: {
          Authorization: "Bearer " + data.data?.token,
        },
      });
      navigate("/", { state: { successState: true, name: props.username } });
    },
    onError: (error) => {
      console.error(error);
      localStorage.removeItem("token")
      navigate("/login", {
        state: {
          errorState: true,
          isSubmitted: false,
          errorMessage: error.message,
        },
      });
      props.setIsSubmitted(false);
    },
  });

  return { authResponse, mutate };
};
