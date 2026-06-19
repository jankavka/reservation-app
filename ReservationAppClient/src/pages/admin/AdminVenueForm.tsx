import {
  Container,
  FormControl,
  FormLabel,
  Form,
  Button,
} from "react-bootstrap";
import { useForm } from "@tanstack/react-form";
import { createVenueMutation } from "../../api/@tanstack/react-query.gen";
import { useMutation } from "@tanstack/react-query";
import { useNavigate } from "react-router";
import Loading from "../../components/Loading";
import { useState } from "react";
import FlashMessage from "../../components/FlashMessages";
import { mySerializer } from "../../components/MySerializer";

const AdminVenueForm = () => {
  const navigate = useNavigate();
  const [errorMessage, setErrorMessage] = useState<string>("");
  const [isError, setIsError] = useState<boolean>(false);
  const mutation = useMutation({
    ...createVenueMutation({
      bodySerializer: mySerializer,
      headers: { "Content-Type": null },
    }),
    onError: (error) => {
      var message = Object.values(error).join(", ");
      console.error(message);
      setErrorMessage(message);
      setIsError(true);
    },
    onSuccess: (data) => {
      navigate("/admin/arealy", {
        state: { success: "true", name: data.name },
      });
    },
  });
  const form = useForm({
    defaultValues: {
      name: "",
      address: "",
      phoneNumber: "",
      file: null as Blob,
    },
    onSubmit: async ({ value }) => {
      mutation.mutate({ body: { venue: value, file: value.file } });
    },
  });

  return (
    <div>
      <Container style={{ maxWidth: "70dvw" }}>
        <h1 className="mb-5">Nový areál</h1>
        <FlashMessage success={false} state={isError} text={errorMessage} />
        <Form
          onSubmit={(e) => {
            e.preventDefault();
            form.handleSubmit();
          }}

        >
          <form.Field
            name="name"
            children={(field: any) => (
              <Form.Group className="mb-3">
                <FormLabel>Název</FormLabel>
                <FormControl
                  value={field.state.value}
                  onBlur={field.handleBlur}
                  onChange={(e) => field.handleChange(e.target.value)}
                />
              </Form.Group>
            )}
          />
          <form.Field
            name="address"
            children={(field: any) => (
              <Form.Group className="mb-3">
                <Form.Label>Adresa</Form.Label>
                <FormControl
                  value={field.state.value}
                  onBlur={field.handleBlur}
                  onChange={(e) => field.handleChange(e.target.value)}
                />
              </Form.Group>
            )}
          />
          <form.Field
            name="phoneNumber"
            children={(field: any) => (
              <Form.Group className="mb-3">
                <Form.Label>Telefonní číslo</Form.Label>
                <Form.Control
                  value={field.state.value}
                  onBlur={field.handleBlur}
                  onChange={(e) => field.handleChange(e.target.value)}
                />
              </Form.Group>
            )}
          />
          <form.Field
            name="file"
            children={(field: any) => (
              <Form.Group className="mb-3">
                <Form.Label>Fotka</Form.Label>
                <Form.Control
                  type="file"
                  value={field.state.filename}
                  onBlur={field.handleBlur}
                  onChange={(e?: React.ChangeEvent) => {
                    const target = e.target as HTMLInputElement;
                    field.handleChange(target.files[0]);
                  }}
                />
              </Form.Group>
            )}
          />
          <form.Subscribe
            selector={(state) => [state.canSubmit, state.isSubmitting]}
            children={([canSubmit, isSubmitting]) => (
              <div>
                <div>
                  <Button disabled={!canSubmit} type="submit" className="me-2">
                    {isSubmitting ? "..." : "Odeslat"}
                  </Button>
                  <Button type="button" onClick={() => navigate(-1)}>
                    Zpět
                  </Button>
                </div>
                <div>{isSubmitting ? <Loading /> : null}</div>
              </div>
            )}
          />
        </Form>
      </Container>
    </div>
  );
};

export default AdminVenueForm;
