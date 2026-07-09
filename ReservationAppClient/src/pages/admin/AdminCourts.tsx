import { Container, Table, Button } from "react-bootstrap";
import { useQuery } from "@tanstack/react-query";
import { showAllCourtsOptions } from "../../api/@tanstack/react-query.gen";
import type { CourtFilter } from "../../api";
import Loading from "../../components/Loading";
import { useNavigate } from "react-router";
import { Link } from "react-router";

const AdminCourts = () => {
  const navigate = useNavigate();
  const filter: CourtFilter = {
    venueId: 1,
    name: " ",
    surface: "CLAY",
    indoor: false,
    lighting: false,
  };

  const { data, isPending, isError, error, refetch } = useQuery({
    ...showAllCourtsOptions({ query: { courtFilter: null } }),
  });

  const showCourtDetail = (id: number) => {
    navigate("/admin/kurty/" + id);
  };

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
    <Container className="text-center">
      <h1>Kurty</h1>
      <div className="text-end me-5 mb-3">
        <Button variant="success" onClick={() => navigate("/admin/kurty/novy")}>
          Nový kurt
        </Button>
      </div>
      <Table hover={true}>
        <thead>
          <tr>
            <th>Id</th>
            <th>Jméno</th>
            <th>Areál</th>
            <th>Povrch</th>
            <th>Uvnitř</th>
            <th>Osvětlení</th>
            <th>Akce</th>
          </tr>
        </thead>
        <tbody>
          {data.map((item, _) => (
            <tr key={item.id}>
              <td>{item.id}</td>
              <td>{item.name}</td>
              <td>
                <Link to={"/admin/arealy/" + item.venue.id}>
                  {item.venue.name}
                </Link>
              </td>
              <td>{item.surface}</td>
              <td>{item.indoor === true ? "ANO" : "NE"}</td>
              <td>{item.lighting === true ? "ANO" : "NE"}</td>
              <td
                className=" d-flex flex-column justify-content-between"
                style={{ minHeight: "18dvh" }}
              >
                <Button variant={"warning"}>Upravit</Button>
                <Button variant={"danger"}>Vymazat</Button>
                <Button
                  onClick={() => showCourtDetail(item.id)}
                  variant={"secondary"}
                >
                  Detail
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </Container>
  );
};

export default AdminCourts;
