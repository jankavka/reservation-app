import { useLocation } from "react-router";
import type { menuItem } from "./NavLinks";
import { Navbar, Container, Nav } from "react-bootstrap";
import { useAuth } from "../hooks/useAuth";

const menu: menuItem[] = [
  {
    label: "Domů",
    link: "/admin",
  },
  {
    label: "Uživatelé",
    link: "/admin/uzivatele",
  },
  {
    label: "Rezervace",
    link: "/admin/rezervace",
  },
  {
    label: "Areály",
    link: "/admin/arealy",
  },
  {
    label: "Kurty",
    link: "/admin/kurty",
  },
  {
    label: "Trenéři",
    link: "/admin/treneri",
  },
  {
    label: "Company Info",
    link: "/admin/info",
  },
];

const AdminNavLinks = () => {
  const pathname = useLocation().pathname;
  const username = "user";
  const { logout } = useAuth({ username });

  return (
    <div>
      <Navbar expand="lg" bg="primary" sticky="top">
        <Container>
          <Nav>
            <Navbar.Brand>ADMIN PAGE</Navbar.Brand>
          </Nav>
        </Container>
      </Navbar>
      <Navbar expand="lg" bg="primary" sticky="top">
        <br />
        <Container>
          <Navbar.Toggle aria-controls="basic-nav" />
          <Navbar.Collapse id="basic-nav">
            <Nav className="me-auto">
              {menu.map((item: menuItem, index: number) => (
                <div key={index}>
                  <Nav.Link
                    className={pathname === item.link ? "active" : ""}
                    href={item.link}
                  >
                    {item.label}
                  </Nav.Link>
                </div>
              ))}
            </Nav>
          </Navbar.Collapse>
          <Navbar className="justify-content-end">
            {username ? (
              <Nav>
                <Nav.Link href={"/admin/profil"}>Profil</Nav.Link>
                <Nav.Link onClick={() => logout()}>Odhlásit</Nav.Link>
                <Nav.Link href={"/"}>Public</Nav.Link>
              </Nav>
            ) : (
              <Nav></Nav>
            )}
          </Navbar>
        </Container>
      </Navbar>
    </div>
  );
};

export default AdminNavLinks;
