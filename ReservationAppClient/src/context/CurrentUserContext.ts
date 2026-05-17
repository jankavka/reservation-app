import { useContext, createContext } from "react";


type CurrentUserContextType = {
  username: string;
  setUsername: (prev: string) => void
};

export const UserContext = createContext<CurrentUserContextType>({
  username: "",
  setUsername: () => {}
});

export function useUserContext() {
  return useContext(UserContext);
}
