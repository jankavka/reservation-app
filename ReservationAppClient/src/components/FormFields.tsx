import { FormGroup, FormLabel, FormControl, Form } from "react-bootstrap";

export const FormInput = ({ formLabel, field }) => {
  return (
    <FormGroup>
      <FormLabel>{formLabel}</FormLabel>
      <FormControl
        value={field.state.value}
        onBlur={field.handleBlur}
        onChange={(e) => field.handleChange(e.target.value)}
      />
    </FormGroup>
  );
};

export const FormInputCheck = ({ formLabel, field }) => {
  return (
    <FormGroup>
      <Form.Check
        type="checkbox"
        label={formLabel}
        value={field.state.value}
        onBlur={field.handleBlur}
        onChange={() => field.handleChange((prev: any) => !prev)}
      />
    </FormGroup>
  );
};

export const FormInputSelect = ({ formLabel, field, options }) => {
  return (
    <FormGroup>
      <FormLabel>{formLabel}</FormLabel>
      <Form.Select
        onChange={(e) => field.handleChange(e.target.value)}
        value={field.state?.value || undefined}
        onBlur={field.handleBlur}
      >
        <option value={""}>Vyberte možnost</option>
        {options.map((option: string, index: number) => (
          <option key={index} value={option}>
            {option}
          </option>
        ))}
      </Form.Select>
    </FormGroup>
  );
};
