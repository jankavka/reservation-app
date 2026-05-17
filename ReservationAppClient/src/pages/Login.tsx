import { Container, Form, Button, FormGroup, Spinner } from "react-bootstrap";
import { useEffect, useState } from "react";
import { type AuthRequestDto } from "../hooks/useApi";
import { useLocation } from "react-router";
import FlashMessage from "../components/FlashMessages";
import { useAuth } from "../hooks/useAuth";

const Login = () => {
  const location = useLocation();
  const [isSubmitted, setIsSubmitted] = useState<boolean>(false || location.state?.isSubmitted);
  const [userData, setUserData] = useState<AuthRequestDto>({
    username: "",
    password: "",
  });
  const { mutate: login } = useAuth({
    username: userData.username,
    password: userData.password,
    setIsSubmitted,

  });
  const success: boolean = false || location.state?.success;
  const errorState: boolean = false || location.state?.errorState
  const nameOfCreatedUser: string = location.state?.username;
  const errorMessage: string = location.state?.errorMessage

  const handleSubmit = (e: any) => {
    e.preventDefault();
    login();
  };

  const handleChange = (e: any) => {
    setUserData((prev) => {
      return { ...prev, [e.target.name]: e.target.value };
    });
  };

  useEffect(() => {
  }, [location.state?.errorState])

  return (
    <div>
      <Container fluid={"md"} style={{ maxWidth: "40dvw" }}>
        <h1 className="text-center">Přihlášení</h1>
        <FlashMessage
          success={true}
          state={success}
          text={`Uživatel ${nameOfCreatedUser} úspěšně zaregistrován `}
        ></FlashMessage>
        <FlashMessage
          success={false}
          state={errorState}
          text={`${errorMessage}`}
        ></FlashMessage>
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
          <FormGroup>
            <Button type="submit" variant="secondary">
              Přihlásit
            </Button>
            <Spinner animation="border" role="status" hidden={!isSubmitted}>
              <span className="visually-hidden">Loading...</span>
            </Spinner>
          </FormGroup>
        </Form>
      </Container>
    </div>
  );
};

export default Login;
