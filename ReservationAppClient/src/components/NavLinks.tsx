import { Navbar, Container, Nav } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router";
import { useApi } from "../hooks/useApi";
import { useMutation } from "@tanstack/react-query";

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
  const username = localStorage.getItem("currentUser");

  const pathname = useLocation().pathname;
  const navigation = useNavigate();
  const api = useApi();
  const logoutFn = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("currentUser");
    return api.logout();
  };

  const logout = useMutation({
    mutationFn: logoutFn,
    onError: (error) => {
      console.error(error);
    },
    onSuccess: () => {
      navigation("/", {
        state: {
          logoutSuccess: true,
          logoutText: "Odhlášení proběhlo úspěšně",
        },
      });
    },
  });

  return (
    <>
      <Navbar data-bs-theme="dark" bg="dark" style={{ height: "8dvh" }}>
        <Container style={{height: "5dvh"}}>
          <Navbar.Brand>Tenisový rezervační systém</Navbar.Brand>
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
            {username ? (
              <Nav>
                <Nav.Link href="{}">Profil</Nav.Link>
                <Nav.Link onClick={() => logout.mutate()}>Odhlásit</Nav.Link>
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
