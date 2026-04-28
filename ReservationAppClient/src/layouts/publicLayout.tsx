import NavLinks from "../components/NavLinks";
import { Route, Routes } from "react-router";
import Login from "../pages/Login";
import Home from "../pages/Home";
import Courts from "../pages/Courts";
import Reservation from "../pages/reservation";
import AboutUs from "../pages/AboutUs";
import Contacts from "../pages/Contacts";
import Registration from "../pages/Registration";
import Coaches from "../pages/Coaches";
import Venues from "../pages/Venues";

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
        <Route path="/login" element={<Login/>}/>
        <Route path="/registrace" element={<Registration/>}/>
        <Route path="/treneri" element={<Coaches/>} />
        <Route path="/arealy" element={<Venues/>} />
      </Routes>
    </div>
  );
};

export default PublicLayout;
