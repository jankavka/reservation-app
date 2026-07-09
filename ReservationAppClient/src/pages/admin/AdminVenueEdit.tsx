import {
  Container,
  Form,
  FormGroup,
  FormLabel,
  FormControl,
  Button,
} from "react-bootstrap";
import { useForm } from "@tanstack/react-form";
import { useQuery, useMutation } from "@tanstack/react-query";
import {
  editVenueMutation,
  getVenueOptions,
} from "../../api/@tanstack/react-query.gen";
import { useParams, useNavigate } from "react-router";
import Loading from "../../components/Loading";
import { translate } from "../../constant/constant";
import { mySerializer } from "../../components/MySerializer";
import type { VenueDto } from "../../api";

const AdminVenueEdit = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { data } = useQuery({
    ...getVenueOptions({ path: { id: parseInt(id) } }),
  });

  const defaultValues: Record<string, unknown> = {
    name: data?.name || " ",
    address: data?.address || " ",
    phoneNumber: data?.phoneNumber || " ",
    file: new Blob(),
  };

  const fields: string[] = Object.keys(defaultValues);

  const mutation = useMutation({
    ...editVenueMutation({
      headers: { "Content-Type": null },
      bodySerializer: mySerializer,
    }),
    onSuccess: () => {
      navigate("/admin/arealy", {
        state: { editSuccess: true, nameOfEdited: defaultValues?.name },
      });
    },
    onError: (error) => {
      console.log(error);
    },
  });

  const form = useForm({
    defaultValues: defaultValues,
    onSubmit: async ({ value }) => {
      mutation.mutate({
        body: { venue: value as VenueDto, file: value.file as File },
        path: { id: parseInt(id) },
      });
    },
  });

  return (
    <div>
      <Container>
        <h1>Upravit Areál</h1>
        <Form
          onSubmit={(e) => {
            e.preventDefault();
            form.handleSubmit();
          }}
        >
          {fields.map((item: string, index) => {
            if (typeof defaultValues?.[item] === "string") {
              return (
                <div key={index} className="mb-3">
                  <form.Field
                    name={item}
                    children={(field: any) => (
                      <FormGroup>
                        <FormLabel>{translate?.[item]}</FormLabel>
                        <FormControl
                          value={field.state.value}
                          onBlur={field.handleBlur}
                          onChange={(e) => field.handleChange(e.target.value)}
                        />
                      </FormGroup>
                    )}
                  />
                </div>
              );
            } else {
              return (
                <div key={index} className="mb-3">
                  <form.Field
                    name="file"
                    children={(field: any) => (
                      <FormGroup>
                        <FormLabel>Foto</FormLabel>
                        <FormControl
                          type="file"
                          value={field.state.filename}
                          onBlur={field.handleBlur}
                          onChange={(e) => {
                            const target = e.target as HTMLInputElement;
                            field.handleChange(target.files[0]);
                          }}
                        />
                      </FormGroup>
                    )}
                  />
                </div>
              );
            }
          })}
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

export default AdminVenueEdit;
