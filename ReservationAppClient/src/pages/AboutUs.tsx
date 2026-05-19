import { Container } from "react-bootstrap";
import { getInfoOptions } from "../api/@tanstack/react-query.gen";
import { useQuery } from "@tanstack/react-query";

const AboutUs = () => {
  const { data, error } = useQuery({
    ...getInfoOptions({}),
  });

  if (error) {
    <>
      <Container>
        <h1 className="text-center">O nás</h1>
        <p>Nepodařilo se načíst data</p>
        <p>Error: {error.message}</p>
      </Container>
    </>;
  }

  return (
    <>
      <Container>
        <h1 className="text-center">O nás</h1>
        <div>
          <p>{data?.description}</p>
        </div>
      </Container>
    </>
  );
};

export default AboutUs;
