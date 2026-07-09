import { Container, Table, Button } from "react-bootstrap";
import { useQuery } from "@tanstack/react-query";
import { getInfoOptions } from "../../api/@tanstack/react-query.gen";
import Loading from "../../components/Loading";
import { useNavigate } from "react-router";

const AdminInfo = () => {
  const navigate = useNavigate();
  const { data, isPending } = useQuery({
    ...getInfoOptions({}),
  });

  if (isPending) {
    return (
      <Container className="text-center">
        <Loading />
      </Container>
    );
  }

  return (
    <Container>
      <div className="mb-5 mt-3">
        <div className="text-center">
          <h1>Popis</h1>
        </div>
        <div className="d-flex flex-column justify-content-center align-items-center">
          <div
            style={{ maxWidth: "60dvw" }}
            dangerouslySetInnerHTML={{ __html: data.description }}
          ></div>
          <div className="text-start" style={{ width: "60dvw" }}>
            <Button onClick={() => navigate("/admin/info/upravit")}>
              Upravit popis
            </Button>
          </div>
        </div>
      </div>
      <div>
        <div className="text-center">
          <h1 className="mb-3">Kontakty</h1>
        </div>
        <div
          className="d-flex flex-column justify-content-center align-items-center"
          style={{ minHeight: "60dvh" }}
        >
          <Table style={{ maxWidth: "60dvw" }}>
            <tbody>
              <tr>
                <td>Název</td>
                <td>{data.companyName}</td>
              </tr>
              <tr>
                <td>Adresa</td>
                <td>{data.address}</td>
              </tr>
              <tr>
                <td>Email</td>
                <td>{data.email}</td>
              </tr>
              <tr>
                <td>Tax number</td>
                <td>{data.taxNumber}</td>
              </tr>
              <tr>
                <td>Telefón</td>
                <td>{data.telNumber}</td>
              </tr>
              <tr>
                <td>Číslo účtu</td>
                <td>{data.bankAccount}</td>
              </tr>
              <tr>
                <td>IBAN</td>
                <td>{data.bankAccountInternationalFormat}</td>
              </tr>
              <tr>
                <td>DIČ</td>
                <td>{data.taxNumber}</td>
              </tr>
              <tr>
                <td>IČO</td>
                <td>{data.businessId}</td>
              </tr>
            </tbody>
          </Table>
          <div className="text-start" style={{ width: "60dvw" }}>
            <Button onClick={() => navigate("/admin/kontakty/upravit")}>
              Upravit kontakty
            </Button>
          </div>
        </div>
      </div>
    </Container>
  );
};

export default AdminInfo;
