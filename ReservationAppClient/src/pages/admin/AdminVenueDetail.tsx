import { Container, Table, Button } from "react-bootstrap";
import { useNavigate, useParams } from "react-router";
import { getVenueOptions } from "../../api/@tanstack/react-query.gen";
import Loading from "../../components/Loading";
import { useQuery } from "@tanstack/react-query";
import { API_URL } from "../../constant/constant";
import { useState } from "react";
import Lightbox from "yet-another-react-lightbox";

const AdminVenueDetail = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const { data, isPending, isError, error } = useQuery({
    ...getVenueOptions({ path: { id: parseInt(id) } }),
  });

  if (isPending) {
    return <Loading />;
  }

  if (isError) {
    return (
      <Container>
        <p>Nastala chyba: {error.message}</p>
      </Container>
    );
  }

  return (
    <Container>
      <h1>{data.name}</h1>
      <Table>
        <tbody>
          <tr>
            <td>Id</td>
            <td>{data.id}</td>
          </tr>
          <tr>
            <td>Adresa</td>
            <td>{data.address}</td>
          </tr>
          <tr>
            <td>Telefonní číslo</td>
            <td>{data.phoneNumber}</td>
          </tr>
          <tr>
            <td>Foto</td>
            <td>
              <img
              onClick={() => {
                setIsOpen((prev) => !prev);
              }}
                width={"200dvw"}
                src={API_URL + data.photoUrl}
                alt={data.name}
              />
            </td>
          </tr>
        </tbody>
      </Table>
      <Button onClick={() => navigate(-1)}>Zpět</Button>
      <Lightbox
        open={isOpen}
        close={() => setIsOpen(false)}
        slides={[{src: API_URL + data.photoUrl}]}
      />
    </Container>
  );
};

export default AdminVenueDetail;
