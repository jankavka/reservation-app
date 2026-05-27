import { useQuery } from "@tanstack/react-query";
import { showCurrentUserOptions } from "../api/@tanstack/react-query.gen";
import { Navigate } from "react-router";

const AdminRoute = ({ children }) => {
  const { data, isSuccess, isPending } = useQuery({
    ...showCurrentUserOptions({}),
    enabled: Boolean(localStorage.getItem("token")),
  });

  if (
    isSuccess &&
    data?.authorities?.find((user) => user.authority.includes("ADMIN"))
  ) {
    return <div>{children}</div>;
  } else if (!isPending) {
    return (
      <div>
        <Navigate to={"/"} />
      </div>
    );
  }
};

export default AdminRoute;
