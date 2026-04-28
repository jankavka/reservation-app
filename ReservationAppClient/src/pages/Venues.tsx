import { useState } from "react";
import { Container, Table } from "react-bootstrap";
import { getAllVenues, type VenueDto } from "../api";
import { useQuery } from "@tanstack/react-query";

const Venues = () => {
  const [venues, setVenues] = useState<VenueDto[]>();
  const callback = () => getAllVenues();
  const [isSet, setIsSet] = useState<boolean>(false);
  const data = useQuery({ queryKey: ["venue"], queryFn: callback });

  if (data.isSuccess && !isSet) {
    setVenues(data.data.data);
    setIsSet((prev) => !prev);
  }

  return (
    <Container>

      <h1 className="text-center mb-5">Areály</h1>
      {venues?.map((venue) => (
        <div style={{maxWidth: "50dvh"}} key={venue.id}>
          <h3>{venue.name}</h3>
          <Table>
            <tbody>
              <tr>
                <td>Adresa</td>
                <td>{venue.address}</td>
              </tr>
              <tr>
                <td>Telefonní číslo</td>
                <td>{venue.phoneNumber}</td>
              </tr>
            </tbody>
          </Table>
        </div>
      ))}
    </Container>
  );
};

export default Venues;
