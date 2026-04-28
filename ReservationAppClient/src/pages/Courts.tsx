import { Container, Table } from "react-bootstrap";
import { useState } from "react";
import { showAllCourts, type CourtDto } from "../api";
import { useQuery } from "@tanstack/react-query";

const Courts = () => {
  const [courts, setCourts] = useState<CourtDto[]>();
  const [isSet, setIsSet] = useState<boolean>(false);
  const callback = () => showAllCourts({ query: null });
  const courtData = useQuery({
    queryKey: ["courts"],
    queryFn: callback,
  });

  if (!isSet && courtData.data?.data) {
    setCourts(courtData.data.data);
    setIsSet((prev) => !prev);
  }

  const showSurface = (surface: String) => {
    if (surface == "HARD") return "beton";
  };

  return (
    <>
      <Container>
        <h1 className="text-center">Kurty</h1>
        {courts?.map((court, index) => (
          <div key={index} style={{ maxWidth: "30dvw" }}>
            <Table>
              <h1>{court.name}</h1>
              <tbody>
                <tr>
                  <td>Povrch</td>
                  <td>{showSurface(court.surface)}</td>
                </tr>
                <tr>
                    <td>Areál</td>
                    <td>{court.venue.name}</td>
                </tr>
                <tr>
                  <td>Světla</td>
                  <td>{court.lighting ? "ANO" : "NE"}</td>
                </tr>
                <tr>
                  <td>Hala</td>
                  <td>{court.indoor ? "ANO" : "NE"}</td>
                </tr>
              </tbody>
            </Table>
          </div>
        ))}
      </Container>
    </>
  );
};

export default Courts;
