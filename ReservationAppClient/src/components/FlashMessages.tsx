import { useEffect, useState } from "react";

// vyřešit prblém s props.state a currentState
const FlashMessage = ({ success, state, text, setTimer = false }) => {
  const [showComponent, setShowComponent] = useState<boolean>(true);

  useEffect(() => {
    if (setTimer) {
      const timer = setTimeout(() => setShowComponent(false), 5000);

      return () => clearTimeout(timer);
    }
  }, [setTimer]);

  if (!showComponent) {
    return <div></div>;
  }

  return (
    <div>
      {success ? (
        state ? (
          <div className="alert alert-success">{text}</div>
        ) : null
      ) : state ? (
        <div className="alert alert-danger">{text}</div>
      ) : null}
    </div>
  );
};

export default FlashMessage;
