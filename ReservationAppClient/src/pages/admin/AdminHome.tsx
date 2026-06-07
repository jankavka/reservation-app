import { Container } from "react-bootstrap";
import FlashMessage from "../../components/FlashMessages";
import { useLocation } from "react-router";

const AdminHome = () => {
  const location = useLocation();
  const state = false || location.state?.successState;
  const text = `Administrátor ${location.state?.name} úspěšně přihlášen`;

  return (
    <Container className="text-center">
      <h1>Welcome on Admin Home Page</h1>
      <FlashMessage success={true} state={state} text={text} setTimer={state} />
    </Container>
  );
};

export default AdminHome;
