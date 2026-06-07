import { Routes, Route } from "react-router";
import Home from "../pages/admin/AdminHome";
import AdminNavLinks from "../components/AdminNavLinks";
import AdminReservation from "../pages/admin/AdminReservation";
import AdminVenues from "../pages/admin/AdminVenues";
import AdminCourts from "../pages/admin/AdminCourts";
import AdminCoaches from "../pages/admin/AdminCoaches";
import AdminAboutUs from "../pages/admin/AdminAboutUs";
import AdminContacts from "../pages/admin/AdminContacts";
import AdminProfile from "../pages/admin/AdminProfile";
import AdminUsers from "../pages/admin/AdminUsers";
import Registration from "../pages/Registration";

const AdminLayout = () => {
  return (
    <div>

        <AdminNavLinks />
        <Routes>
          <Route index element={<Home />} />
          <Route path="/uzivatele" element={<AdminUsers />} />
          <Route path="uzivatele/novy" element={<Registration/>}/>
          <Route path="/rezervace" element={<AdminReservation />} />
          <Route path="/arealy" element={<AdminVenues />} />
          <Route path="/kurty" element={<AdminCourts />} />
          <Route path="/treneri" element={<AdminCoaches />} />
          <Route path="/o-nas" element={<AdminAboutUs />} />
          <Route path="/kontakty" element={<AdminContacts />} />
          <Route path="/profil" element={<AdminProfile />} />
        </Routes>

    </div>
  );
};

export default AdminLayout;
