import { useQuery } from "@tanstack/react-query";
import { Container, Table, Button } from "react-bootstrap";
import { showCourtOptions } from "../../api/@tanstack/react-query.gen";
import { useNavigate, useParams, Link } from "react-router";
import Loading from "../../components/Loading";

const AdminCourtDetail = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  const { data, isPending, isError, error } = useQuery({
    ...showCourtOptions({ path: { id: parseInt(id) } }),
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
      <h1>{data?.name}</h1>
      <Table>
        <tbody>
          <tr>
            <td>Povrch</td>
            <td>{data?.surface}</td>
          </tr>
          <tr>
            <td>Uvnitř</td>
            <td>{data?.indoor ? "ANO" : "NE"}</td>
          </tr>
          <tr>
            <td>Osvětlení</td>
            <td>{data?.lighting ? "ANO" : "NE"}</td>
          </tr>
          <tr>
            <td>Areál</td>
            <td>
              <Link to={"/admin/arealy/" + data?.venue.id}>
                {data?.venue.name}
              </Link>
            </td>
          </tr>
        </tbody>
      </Table>
      <Button
        onClick={(e) => {
          console.log(e);
          navigate(-1);
        }}
      >
        Zpět
      </Button>
    </Container>
  );
};

export default AdminCourtDetail;
