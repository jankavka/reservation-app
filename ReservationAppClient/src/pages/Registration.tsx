import { Container, Form, Button, FormGroup } from "react-bootstrap";
import { useState } from "react";
import type { RegistrationRequestDto } from "../api";
import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router";
import { useApi } from "../hooks/useApi";
import FlashMessage from "../components/FlashMessages";

const Registration = () => {
  const [isHidden, setIsHidden] = useState<boolean>(true);
  const navigate = useNavigate();
  const [firstName, setFirstName] = useState<string>("");
  const [surname, setSurname] = useState<string>("");
  const api = useApi();
  const [success, setSuccess] = useState<boolean>();
  const [state, setState] = useState<boolean>(false);
  const [errorMessage, setErrorMessage] = useState<string>();

  const callback = async (user: RegistrationRequestDto) => {
    const response = await api.createUser({ body: user });
    if (response.error) {
      const values = Object.values(response.error).join(", ");
      throw new Error(values);
    }
  };

  const createUser = useMutation({
    mutationFn: callback,
    onError: (error) => {
      setErrorMessage(error.message);
      setSuccess(false);
      setState(true);
      console.error(error);
    },
    onSuccess: () => navigate("/"),
  });

  const [newUser, setNewUser] = useState({
    email: "",
    password: "",
    telephoneNumber: "",
    fullName: "",
  });

  const handleSubmit = (e: any) => {
    e.preventDefault();
    const payload = {
      ...newUser,
      fullName: firstName.trim() + " " + surname.trim(),
    };

    if (isHidden) {
      createUser.mutate(payload, { onError: (e) => console.error(e) });
    }
  };

  /**
   * Sets params of newUser object
   * @param e
   */
  const setUserObject = (e: any): void => {
    setNewUser((prev) => {
      return { ...prev, [e.target.name]: e.target.value };
    });
  };

  /**
   * Checks if passwords are the same
   * @param e event
   */
  const checkIfPasswordsMatches = (e: any): void => {
    if (e.target.value !== newUser.password) {
      setIsHidden(false);
    } else {
      setIsHidden(true);
    }
  };

  return (
    <div>
      <Container fluid={"md"} style={{ maxWidth: "40dvw" }}>
        <div className="mt-3">
          <FlashMessage success={success} text={errorMessage} state={state} />
        </div>
        <h1 className="text-center">Registrace</h1>
        <Form onSubmit={(e) => handleSubmit(e)}>
          <Form.Group className="mb-3">
            <Form.Label>Jméno</Form.Label>
            <Form.Control
              name="firstName"
              onChange={(e) => setFirstName(e.target.value)}
              placeholder="Jméno"
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Příjmeni</Form.Label>
            <Form.Control
              name="surname"
              onChange={(e) => setSurname(e.target.value)}
              placeholder="Přijmení"
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Email</Form.Label>
            <Form.Control
              name="email"
              onChange={(e) => setUserObject(e)}
              placeholder="Email"
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Heslo</Form.Label>
            <Form.Control
              name="password"
              type="password"
              onChange={(e) => setUserObject(e)}
              placeholder="Heslo"
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Potvrzení hesla</Form.Label>
            <Form.Control
              type="password"
              name="cofirmPassword"
              onChange={(e) => checkIfPasswordsMatches(e)}
              placeholder="Potvrzení hesla"
            />
            <div className="text-danger" hidden={isHidden}>
              Hesla se neshodují
            </div>
          </Form.Group>
          <FormGroup className="mb-3">
            <Form.Label>Telefonní číslo</Form.Label>
            <Form.Control
              name="telephoneNumber"
              onChange={(e) => setUserObject(e)}
              placeholder="Tel. číslo"
            />
          </FormGroup>
          <Button type="submit" variant="secondary">
            Vytvořit účet
          </Button>
        </Form>
      </Container>
    </div>
  );
};

export default Registration;
