import { Navbar, Container, Nav } from "react-bootstrap";

type menuItem = {
  label: string;
  link: string;
};

const menu: menuItem[] = [
  {
    label: "Domů",
    link: "/domu",
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
  return (
    <>
      <Navbar data-bs-theme="dark" bg="dark">
        <Container>
          <Navbar.Brand>Tenisový rezervační systém</Navbar.Brand>
          <Navbar.Text className="justify-content-end">
            Přihlášený uživatel:
          </Navbar.Text>
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
            <Nav>
              <Nav.Link href="{}">Profil</Nav.Link>
            </Nav>
          </Navbar>
        </Container>
      </Navbar>
    </>
  );
};

export default NavLinks;
