import { Navbar, Container, Nav } from "react-bootstrap";
import { useState } from "react";

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
    label: "Kurty",
    link: "/kurty",
  },
  {
    label: "Kontakty",
    link: "/kontakty",
  },
  {
    label: "O nás",
    link: "/o-nas",
  },
];

const NavLinks = () => {
  const [isLoggedIn, setIsLoggedIn] = useState<boolean>(false);
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
            <Navbar.Text>
              <Nav.Link>Přihlásit</Nav.Link>
              <Nav.Link>Registrovat</Nav.Link>
            </Navbar.Text>
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
                  <Nav.Link href={item.link}>{item.label}</Nav.Link>
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
