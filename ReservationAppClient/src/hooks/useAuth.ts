import { useMutation } from "@tanstack/react-query";
import { useState } from "react";
import { type LoginResponseDto } from "../api";
import { useApi } from "./useApi";
import { useNavigate } from "react-router";
import { useUserContext } from "../context/CurrentUserContext";
import { client } from "../api/client.gen";

export const useAuth = (props: any) => {
  const [authResponse, setAuthResponse] = useState<LoginResponseDto>();
  const api = useApi();
  const navigate = useNavigate();
  const { setUsername } = useUserContext();
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
      localStorage.setItem("token", data.data?.token);
      client.setConfig({
        baseUrl: "http://localhost:8080/",
        headers: {
          Authorization: "Bearer " + data.data?.token,
        },
      });
      setUsername(props.username);
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
