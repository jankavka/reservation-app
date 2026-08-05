import {
  Container,
  Table,
  Button,
  FormGroup,
  FormLabel,
  Collapse,
} from "react-bootstrap";
import { useQuery } from "@tanstack/react-query";
import { showAllCourtsOptions } from "../../api/@tanstack/react-query.gen";
import Loading from "../../components/Loading";
import { useNavigate } from "react-router";
import { Link } from "react-router";
import { Form } from "react-bootstrap";
import { useState } from "react";
import { useForm } from "@tanstack/react-form";
import { FormInput, FormInputCheck } from "../../components/FormFields";

const AdminCourts = () => {
  const navigate = useNavigate();
  const [filter, setFilter] = useState<any>(null);
  const [isFilterOpen, setIsFilterOpen] = useState<boolean>(false);
  const [reset, setReset] = useState<boolean>(false);
  const { data, isPending, isError, error } = useQuery({
    ...showAllCourtsOptions({ query: { courtFilter: filter } }),
  });

  const showCourtDetail = (id: number) => {
    navigate("/admin/kurty/" + id);
  };

  const resetFilter = () => {
    //setFilter(null);
    setReset(true);
    form.reset();
  };

  const form = useForm({
    defaultValues: {
      name: " ",
      surface: " ",
      indoor: filter?.indoor || false,
      lighting: false,
    },
    onSubmit: async ({ value }) => {
      if (reset) {
        setFilter(null);
        setReset(false);
      } else {
        setFilter(value);
      }
    },
  });

  if (isPending) {
    return <Loading />;
  }

  if (isError) {
    return (
      <Container>
        <p>Nastala chyba: {error.message}</p>
      </Container>
    );
  }

  return (
    <Container className="text-center">
      <h1>Kurty</h1>

      <div className="text-start">
        <Button onClick={() => setIsFilterOpen((prev) => !prev)}>
          {isFilterOpen ? "Skrýt filter" : "Zobrazit filter"}
        </Button>
      </div>

      <div className="text-end me-5 mb-3">
        <Button variant="success" onClick={() => navigate("/admin/kurty/novy")}>
          Nový kurt
        </Button>
      </div>

      <Collapse in={isFilterOpen} className="mb-5">
        <div
          className="text-start"
          id="filter"
          aria-expanded={isFilterOpen}
          aria-controls="filter"
          style={{ width: "50dvw" }}
        >
          <Form
            style={{ height: "20dvh", width: "70dvw" }}
            onSubmit={(e) => {
              e.preventDefault();
              form.handleSubmit();
            }}
          >
            <div className="d-flex justify-content-between mb-5">
              <form.Field
                name={"name"}
                children={(field) => (
                  <FormInput formLabel={"Jméno"} field={field} />
                )}
              />
              <form.Field
                name={"surface"}
                children={(field) => (
                  <FormGroup>
                    <FormLabel>Povrch</FormLabel>
                    <Form.Select
                      onChange={(e) => field.handleChange(e.target.value)}
                      value={field.state.value || undefined}
                      onBlur={field.handleBlur}
                      multiple={false}
                    >
                      <option value={" "}>Vyberte možnost</option>
                      <option value={"CLAY"}>Antuka</option>
                      <option value={"HARD"}>Tvrdý</option>
                      <option value={"WIMBLEDON"}>Koberec</option>
                    </Form.Select>
                  </FormGroup>
                )}
              />
              <form.Field
                name={"indoor"}
                children={(field) => (
                  <FormInputCheck formLabel={"uvnitř"} field={field} />
                )}
              />
              <form.Field
                name={"lighting"}
                children={(field) => (
                  <FormInputCheck formLabel={"osvětlení"} field={field} />
                )}
              />
            </div>
            <div>
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
                      <Button type="button" onClick={() => resetFilter()}>
                        Reset
                      </Button>
                    </div>
                    <div>{isSubmitting ? <Loading /> : null}</div>
                  </div>
                )}
              />
            </div>
          </Form>
        </div>
      </Collapse>
      <Table hover={true}>
        <thead>
          <tr>
            <th>Id</th>
            <th>Jméno</th>
            <th>Areál</th>
            <th>Povrch</th>
            <th>Uvnitř</th>
            <th>Osvětlení</th>
            <th>Akce</th>
          </tr>
        </thead>
        <tbody>
          {data.map((item, _) => (
            <tr key={item.id}>
              <td>{item.id}</td>
              <td>{item.name}</td>
              <td>
                <Link to={"/admin/arealy/" + item.venue.id}>
                  {item.venue.name}
                </Link>
              </td>
              <td>{item.surface}</td>
              <td>{item.indoor === true ? "ANO" : "NE"}</td>
              <td>{item.lighting === true ? "ANO" : "NE"}</td>
              <td
                className=" d-flex flex-column justify-content-between"
                style={{ minHeight: "18dvh" }}
              >
                <Button variant={"warning"}>Upravit</Button>
                <Button variant={"danger"}>Vymazat</Button>
                <Button
                  onClick={() => showCourtDetail(item.id)}
                  variant={"secondary"}
                >
                  Detail
                </Button>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </Container>
  );
};

export default AdminCourts;
