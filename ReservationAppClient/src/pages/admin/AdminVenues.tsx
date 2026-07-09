import { Container, Table, Button, Modal } from "react-bootstrap";
import { Link, useLocation, useNavigate } from "react-router";
import FlashMessage from "../../components/FlashMessages";
import {
  deleteVenueMutation,
  getAllVenuesOptions,
} from "../../api/@tanstack/react-query.gen";
import { useMutation, useQuery } from "@tanstack/react-query";
import Loading from "../../components/Loading";
import { useState } from "react";
import Lightbox from "yet-another-react-lightbox";
import { API_URL } from "../../constant/constant";

const AdminVenues = () => {
  const [index, setIndex] = useState<number>(0);
  const [currentArealName, setCurrentArealName] = useState<string>("");
  const [currentId, setCurrentId] = useState<number>(null);
  const navigate = useNavigate();
  const location = useLocation();
  const [isModalVisible, setIsModalVisible] = useState<boolean>(false);
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const deleteSuccess = location.state?.deleteSuccess || false;
  const createSuccess = location.state?.success || false;
  const editSuccess = location.state?.editSuccess || false;
  const deleteText = `Areál s názvem ${location.state?.name} úspěšně vymazán`;
  const createText = `Areál s názvem ${location.state?.name} úspěšně vytvořen`;
  const editText = `Areál s názvem ${location.state?.nameOfEdited} úspěšně upraven`;
  const { data, isError, isPending, refetch } = useQuery({
    ...getAllVenuesOptions({}),
  });

  const deleteVenue = useMutation({
    ...deleteVenueMutation({}),
    onSuccess: () => {
      navigate("/admin/arealy", {
        state: { deleteSuccess: true, name: currentArealName },
      });
      refetch();
    },
    onError: (error) => {
      navigate("/admin/arealy");
      console.error(error.message);
    },
  });

  if (isPending) {
    return <Loading />;
  }

  if (isError) {
    return (
      <FlashMessage
        success={false}
        state={isError}
        text={"Nepodařilo se načíst data"}
      />
    );
  }

  const handleShowModal = (name: string, id: number) => {
    setCurrentId(id);
    setCurrentArealName(name);
    setIsModalVisible(true);
  };

  const handleDeleteVenue = (id: number): void => {
    deleteVenue.mutate({ path: { id: id } });
    setCurrentId(null);
  };

  return (
    <Container className="text-center">
      <h1>Areály</h1>
      <FlashMessage
        success={createSuccess}
        state={createSuccess}
        text={createText}
        setTimer={createSuccess}
      />
      <FlashMessage
        success={editSuccess}
        state={editSuccess}
        text={editText}
        setTimer={editSuccess}
      />
      <FlashMessage
        success={deleteSuccess}
        state={deleteSuccess}
        text={deleteText}
        setTimer={deleteSuccess}
      />
      <div className="text-end mb-3 me-5">
        <Link className="btn btn-success" to={"/admin/arealy/novy"}>
          Nový areál
        </Link>
      </div>
      <Table hover={true}>
        <thead>
          <tr>
            <th>id</th>
            <th>Název</th>
            <th>Adresa</th>
            <th>Tel. číslo</th>
            <th>Foto</th>
            <th>Akce</th>
          </tr>
        </thead>
        <tbody>
          {data
            ?.toSorted((venue1, venue2) => venue1.id - venue2.id)
            .map((item, index) => (
              <tr key={item.id}>
                <td>{item.id}</td>
                <td>{item.name}</td>
                <td>{item.address}</td>
                <td>{item.phoneNumber}</td>
                <td>
                  <img
                    style={{ maxWidth: "10dvw" }}
                    src={API_URL + item.photoUrl}
                    alt={item.name}
                    onClick={() => {
                      setIsOpen((prev) => !prev);
                      setIndex(index);
                    }}
                  />
                </td>
                <td>
                  <div
                    className="d-flex flex-column justify-content-between"
                    style={{ minHeight: "15dvh" }}
                  >
                    <Button
                      onClick={() => handleShowModal(item.name, item.id)}
                      variant="danger"
                    >
                      Vymazat
                    </Button>
                    <Link
                      className="btn btn-warning"
                      to={`/admin/arealy/upravit/${item.id}`}
                    >
                      Upravit
                    </Link>
                    <Button
                    variant="secondary"
                      onClick={() => navigate("/admin/arealy/" + item.id)}
                    >
                      Detail
                    </Button>
                  </div>
                </td>
              </tr>
            ))}
        </tbody>
      </Table>
      <Lightbox
        open={isOpen}
        index={index}
        close={() => setIsOpen(false)}
        slides={data.map((item) => {
          return { src: API_URL + item.photoUrl };
        })}
      />
      <Modal show={isModalVisible} onHide={() => setIsModalVisible(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Vymazat areál?</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>Určitě chcete vymazat areál s názvem {currentArealName}</p>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setIsModalVisible(false)}>
            Zavřít
          </Button>
          <Button
            variant="primary"
            onClick={() => {
              handleDeleteVenue(currentId);
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

export default AdminVenues;
