import { Alert, Card, Col, Container, Row } from "react-bootstrap";
import { useQuery } from "@tanstack/react-query";
import { getAllCoachesOptions } from "../api/@tanstack/react-query.gen";
import Loading from "../components/Loading";

const Coaches = () => {
  const { data, isPending, error } = useQuery(getAllCoachesOptions());

  const coaches = data ?? [];

  return (
    <Container className="mb-5">
      <h1 className="text-center mb-5">Trenéři</h1>

      {isPending && (
        <div className="text-center">
          <Loading />
        </div>
      )}

      {error && (
        <Alert variant="danger">
          Nepodařilo se načíst trenéry: {error.message}
        </Alert>
      )}

      {!isPending && !error && coaches.length === 0 && (
        <p className="text-center text-muted">Zatím zde nejsou žádní trenéři.</p>
      )}

      <Row xs={1} md={2} lg={3} className="g-4">
        {coaches.map((coach) => (
          <Col key={coach.id}>
            <Card className="h-100 shadow-sm">
              <Card.Body>
                <Card.Title>{coach.user?.fullName}</Card.Title>
                {coach.certification && (
                  <Card.Subtitle className="mb-3 text-muted">
                    {coach.certification}
                  </Card.Subtitle>
                )}
                <Card.Text style={{ whiteSpace: "pre-line" }}>
                  {coach.bio}
                </Card.Text>
              </Card.Body>
              {(coach.user?.email || coach.user?.telephoneNumber) && (
                <Card.Footer className="small">
                  {coach.user.email && (
                    <div>
                      <a href={`mailto:${coach.user.email}`}>{coach.user.email}</a>
                    </div>
                  )}
                  {coach.user.telephoneNumber && (
                    <div>
                      <a href={`tel:${coach.user.telephoneNumber}`}>
                        {coach.user.telephoneNumber}
                      </a>
                    </div>
                  )}
                </Card.Footer>
              )}
            </Card>
          </Col>
        ))}
      </Row>
    </Container>
  );
};

export default Coaches;
