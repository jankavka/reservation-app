import { useQuery } from "@tanstack/react-query";
import { showCurrentUserOptions } from "../api/@tanstack/react-query.gen";
import { Navigate } from "react-router";
import Loading from "./Loading";

const AdminRoute = ({ children }) => {
  const { data, isSuccess, isPending, error } = useQuery({
    ...showCurrentUserOptions({}),
    enabled: Boolean(localStorage.getItem("token")),
  });

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
  } else if (error?.message?.includes("Use refresh token or Login")) {
    console.log("catched");
  } else if (!isPending) {
    console.log("here");
    return (
      <div>
        <Navigate to={"/"} />
      </div>
    );
  }
};

export default AdminRoute;
