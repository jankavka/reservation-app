import { useState } from "react";
import { Alert, Button, Container, Modal, Table } from "react-bootstrap";
import { Link, useLocation, useNavigate } from "react-router";
import { useMutation, useQuery } from "@tanstack/react-query";
import {
  deleteCoachMutation,
  getAllCoachesOptions,
} from "../../api/@tanstack/react-query.gen";
import Loading from "../../components/Loading";
import FlashMessage from "../../components/FlashMessages";
import type { CoachDto } from "../../api";

const AdminCoaches = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const flashText: string | undefined = location.state?.flash;
  const [coachToDelete, setCoachToDelete] = useState<CoachDto | null>(null);

  const { data, isPending, error, refetch } = useQuery(getAllCoachesOptions());
  const coaches = data ?? [];

  const deleteCoach = useMutation({
    ...deleteCoachMutation(),
    onSuccess: () => {
      navigate("/admin/treneri", {
        replace: true,
        state: { flash: `Trenér ${coachToDelete?.user.fullName} odebrán` },
      });
      setCoachToDelete(null);
      refetch();
    },
  });

  if (isPending) {
    return <Loading />;
  }

  if (error) {
    return (
      <Container>
        <Alert variant="danger">Nastala chyba: {error.message}</Alert>
      </Container>
    );
  }

  return (
    <Container>
      <h1 className="text-center mb-3">Trenéři</h1>

      <FlashMessage
        success={true}
        state={!!flashText}
        text={flashText}
        setTimer={!!flashText}
      />
      {deleteCoach.isError && (
        <Alert variant="danger">
          Smazání se nepovedlo: {deleteCoach.error.message}
        </Alert>
      )}

      <div className="text-end mb-3 me-3">
        <Link className="btn btn-success" to="/admin/treneri/novy">
          Nový trenér
        </Link>
      </div>

      {coaches.length === 0 ? (
        <p className="text-center text-muted">Zatím nejsou žádní trenéři.</p>
      ) : (
        <Table hover>
          <thead>
            <tr>
              <th>Id</th>
              <th>Jméno</th>
              <th>Email</th>
              <th>Tel</th>
              <th>Certifikace</th>
              <th>Akce</th>
            </tr>
          </thead>
          <tbody>
            {coaches.map((coach) => (
              <tr key={coach.id}>
                <td>{coach.id}</td>
                <td>
                  <Link to={"/admin/uzivatele/" + coach.user.id}>
                    {coach.user.fullName}
                  </Link>
                </td>
                <td>{coach.user.email}</td>
                <td>{coach.user.telephoneNumber}</td>
                <td>{coach.certification}</td>
                <td>
                  <div className="d-flex flex-column gap-2">
                    <Link
                      className="btn btn-warning"
                      to={"/admin/treneri/upravit/" + coach.id}
                    >
                      Upravit
                    </Link>
                    <Button
                      variant="danger"
                      onClick={() => setCoachToDelete(coach)}
                    >
                      Odebrat
                    </Button>
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </Table>
      )}

      <Modal show={!!coachToDelete} onHide={() => setCoachToDelete(null)}>
        <Modal.Header closeButton>
          <Modal.Title>Odebrat trenéra?</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <p>
            Opravdu chcete odebrat trenéra {coachToDelete?.user.fullName}?
          </p>
          <p className="text-muted small mb-0">
            Uživatelský účet zůstane zachován, odebere se jen role trenéra a
            trenér bude odpojen od svých skupin.
          </p>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setCoachToDelete(null)}>
            Zavřít
          </Button>
          <Button
            variant="danger"
            disabled={deleteCoach.isPending}
            onClick={() =>
              coachToDelete?.id &&
              deleteCoach.mutate({ path: { id: coachToDelete.id } })
            }
          >
            {deleteCoach.isPending ? "..." : "Odebrat"}
          </Button>
        </Modal.Footer>
      </Modal>
    </Container>
  );
};

export default AdminCoaches;
