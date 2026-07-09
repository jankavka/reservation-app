import { Container, Button, Form, FormGroup, FormLabel } from "react-bootstrap";
import { useNavigate } from "react-router";
import { useForm } from "@tanstack/react-form";
import type { CourtDto, VenueDto } from "../../api";
import {
  FormInputCheck,
  FormInput,
  FormInputSelect,
} from "../../components/FormFields";
import { useMutation, useQuery } from "@tanstack/react-query";
import {
  createCourtMutation,
  getAllVenuesOptions,
} from "../../api/@tanstack/react-query.gen";
import Loading from "../../components/Loading";
import { mySerializer } from "../../components/MySerializer";

const AdminCreateCourt = () => {
  const court: any = {
    name: " ",
    surface: null,
    indoor: false,
    lighting: false,
    venueId: null as number,
    photoUrl: " ",
  };
  const navigate = useNavigate();
  const { mutate } = useMutation({
    ...createCourtMutation({
      headers: { "Content-Type": null },
      bodySerializer: mySerializer,
    }),
    onSuccess: () => {
      navigate("/admin/kurty", { state: { createSuccess: true } });
    },
    onError: (error) => {
      console.error(error.message);
    },
  });
  const form = useForm({
    defaultValues: court,
    onSubmit: async ({ value }) => {
      const body: CourtDto = {
        venue: {
          id: value?.venue,
          name: null,
          address: null,
          phoneNumber: null,
        },
        name: value.name,
        surface: value.surface,
        indoor: value.indoor,
        lighting: value.lighting,
      };
      mutate({ body: { court: body, file: null } });
    },
  });

  const { data: venues } = useQuery({
    ...getAllVenuesOptions({}),
  });

  return (
    <Container style={{ maxWidth: "40dvw" }}>
      <div className="text-center">
        <h1>Vytvořit kurt</h1>
      </div>
      <Form
        onSubmit={(e) => {
          e.preventDefault();
          form.handleSubmit();
        }}
      >
        <div className="mb-3">
          <form.Field
            name="name"
            children={(field) => (
              <FormInput field={field} formLabel={"Jméno"} />
            )}
          />
        </div>
        <div className="mb-3">
          <form.Field
            name="surface"
            children={(field) => (
              <FormInputSelect
                formLabel={"Povrch"}
                field={field}
                options={["CLAY", "HARD", "WIMBLEDON"]}
              />
            )}
          />
        </div>
        <div className="mb-3">
          <form.Field
            name="indoor"
            children={(field) => (
              <FormInputCheck formLabel={"Vevnitř"} field={field} />
            )}
          />
        </div>
        <div className="mb-3">
          <form.Field
            name="lighting"
            children={(field) => (
              <FormInputCheck formLabel={"Osvětlení"} field={field} />
            )}
          />
        </div>
        <div className="mb-3">
          <form.Field
            name="venue"
            children={(field) => (
              <FormGroup>
                <FormLabel>Areál</FormLabel>
                <Form.Select
                  value={field.state?.value?.id || undefined}
                  onChange={(e: any) => field.handleChange(e.target.value)}
                >
                  <option value={""}>Vyberte areál</option>
                  {venues?.map((item: VenueDto) => (
                    <option key={item?.id} value={item.id}>
                      {item.name}
                    </option>
                  ))}
                </Form.Select>
              </FormGroup>
            )}
          />
        </div>
        <form.Subscribe
          selector={(state) => [state.canSubmit, state.isSubmitting]}
          children={([canSubmit, isSubmitting]) => (
            <div>
              <div className="mt-3">
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
  );
};

export default AdminCreateCourt;
