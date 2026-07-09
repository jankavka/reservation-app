import { Routes, Route } from "react-router";
import Home from "../pages/admin/AdminHome";
import AdminNavLinks from "../components/AdminNavLinks";
import AdminReservation from "../pages/admin/AdminReservation";
import AdminVenues from "../pages/admin/AdminVenues";
import AdminCourts from "../pages/admin/AdminCourts";
import AdminCoaches from "../pages/admin/AdminCoaches";
import AdminInfo from "../pages/admin/AdminInfo";
import AdminProfile from "../pages/admin/AdminProfile";
import AdminUsers from "../pages/admin/AdminUsers";
import Registration from "../pages/Registration";
import AdminEditUser from "../pages/admin/AdminEditUser";
import AdminVenueForm from "../pages/admin/AdminVenueForm";
import AdminVenueEdit from "../pages/admin/AdminVenueEdit";
import AdminEditContacts from "../pages/admin/AdminEditContacts";
import AdminAboutUsEdit from "../pages/admin/AdminAboutUsEdit";
import AdminCreateCourt from "../pages/admin/AdminCreateCourt";
import AdminCourtDetail from "../pages/admin/AdminCourtDetail";
import AdminVenueDetail from "../pages/admin/AdminVenueDetail";
import AdminUserDetail from "../pages/admin/AdminUserDetail";

const AdminLayout = () => {
  return (
    <div>
      <AdminNavLinks />
      <Routes>
        <Route index element={<Home />} />
        <Route path="/uzivatele" element={<AdminUsers />} />
        <Route path="/uzivatele/novy" element={<Registration />} />
        <Route path="/uzivatele/upravit/:id" element={<AdminEditUser />} />
        <Route path="/uzivatele/:id" element={<AdminUserDetail />} />
        <Route path="/rezervace" element={<AdminReservation />} />
        <Route path="/arealy" element={<AdminVenues />} />
        <Route path="/arealy/novy" element={<AdminVenueForm />} />
        <Route path="/arealy/upravit/:id" element={<AdminVenueEdit />} />
        <Route path="/arealy/:id" element={<AdminVenueDetail />} />
        <Route path="/kurty" element={<AdminCourts />} />
        <Route path="/kurty/novy" element={<AdminCreateCourt />} />
        <Route path="/kurty/:id" element={<AdminCourtDetail />} />
        <Route path="/treneri" element={<AdminCoaches />} />
        <Route path="/info/upravit" element={<AdminAboutUsEdit />} />
        <Route path="/info" element={<AdminInfo />} />
        <Route path="/kontakty/upravit" element={<AdminEditContacts />} />
        <Route path="/profil" element={<AdminProfile />} />
      </Routes>
    </div>
  );
};

export default AdminLayout;
