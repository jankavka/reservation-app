import { Container, Table, Button, Form, Collapse } from "react-bootstrap";
import { useMutation } from "@tanstack/react-query";
import type { UserDto, UserFilter } from "../../api";
import { useEffect, useState } from "react";
import { Link, useLocation } from "react-router";
import formatDate from "../../components/DateFormat";
import { useApi } from "../../hooks/useApi";
import FormControlElement from "react";
import useDateFormat from "../../hooks/useDateFormat";
import FlashMessage from "../../components/FlashMessages";

const AdminUsers = () => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const api = useApi();
  const location = useLocation();
  const success: boolean = location.state?.success || false;
  const text: string = `Uživatel ${location.state?.user} úspěšně upraven`;
  const width = window.innerWidth > 600;
  const [filter, setFilter] = useState<UserFilter>({
    email: "",
    telephoneNumber: "",
    fullName: "",
    roles: ["ADMIN", "PARENT", "COACH"],
    createdBefore: "",
    createdAfter: "",
  });
  const [users, setUsers] = useState<UserDto[]>();

  const callback = async () => {
    const response = await api.getAllUsers({ query: { userFilter: filter } });
    if (response.error) {
      const values = Object.values(response.error).join(", ");
      throw new Error(values);
    }
    return response;
  };

  const myUsers = useMutation({
    mutationFn: callback,
    mutationKey: ["users"],
    throwOnError: false,
    onError: (error) => {
      console.log(error.message);
      if (error.message.includes("Use refresh token or Login")) {
      }
    },
    onSuccess: async (data) => {
      setUsers(data.data);
    },
  });

  const handleSubmit = (e: any) => {
    e.preventDefault();
    myUsers.mutate();
  };

  useEffect(() => {
    myUsers.mutate();
  }, []);

  const handleChange = (
    e: FormControlElement.ChangeEvent<HTMLInputElement, Element>
  ) => {
    setFilter((prev) => {
      return { ...prev, [e.target.name]: e.target.value };
    });
  };

  const handleRolesChange = (e: any) => {
    const selectedOptions = [...e.target.selectedOptions];
    let array = selectedOptions.map((option) => option.value);
    setFilter((prev) => {
      return { ...prev, roles: array };
    });
  };

  const handleClearFilter = () => {
    setFilter({
      email: "",
      telephoneNumber: "",
      fullName: "",
      roles: ["ADMIN", "PARENT", "COACH"],
      createdBefore: "",
      createdAfter: "",
    });
  };

  const handleDateChange = (
    e: FormControlElement.ChangeEvent<HTMLInputElement, Element>
  ) => {
    const date = e.target.value + "T00:00:00";
    setFilter((prev) => {
      return { ...prev, [e.target.name]: date };
    });
  };

  return (
    <Container>
      <h1 className="mb-3 text-center">Správa uživatelů</h1>
      <FlashMessage success={success} state={success} text={text} setTimer={success}/>
      <Button
        onClick={() => setIsOpen((prev) => !prev)}
        aria-controls="filter"
        aria-expanded={isOpen}
        className="mb-5"
      >{`${isOpen ? "Skrýt filter" : "Zobrazit filter"}`}</Button>
      <Collapse in={isOpen}>
        <div id="filter">
          <Form onSubmit={(e) => handleSubmit(e)}>
            <div
              className={`d-flex ${
                width ? "flex-row" : "flex-column"
              } mb-3 justify-content-between`}
            >
              <Form.Group className={`${width ? "" : "mb-3"}`}>
                <Form.Label>Email</Form.Label>
                <Form.Control
                  name="email"
                  value={filter.email}
                  onChange={(
                    e: FormControlElement.ChangeEvent<HTMLInputElement, Element>
                  ) => handleChange(e)}
                />
              </Form.Group>
              <Form.Group className={`${width ? "" : "mb-3"}`}>
                <Form.Label>Jméno</Form.Label>
                <Form.Control
                  name="fullName"
                  value={filter.fullName}
                  onChange={(
                    e: FormControlElement.ChangeEvent<HTMLInputElement, Element>
                  ) => handleChange(e)}
                />
              </Form.Group>
              <Form.Group className={`${width ? "" : "mb-3"}`}>
                <Form.Label>Tel</Form.Label>
                <Form.Control
                  name="telephoneNumber"
                  value={filter.telephoneNumber}
                  onChange={(
                    e: FormControlElement.ChangeEvent<HTMLInputElement, Element>
                  ) => handleChange(e)}
                />
              </Form.Group>
              <Form.Group className={`${width ? "" : "mb-3"}`}>
                <Form.Label>Role</Form.Label>
                <Form.Select
                  multiple={true}
                  value={filter.roles}
                  aria-label="Default select example"
                  onChange={(e) => handleRolesChange(e)}
                >
                  <option value="ADMIN">admin</option>
                  <option value="PARENT">parent</option>
                  <option value="COACH">coach</option>
                </Form.Select>
              </Form.Group>
              <Form.Group className={`${width ? "" : "mb-3"}`}>
                <Form.Label>Vytvořeno po</Form.Label>
                <Form.Control
                  onChange={(
                    e: FormControlElement.ChangeEvent<HTMLInputElement, Element>
                  ) => handleDateChange(e)}
                  value={useDateFormat(filter.createdAfter)}
                  name="createdAfter"
                  type="date"
                />
              </Form.Group>
              <Form.Group className={`${width ? "" : "mb-3"}`}>
                <Form.Label>Vytvořeno před</Form.Label>
                <Form.Control
                  onChange={(
                    e: FormControlElement.ChangeEvent<HTMLInputElement, Element>
                  ) => handleDateChange(e)}
                  value={useDateFormat(filter.createdBefore)}
                  name="createdBefore"
                  type="date"
                />
              </Form.Group>
            </div>

            <Form.Group>
              <Button className="me-2" type="submit" variant="primary">
                Filtruj
              </Button>
              <Button type="button" onClick={() => handleClearFilter()}>
                Vynuluj Filtr
              </Button>
            </Form.Group>
          </Form>
        </div>
      </Collapse>
      <div className="text-end mb-3 me-3">
        <Link className="btn btn-success" to={"/admin/uzivatele/novy"}>
          Nový uživatel{" "}
        </Link>
      </div>
      <Table>
        <thead>
          <tr>
            <th>id</th>
            <th>Jméno</th>
            <th>Email</th>
            <th>Tel</th>
            <th>Role</th>
            <th>Vytvořeno</th>
            <th>Akce</th>
          </tr>
        </thead>
        <tbody>
          {users?.map((user) => (
            <tr key={user.id}>
              <td>{user.id}</td>
              <td>{user.fullName}</td>
              <td>{user.email}</td>
              <td>{user.telephoneNumber}</td>
              <td>{user.roles.join(", ")}</td>
              <td>{formatDate(new Date(user.createdAt))}</td>
              <th>
                <p>
                  <Link className="btn btn-primary" to={"/admin/uzivatele/upravit/" + user.id}>
                    Upravit
                  </Link>
                </p>
                <p>
                  <Button variant="danger">Vymazat</Button>
                </p>
              </th>
            </tr>
          ))}
        </tbody>
      </Table>
    </Container>
  );
};

export default AdminUsers;
