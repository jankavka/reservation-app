import { Container, Table } from "react-bootstrap";
import { useQuery } from "@tanstack/react-query";
import { getInfoOptions } from "../api/@tanstack/react-query.gen";

const Contacts = () => {
  const { data, error } = useQuery({
    ...getInfoOptions({}),
  });

  if (error) {
    return (
      <>
        <Container>
          <h1 className="text-center">Kontakty</h1>
          <p>Nepodařilo se načíst data</p>
          <p>Error: {error.message}</p>
        </Container>
      </>
    );
  }

  return (
    <>
      <Container style={{maxWidth: "80dvw"}} fluid={"xxl"}>
        <h1 className="text-center">Kontakty</h1>
        <Table>
          <tbody>
            <tr>
              <td>Název</td>
              <td>{data?.companyName}</td>
            </tr>
            <tr>
              <td>Adresa</td>
              <td>{data?.address}</td>
            </tr>
            <tr>
              <td>Email</td>
              <td>{data?.email}</td>
            </tr>
            <tr>
                <td>Tel. číslo</td>
                <td>{data?.telNumber}</td>
              </tr>
              <tr>
                <td>čísl účtu</td>
                <td>{data?.bankAccount}</td>
              </tr>
              <tr>
                <td>IBAN</td>
                <td>{data?.bankAccountInternationalFormat}</td>
              </tr>
          </tbody>
        </Table>
      </Container>
    </>
  );
};

export default Contacts;
