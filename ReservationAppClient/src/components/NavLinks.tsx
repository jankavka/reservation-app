import { Navbar, Container, Nav } from "react-bootstrap";
import { useState } from "react";
import { useLocation } from "react-router";

type menuItem = {
  label: string;
  link: string;
};

const menu: menuItem[] = [
  {
    label: "Domů",
    link: "/",
  },
  {
    label: "Rezervace",
    link: "/rezervace",
  },
  {
    label: "Areály",
    link: "/arealy",
  },
  {
    label: "Kurty",
    link: "/kurty",
  },
  {
    label: "Trenéři",
    link: "/treneri",
  },
  {
    label: "O nás",
    link: "/o-nas",
  },
  {
    label: "Kontakty",
    link: "/kontakty",
  },
];

const NavLinks = () => {
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false);
  const pathname = useLocation().pathname;
  return (
    <>
      <Navbar data-bs-theme="dark" bg="dark">
        <Container>
          <Navbar.Brand>Tenisový rezervační systém</Navbar.Brand>
          {isLoggedIn ? (
            <Navbar.Text className="justify-content-end">
              <div>Přihlášený uživatel:</div>
              <div className="text-end">Some Name</div>
            </Navbar.Text>
          ) : (
            <Nav>
              <Nav.Link
                className={pathname === "/login" ? "active" : ""}
                href={"/login"}
              >
                Přihlásit
              </Nav.Link>
              <Nav.Link
                className={pathname === "/registrace" ? "active" : ""}
                href={"/registrace"}
              >
                Registrovat
              </Nav.Link>
            </Nav>
          )}
        </Container>
      </Navbar>
      <Navbar expand="lg" data-bs-theme="dark" bg="dark">
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
            {isLoggedIn ? (
              <Nav>
                <Nav.Link href="{}">Profil</Nav.Link>
              </Nav>
            ) : (
              <Nav></Nav>
            )}
          </Navbar>
        </Container>
      </Navbar>
    </>
  );
};

export default NavLinks;
