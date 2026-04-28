import { Container, Form, Button } from "react-bootstrap";
import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { useApi, type AuthRequestDto } from "../hooks/useApi";

const Login = () => {
  const [token, setToken] = useState<string>();
  const [userData, setUserData] = useState<AuthRequestDto>({
    username: "",
    password: "",
  });
  const api = useApi();

  /**
   * callback for getting access token
   * @param data object with username and password
   * @returns access token if success, error or false in not
   */
  const callback = async (data: AuthRequestDto) => {
    console.log(data);
    try {
      api.authenticateAndGetToken({ body: data }).then((data) => {
        console.log(data);
        setToken(data.data.token);
        return data.data.token;
      });
    } catch (error) {
      console.error("Error during fetching token" + error);
      return error;
    }
    return false;
  };


  const fetchedData = useQuery({
    queryKey: ["token", userData],
    queryFn: () => callback(userData),
    enabled: false,
  });

  const handleSubmit = (e: any) => {
    e.preventDefault();
    console.log(userData);
    fetchedData.refetch();
  };

  if (token) {
    console.log(token);
  }

  const handleChange = (e: any) => {
    setUserData((prev) => {
      return { ...prev, [e.target.name]: e.target.value };
    });
  };

  return (
    <div>
      <Container fluid={"md"} style={{ maxWidth: "40dvw" }}>
        <h1 className="text-center">Přihlášení</h1>
        <Form onSubmit={(e) => handleSubmit(e)}>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="email">Email</Form.Label>
            <Form.Control
              name="username"
              id="email"
              type="email"
              placeholder="Email"
              onChange={(e) => handleChange(e)}
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label htmlFor="password">Heslo</Form.Label>
            <Form.Control
              name="password"
              id="password"
              type="password"
              placeholder="Heslo"
              onChange={(e) => handleChange(e)}
            />
          </Form.Group>
          <Button type="submit" variant="secondary">
            Přihlásit
          </Button>
        </Form>
      </Container>
    </div>
  );
};

export default Login;
