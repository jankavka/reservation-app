import NavLinks from "../components/NavLinks";
import { Route, Routes } from "react-router";
import Home from "../pages/Home";
import Courts from "../pages/Courts";
import Reservation from "../pages/reservation";
import AboutUs from "../pages/AboutUs";
import Contacts from "../pages/Contacts";

const PublicLayout = () => {
  return (
    <div>
      <NavLinks />

      <Routes>
        <Route index element={<Home />} />
        <Route path="/rezervace" element={<Reservation />} />
        <Route path="/kurty" element={<Courts/>}/>
        <Route path="/o-nas" element={<AboutUs/>}/>
        <Route path="/kontakty" element={<Contacts/>}/>
      </Routes>
    </div>
  );
};

export default PublicLayout;
