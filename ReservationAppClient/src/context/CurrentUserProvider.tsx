import { useState } from "react";
import { UserContext } from "./CurrentUserContext";
import { useQuery } from "@tanstack/react-query";
import { showCurrentUser } from "../api";

export const CurrentUserProvider = ({
  children,
}: {
  children: React.ReactNode;
}) => {
  const token = localStorage.getItem("token");
  const callback = async () =>
    await showCurrentUser()
      .then((response) => {
        setUsername(response.data?.username);
        return response;
      })
      .catch((error) => console.error(error));

  useQuery({
    queryKey: ["currentUser"],
    queryFn: callback,
    enabled: Boolean(token !== null),
  });
  const [username, setUsername] = useState<string | undefined>();

  return (
    <UserContext
      value={{
        username,
        setUsername,
      }}
    >
      {children}
    </UserContext>
  );
};
