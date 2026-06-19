import {
  Button,
  Collapse,
  Form,
  FormControl,
  FormGroup,
  FormLabel,
} from "react-bootstrap";
import { useState } from "react";
import { useForm } from "@tanstack/react-form";
import Loading from "./Loading";
import { useNavigate } from "react-router";

const Filter = ({ values, mutation}) => {
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const fields: string[] = Object.keys(values);
  const navigate = useNavigate();

  const form = useForm({
    defaultValues: values,
    onSubmit: async({value}) => {
        // mutation.mutate({ body: { venue: value, file: value.file } });
    }
  });

  return (
    <div className="text-start mb-3">
      <Button onClick={() => setIsOpen((prev) => !prev)}>
        {isOpen ? "Zavřít" : "Otevřít"}
      </Button>
      <Collapse in={isOpen}>
        <div>
          <h1>Test text</h1>
          <Form
            onSubmit={(e) => {
              e.preventDefault();
              form.handleSubmit();
            }}
          >
            {fields.map((item: string, index) => (
              <form.Field
                key={index}
                name={item}
                children={(field) => (
                  <FormGroup
                    className={`${index === fields.length - 1 ? "mb-3" : null}`}
                  >
                    <FormLabel>{item}</FormLabel>
                    <FormControl
                      value={field.state.value}
                      onBlur={field.handleBlur}
                      onChange={(e) => field.handleChange(e.target.value)}
                    />
                  </FormGroup>
                )}
              />
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
      </Collapse>
    </div>
  );
};
export default Filter;
