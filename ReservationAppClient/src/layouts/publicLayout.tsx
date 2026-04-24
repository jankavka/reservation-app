import NavLinks from "../components/NavLinks";
import { Route, Routes } from "react-router";
import Home from "../pages/Home";

const PublicLayout = () => {
  return (
    <div>
      <NavLinks />

      <Routes>
        <Route path={"/"} element={<Home />} />
      </Routes>
    </div>
  );
};

export default PublicLayout;
