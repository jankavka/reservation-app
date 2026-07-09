import { Container, Form, Button } from "react-bootstrap";
import { useQuery, useMutation } from "@tanstack/react-query";
import {
  getInfoOptions,
  updateInfoMutation,
} from "../../api/@tanstack/react-query.gen";
import { useRef } from "react";
import Loading from "../../components/Loading";
import { useForm } from "@tanstack/react-form";
import type { CompanyInfoDto } from "../../api";
import { useNavigate } from "react-router";
import MyEditor from "../../components/MyEditor";

const AdminAboutUsEdit = () => {
  const navigate = useNavigate();
  const editorRef = useRef(null);
  const { data, isPending, isError } = useQuery({
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
    onSuccess: () => {
      navigate("/admin/info");
    },
  });

  const form = useForm({
    defaultValues: defaultValues,
    onSubmit: async ({ value }) => {
      mutate({ body: value });
    },
  });

  if (isPending) {
    return <Loading />;
  }

  if (isError) {
    <Container>
      <div className="text-center">
        <p>Problém s načtením formuláře</p>
      </div>
    </Container>;
  }

  return (
    <div>
      <Container>
        <div className="text-center">
          <h1>Upravit popis</h1>
          <Form
            onSubmit={(e) => {
              e.preventDefault();
              form.handleSubmit();
            }}
          >
            <form.Field
              name="description"
              children={(field: any) => (
                <MyEditor
                  editorRef={editorRef}
                  field={field}
                />
              )}
            />
            <form.Subscribe
              selector={(state) => [state.canSubmit, state.isSubmitting]}
              children={([canSubmit, isSubmitting]) => (
                <div>
                  <div className="mt-3">
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
      </Container>
    </div>
  );
};

export default AdminAboutUsEdit;
