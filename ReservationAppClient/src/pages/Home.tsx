import { Container } from "react-bootstrap";
import FlashMessage from "../components/FlashMessages";
import { useLocation } from "react-router";

const Home = () => {
  const location = useLocation();
  const succeSstate = location.state?.successState;
  const text = `Uživatel ${location.state?.name} úspěšně přihlášen`;

  return (
    <div>
      <Container className="text-center">
        <div className="mt-3">
          <FlashMessage
            success={true}
            state={succeSstate}
            text={text}
            setTimer={true}
          ></FlashMessage>
        </div>
        <h1>Vítejte na úvodní stránce</h1>
      </Container>
    </div>
  );
};

export default Home;
