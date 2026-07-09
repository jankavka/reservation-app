import { Container, Table, Button } from "react-bootstrap";
import { useQuery } from "@tanstack/react-query";
import { getUserOptions } from "../../api/@tanstack/react-query.gen";
import { useNavigate, useParams } from "react-router";
import Loading from "../../components/Loading";
import formatDate from "../../components/DateFormat";

const AdminUserDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();

  const { data, isPending, isError, error } = useQuery({
    ...getUserOptions({ path: { id: parseInt(id) } }),
  });

  if (isPending) {
    return <Loading />;
  }

  if (isError) {
    <Container>
      <p>Nastala chyba: {error.message}</p>
    </Container>;
  }

  console.log(data);

  return (
    <Container>
      <div className="mb-5">
        <h1>Detail uživatele: {data.fullName}</h1>
      </div>
      <Table style={{ width: "50dvw" }}>
        <tbody>
          <tr>
            <td>Id</td>
            <td>{data?.id}</td>
          </tr>
          <tr>
            <td>Email</td>
            <td>{data?.email}</td>
          </tr>
          <tr>
            <td>Telefon</td>
            <td>{data?.telephoneNumber}</td>
          </tr>
          <tr>
            <td>Role</td>
            <td>{data?.roles.join(", ")}</td>
          </tr>
          <tr>
            <td>Vytvořeno</td>
            <td>{formatDate(new Date(data.createdAt))}</td>
          </tr>
        </tbody>
      </Table>
      <div>
        <Button onClick={() => navigate(-1)}>Zpět</Button>
      </div>
    </Container>
  );
};

export default AdminUserDetail;
