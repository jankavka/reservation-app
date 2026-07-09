import {
  Container,
  Table,
  Button,
  Form,
  Collapse,
  Modal,
} from "react-bootstrap";
import { useMutation, useQuery } from "@tanstack/react-query";
import type { UserFilter } from "../../api";
import { useState } from "react";
import { Link, useLocation, useNavigate } from "react-router";
import formatDate from "../../components/DateFormat";
import FormControlElement from "react";
import useDateFormat from "../../hooks/useDateFormat";
import FlashMessage from "../../components/FlashMessages";
import {
  deleteUserMutation,
  getAllUsersOptions,
} from "../../api/@tanstack/react-query.gen";
import Loading from "../../components/Loading";

const AdminUsers = () => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [isModalVisible, setIsModalVisible] = useState<boolean>(false);
  const [currentUserName, setCurrentUserName] = useState<string>("");
  const [currentId, setCurrentId] = useState<number>(null);
  const location = useLocation();
  const navigate = useNavigate();
  const success: boolean = location.state?.success || false;
  const text: string = `Uživatel ${location.state?.user} úspěšně upraven`;
  const width = window.innerWidth > 600;
  const [filter, setFilter] = useState<UserFilter>({
    email: "",
    telephoneNumber: "",
    fullName: "",
    roles: ["ADMIN", "PARENT", "COACH", "PLAYER"],
    createdBefore: "",
    createdAfter: "",
  });
  const [currentFilter, setCurrentFilter] = useState<UserFilter>(null);

  const { data, refetch, isPending } = useQuery({
    ...getAllUsersOptions({ query: { userFilter: currentFilter } }),
  });

  //TODO: Make a useMutation for sending filter data on server

  const deleteUser = useMutation({
    ...deleteUserMutation(),
    onSuccess: () => {
      navigate("/admin/uzivatele", {
        state: { success: true, name: currentUserName },
      });
      refetch();
    },
  });

  const handleSubmitFilter = (e: any) => {
    e.preventDefault();
    setCurrentFilter(filter);
  };

  const handleShowModal = (name: string, id: number) => {
    setCurrentId(id);
    setCurrentUserName(name);
    setIsModalVisible(true);
  };

  const handleDeleteUser = (id: number) => {
    deleteUser.mutate({ path: { id: id } });
    setCurrentId(null);
  };

  const handleChange = (
    e: FormControlElement.ChangeEvent<HTMLInputElement, Element>
  ) => {
    setFilter((prev) => {
      return { ...prev, [e.target.name]: e.target.value };
    });
  };

  const handleRolesChange = (
    e: React.ChangeEvent<HTMLSelectElement, HTMLSelectElement>
  ) => {
    const selectedOptions = [...e.target.selectedOptions];
    let array = selectedOptions.map((option) => option.value);
    setFilter((prev: any) => {
      return { ...prev, roles: array };
    });
  };

  const handleClearFilter = () => {
    setCurrentFilter(null);
    setFilter({
      email: "",
      telephoneNumber: "",
      fullName: "",
      roles: ["ADMIN", "PARENT", "COACH", "PLAYER"],
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

  if (isPending) {
    return <Loading />;
  }

  return (
    <Container>
      <h1 className="mb-3 text-center">Správa uživatelů</h1>
      <FlashMessage
        success={success}
        state={success}
        text={text}
        setTimer={success}
      />
      <Button
        onClick={() => setIsOpen((prev) => !prev)}
        aria-controls="filter"
        aria-expanded={isOpen}
        className="mb-5"
      >{`${isOpen ? "Skrýt filter" : "Zobrazit filter"}`}</Button>
      <Collapse in={isOpen}>
        <div id="filter">
          <Form onSubmit={(e) => handleSubmitFilter(e)}>
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
                  <option value="PLAYER">player</option>
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
          {data?.map((user) => (
            <tr key={user.id}>
              <td>{user.id}</td>
              <td>{user.fullName}</td>
              <td>{user.email}</td>
              <td>{user.telephoneNumber}</td>
              <td>{user.roles.join(", ")}</td>
              <td>{formatDate(new Date(user.createdAt))}</td>
              <th
                className="d-flex flex-column justify-content-between"
                style={{ minHeight: "18dvh" }}
              >
                <Link
                  className="btn btn-warning"
                  to={"/admin/uzivatele/upravit/" + user.id}
                >
                  Upravit
                </Link>

                <Button
                  variant="danger"
                  onClick={() => handleShowModal(user.fullName, user.id)}
                >
                  Vymazat
                </Button>
                <Button
                  onClick={() => navigate("/admin/uzivatele/" + user.id)}
                  variant={"secondary"}
                >
                  Detail
                </Button>
              </th>
            </tr>
          ))}
        </tbody>
      </Table>
      <Modal show={isModalVisible} onHide={() => setIsModalVisible(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Vymazat areál?</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>Určitě chcete vymazat areál s názvem {currentUserName}</p>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setIsModalVisible(false)}>
            Zavřít
          </Button>
          <Button
            variant="primary"
            onClick={() => {
              handleDeleteUser(currentId);
              setIsModalVisible(false);
            }}
          >
            Vymazat
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default AdminUsers;
