import { Container, Form, FormGroup, Button } from "react-bootstrap";
import { useParams } from "react-router";
import { useNavigate } from "react-router";
import { getUser, type UserDto } from "../../api";
import { useState } from "react";
import { useMutation, useQuery } from "@tanstack/react-query";
import { updateUserByAdminMutation } from "../../api/@tanstack/react-query.gen";
import Loading from "../../components/Loading";

const AdminEditUser = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [user, setUser] = useState<UserDto>({
    email: "",
    telephoneNumber: "",
    fullName: "",
    roles: [],
    createdAt: "",
  });
  const [firstName, setFirstName] = useState<string>("");
  const [surname, setSurname] = useState<string>("");

  useQuery({
    queryFn: () =>
      getUser({ path: { id: parseInt(id) } })
        .then((myUser) => {
          setUser(myUser.data);
          setFirstName(splitFullName(myUser.data.fullName)[0]);
          setSurname(splitFullName(myUser.data.fullName)[1]);
          return myUser;
        })
        .catch((error) => {
          console.error(error);
        }),
    queryKey: ["user", id],
  });

  const { mutate } = useMutation({
    ...updateUserByAdminMutation({
      body: user,
      path: { id: parseInt(id.trim()) },
    }),
  });

  const handleChange = (e: any) => {
    setUser((prev) => {
      return { ...prev, [e.target.name]: e.target.value };
    });
  };

  const handleRolesChange = (e: any) => {
    const options = [...e.target.selectedOptions];
    console.log(options);
    const array = options.map((item) => item.value);
    setUser((prev) => {
      return { ...prev, roles: array };
    });
  };

  const splitFullName = (fullName: string) => {
    return fullName.split(" ");
  };

  const handleSubmit = (e: any) => {
    e.preventDefault();
    setIsLoading(true);
    const payload: UserDto = {
      id: user.id,
      telephoneNumber: user.telephoneNumber,
      fullName: firstName.trim() + " " + surname.trim(),
      roles: user.roles,
      email: user.email,
    };

    mutate({ body: payload, path: { id: payload.id } });
    navigate("/admin/uzivatele", {
      state: { success: true, user: payload.fullName },
    });
    setIsLoading(false);
  };

  return (
    <div>
      <Container>
        <div className="mb-3">
          <h1>Upravit uživatele</h1>
          <p>Id uživatele: {id}</p>
        </div>
        <Form onSubmit={(e) => handleSubmit(e)}>
          <Form.Group className="mb-3">
            <Form.Label>Jméno</Form.Label>
            <Form.Control
              value={firstName}
              onChange={(e) => setFirstName(e.target.value)}
              name="firstName"
              placeholder="Jméno"
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Příjmeni</Form.Label>
            <Form.Control
              value={surname}
              onChange={(e) => setSurname(e.target.value)}
              name="surname"
              placeholder="Přijmení"
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Email</Form.Label>
            <Form.Control
              onChange={(e) => handleChange(e)}
              value={user?.email}
              name="email"
              placeholder="Email"
            />
          </Form.Group>
          <FormGroup className="mb-3">
            <Form.Label>Telefonní číslo</Form.Label>
            <Form.Control
              value={user?.telephoneNumber}
              onChange={(e) => handleChange(e)}
              name="telephoneNumber"
              placeholder="Tel. číslo"
            />
          </FormGroup>
          <FormGroup className="mb-3">
            <Form.Label>Role</Form.Label>
            <Form.Select
              onChange={(e) => handleRolesChange(e)}
              value={user.roles}
              multiple={true}
            >
              <option value="ADMIN">admin</option>
              <option value="PARENT">parent</option>
              <option value="COACH">coach</option>
            </Form.Select>
          </FormGroup>
          <FormGroup>
            <Button className="me-2" type="submit">
              Odeslat
            </Button>
            <Button type="button" onClick={() => navigate(-1)}>
              Zpět
            </Button>
          </FormGroup>
          {isLoading ? <Loading /> : null}
        </Form>
      </Container>
    </div>
  );
};

export default AdminEditUser;
