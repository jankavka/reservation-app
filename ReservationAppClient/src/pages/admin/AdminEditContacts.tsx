import { Container, Form, Button } from "react-bootstrap";
import { useForm } from "@tanstack/react-form";
import type { CompanyInfoDto } from "../../api";
import { useMutation, useQuery } from "@tanstack/react-query";
import {
  getInfoOptions,
  updateInfoMutation,
} from "../../api/@tanstack/react-query.gen";
import { FormInput } from "../../components/FormFields";
import Loading from "../../components/Loading";
import { useLocation, useNavigate } from "react-router";
import FlashMessage from "../../components/FlashMessages";

const AdminEditContacts = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const errorMessage = location.state?.errorMessage;
  const editSuccess = location.state?.editSuccess || null;
  const { data } = useQuery({
    ...getInfoOptions({}),
  });

  const defaultValues: CompanyInfoDto = {
    companyName: data?.companyName || " ",
    address: data?.address || " ",
    businessId: data?.businessId || " ",
    taxNumber: data?.taxNumber || " ",
    email: data?.email || " ",
    telNumber: data?.telNumber || " ",
    bankAccount: data?.bankAccount || " ",
    bankAccountInternationalFormat: data?.bankAccountInternationalFormat || " ",
    description: data?.description || " ",
  };

  const { mutate } = useMutation({
    ...updateInfoMutation({}),
    onSuccess: () =>
      navigate("/admin/info", { state: { editSuccess: true } }),
    onError: (error) => {
      console.error(error.message);
      navigate("/admin/kontakty/upravit", {
        state: { editSuccess: false, errorMessage: error.message },
      });
    },
  });

  const form = useForm({
    defaultValues: defaultValues,
    onSubmit: async ({ value }) => {
      mutate({ body: value });
    },
  });

  return (
    <Container className="mb-5">
      <div className="text-center">
        <h1>Upravit Kontakty</h1>
        <FlashMessage
          success={editSuccess}
          state={editSuccess}
          text={errorMessage}
        />
        <div className="text-start" style={{ maxWidth: "70dvw" }}>
          <Form
            onSubmit={(e) => {
              e.preventDefault();
              form.handleSubmit();
            }}
          >
            {Array.from(Object.keys(defaultValues)).map((item, index) => (
              <div key={index} className="mb-3">
                <form.Field
                  name={item}
                  children={(field: any) => {
                    if (item !== "description") {
                      return <FormInput formLabel={item} field={field} />;
                    }
                  }}
                />
              </div>
            ))}
            <form.Subscribe
              selector={(state) => [state.canSubmit, state.isSubmitting]}
              children={([canSubmit, isSubmitting]) => (
                <div>
                  <div>
                    <Button
                      disabled={!canSubmit}
                      type="submit"
                      className="me-2"
                    >
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
        </div>
      </div>
    </Container>
  );
};

export default AdminEditContacts;
