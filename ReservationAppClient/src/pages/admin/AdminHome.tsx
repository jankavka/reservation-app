import { Container } from "react-bootstrap";
import FlashMessage from "../../components/FlashMessages";
import { useLocation } from "react-router";

const AdminHome = () => {
  const location = useLocation();
  const state = false || location.state?.successState;
  const adminLoginSuccessState = `Administrátor ${location.state?.name} úspěšně přihlášen`;
  const registrationSuccessText = `Uživatel ${location.state?.username} úspěšně zaregistrován `;
  const success = location.state?.success;

  return (
    <Container className="text-center">
      <h1>Welcome on Admin Home Page</h1>
      <FlashMessage
        success={true}
        state={state}
        text={adminLoginSuccessState}
        setTimer={state}
      />
      <FlashMessage
        success={true}
        state={success}
        text={registrationSuccessText}
        setTimer={success}
      />
    </Container>
  );
};

export default AdminHome;
