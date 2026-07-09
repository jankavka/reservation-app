import { useQuery } from "@tanstack/react-query";
import { showCurrentUserOptions } from "../api/@tanstack/react-query.gen";
import { Navigate } from "react-router";
import Loading from "./Loading";

const AdminRoute = ({ children }) => {
  const { data, isSuccess, isPending, isError } = useQuery({
    ...showCurrentUserOptions({}),
    enabled: Boolean(localStorage.getItem("token")),
    retry: 3,
  });

  const autoLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("currentUser");
  };

  if (isPending) {
    return (
      <div
        className="d-flex justify-content-center  align-items-center"
        style={{ height: "100dvh" }}
      >
        <div className="text-center">
          <Loading />
          <p className="text-center">Loading...</p>
        </div>
      </div>
    );
  }

  if (
    isSuccess &&
    data?.authorities?.find((user) => user.authority.includes("ADMIN"))
  ) {
    return <div>{children}</div>;
  } else if (!isPending && isError) {
    autoLogout();

    return (
      <div>
        <Navigate
          to={"/"}
          state={{
            logoutSuccess: true,
            logoutText: "Odhlášení proběhlo úspěšně",
          }}
        />
      </div>
    );
  }
};

export default AdminRoute;
