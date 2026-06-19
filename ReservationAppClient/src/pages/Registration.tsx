import {
  Container,
  Form,
  Button,
  FormGroup,
  Spinner,
} from "react-bootstrap";
import { useState } from "react";
import { type RegistrationRequestDto, type CreateUserByAdminDto } from "../api";
import { useMutation } from "@tanstack/react-query";
import { useNavigate, useLocation } from "react-router";
import { useApi } from "../hooks/useApi";
import FlashMessage from "../components/FlashMessages";

const Registration = () => {
  const [isHidden, setIsHidden] = useState<boolean>(true);
  const navigate = useNavigate();
  const [firstName, setFirstName] = useState<string>("");
  const [surname, setSurname] = useState<string>("");
  const api = useApi();
  const location = useLocation();
  const isAdmin = location.pathname.includes("admin");
  const [isSubmitted, setIsSubmitted] = useState<boolean>(false);
  const [success, setSuccess] = useState<boolean>();
  const [state, setState] = useState<boolean>(true);
  const [errorMessage, setErrorMessage] = useState<string>();
  const defaultErrorMessage = "Some error occured. Please try again later";
  const [newUser, setNewUser] = useState<RegistrationRequestDto>({
    email: "",
    password: "",
    telephoneNumber: "+420",
    fullName: "",
  });
  const [newUserByAdmin, setNewUserByAdmin] = useState<CreateUserByAdminDto>({
    email: "",
    telephoneNumber: "+420",
    fullName: "",
    roles: [],
  });

  const concatNameAndSurname = (firstName: string, surname: string): string => {
    return firstName.trim() + " " + surname.trim();
  };

  //USER METHODS

  const callback = async (user: RegistrationRequestDto) => {
    const response = await api.createUser({ body: user });

    if (response.error) {
      const values =
        Object.values(response.error).join(", ") || defaultErrorMessage;
      throw new Error(values);
    }
  };

  const createUser = useMutation({
    mutationFn: callback,
    onError: (error) => {
      setIsSubmitted(false);
      setErrorMessage(error.message);
      setSuccess(false);
      setState(false);
      console.error(error);
    },
    onSuccess: () =>
      navigate("/login", {
        state: {
          success: true,
          username: concatNameAndSurname(firstName, surname),
        },
      }),
  });

  //ADMIN METHODS

  const callbackByAdmin = async (user: CreateUserByAdminDto) => {
    const response = await api.createUserByAdmin({ body: user });
    if (response.error) {
      const values =
        Object.values(response.error).join(", ") || defaultErrorMessage;
      throw new Error(values);
    }
  };

  const createUserByAdmin = useMutation({
    mutationFn: callbackByAdmin,
    onError: (error) => {
      setIsSubmitted(false);
      setErrorMessage(error.message);
      setSuccess(false);
      setState(false);
      console.error(error);
    },
    onSuccess: () =>
      navigate("/admin", {
        state: {
          success: true,
          username: concatNameAndSurname(firstName, surname),
        },
      }),
  });

  //Submit for both admin and user
  const handleSubmit = (e: any) => {
    e.preventDefault();
    setIsSubmitted(true);
    if (isAdmin) {
      const payload = {
        ...newUserByAdmin,
        fullName: concatNameAndSurname(firstName, surname),
      };
      createUserByAdmin.mutate(payload, {
        onError: (e) => console.error(e.message),
      });
    } else {
      const payload = {
        ...newUser,
        fullName: concatNameAndSurname(firstName, surname),
      };

      if (isHidden) {
        createUser.mutate(payload, {
          onError: (e) => console.error(e.message),
        });
      }
    }
  };

  /**
   * Sets params of newUser or newUserByAdmin object
   * @param e
   */
  const setUserObject = (e: any): void => {
    if (isAdmin) {
      setNewUserByAdmin((prev) => {
        return { ...prev, [e.target.name]: e.target.value };
      });
    } else {
      setNewUser((prev) => {
        return { ...prev, [e.target.name]: e.target.value };
      });
    }
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

  const handleRolesChange = (e: any) => {
    console.log([...e.target.selectedOptions]);
    const selectedOptions = [...e.target.selectedOptions];
    let array = selectedOptions.map((option) => option.value);
    setNewUserByAdmin((prev) => {
      return { ...prev, roles: array };
    });
  };

  return (
    <div>
      <Container fluid={"md"} style={{ maxWidth: "40dvw" }}>
        <h1 className="text-center">
          {isAdmin ? "Nový uživatel" : "Registrace"}
        </h1>
        <div hidden={state} className="mt-3">
          <FlashMessage success={success} text={errorMessage} state={!state} />
        </div>
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
              value={isAdmin ? newUserByAdmin.email : newUser.email}
              onChange={(e) => setUserObject(e)}
              placeholder="Email"
            />
          </Form.Group>
          {isAdmin ? null : (
            <div>
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
            </div>
          )}
          <FormGroup className="mb-3">
            <Form.Label>Telefonní číslo</Form.Label>
            <Form.Control
              name="telephoneNumber"
              value={
                isAdmin
                  ? newUserByAdmin.telephoneNumber
                  : newUser.telephoneNumber
              }
              onChange={(e) => setUserObject(e)}
              placeholder="Tel. číslo"
            />
          </FormGroup>
          {isAdmin ? (
            <FormGroup className="mb-3">
              <Form.Label>Role</Form.Label>
              <Form.Select
                multiple={true}
                value={newUserByAdmin.roles}
                onChange={(e) => handleRolesChange(e)}
              >
                <option value="ADMIN">admin</option>
                <option value="PARENT">parent</option>
                <option value="COACH">coach</option>
              </Form.Select>
            </FormGroup>
          ) : null}
          <FormGroup className="d-flex align-items-center">
            <Button type="submit" variant="secondary" className="me-3">
              Vytvořit účet
            </Button>
            <Button variant="secondary" onClick={() => navigate(-1)}>Zpět</Button>
            <Spinner animation="border" role="status" hidden={!isSubmitted}>
              <span className="visually-hidden">Loading...</span>
            </Spinner>
          </FormGroup>
        </Form>
      </Container>
    </div>
  );
};

export default Registration;
