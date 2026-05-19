import { Container } from "react-bootstrap";
import FlashMessage from "../components/FlashMessages";
import { useLocation } from "react-router";

const Home = () => {
  const location = useLocation();
  const successState = location.state?.successState;
  const logoutSuccess = false || location.state?.logoutSuccess;
  const logoutText = location.state?.logoutText;
  const text = `Uživatel ${location.state?.name} úspěšně přihlášen`;

  return (
    <div>
      <Container className="text-center">
        <div className="mt-3">
          <FlashMessage
            success={true}
            state={successState}
            text={text}
            setTimer={successState}
          />
        </div>
        <div>
          <FlashMessage
            success={true}
            state={logoutSuccess}
            text={logoutText}
            setTimer={logoutSuccess}
          />
        </div>
        <h1>Vítejte na úvodní stránce</h1>
      </Container>
    </div>
  );
};

export default Home;
