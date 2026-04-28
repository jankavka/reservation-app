const FlashMessage = ({ success, state, text }) => {
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

  export default FlashMessage
