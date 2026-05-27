import { Navbar, Container, Nav } from "react-bootstrap";
import { useLocation } from "react-router";
import { useAuth } from "../hooks/useAuth";

export type menuItem = {
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

export const NavLinks = () => {
  const username = localStorage.getItem("currentUser");
  const pathname = useLocation().pathname;
  const {logout} = useAuth({username});


  return (
    <>
      <Navbar data-bs-theme="dark" bg="dark">
        <Container
          style={{
            height: `${window.innerWidth > 440 ? "5dvh" : "15dvh"}`,
            display: `${window.innerWidth > 440 ? "" : "flex"}`,
            flexDirection: `${window.innerWidth > 440 ? "row" : "column"}`,
          }}
        >
          <Nav style={{ display: "flex", alignItems: "center" }}>
            <Navbar.Brand>Tenisový rezervační systém</Navbar.Brand>
            <img
              src="/src/assets/tennis.png"
              alt="tennis-ball"
              style={{ maxHeight: "3dvh" }}
            />
          </Nav>
          {username ? (
            <Nav>
              <Navbar.Text className="justify-content-end">
                <div>Přihlášený uživatel:</div>
                <div className="text-end">{username}</div>
              </Navbar.Text>
            </Nav>
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
      <Navbar expand="lg" data-bs-theme="dark" bg="dark" sticky="top">
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
                <Nav.Link href="/profil">Profil</Nav.Link>
                <Nav.Link onClick={() => logout()}>Odhlásit</Nav.Link>
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
