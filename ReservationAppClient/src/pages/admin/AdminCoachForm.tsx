import { Alert, Button, Container, Form } from "react-bootstrap";
import { useNavigate, useParams } from "react-router";
import { useForm } from "@tanstack/react-form";
import { useMutation, useQuery } from "@tanstack/react-query";
import {
  createCoachMutation,
  editCoachMutation,
  getAllCoachesOptions,
  getAllUsersOptions,
  getCoachOptions,
} from "../../api/@tanstack/react-query.gen";
import type { CoachDto, UserDto } from "../../api";
import Loading from "../../components/Loading";

type CoachFormValues = {
  userId: string;
  bio: string;
  certification: string;
};

/** Create (`/admin/treneri/novy`) and edit (`/admin/treneri/upravit/:id`) coach. */
const AdminCoachForm = () => {
  const { id } = useParams();
  const coachId = id ? Number(id) : null;
  const isEdit = coachId !== null;

  const coachQuery = useQuery({
    ...getCoachOptions({ path: { id: coachId ?? 0 } }),
    enabled: isEdit,
  });
  const coach = coachQuery.data;

  const usersQuery = useQuery({
    ...getAllUsersOptions({ query: { userFilter: {} } }),
    enabled: !isEdit,
  });
  const coachesQuery = useQuery({
    ...getAllCoachesOptions(),
    enabled: !isEdit,
  });

  // When creating, offer only users who are not coaches yet.
  const existingCoachIds = new Set(
    (coachesQuery.data ?? []).map((c) => c.id)
  );
  const availableUsers: UserDto[] = (usersQuery.data ?? []).filter(
    (u) => !existingCoachIds.has(u.id)
  );

  if (
    (isEdit && coachQuery.isPending) ||
    (!isEdit && (usersQuery.isPending || coachesQuery.isPending))
  ) {
    return <Loading />;
  }

  const loadError = coachQuery.error ?? usersQuery.error ?? coachesQuery.error;
  if (loadError) {
    return (
      <Container>
        <Alert variant="danger">Nastala chyba: {loadError.message}</Alert>
      </Container>
    );
  }

  return (
    <CoachForm
      coachId={coachId}
      coach={coach}
      availableUsers={availableUsers}
    />
  );
};

type CoachFormProps = {
  coachId: number | null;
  coach?: CoachDto;
  availableUsers: UserDto[];
};

const CoachForm = ({ coachId, coach, availableUsers }: CoachFormProps) => {
  const navigate = useNavigate();
  const isEdit = coachId !== null;

  const onSuccess = (name?: string) =>
    navigate("/admin/treneri", {
      state: {
        flash: isEdit ? `Trenér ${name} upraven` : `Trenér ${name} vytvořen`,
      },
    });

  const createCoach = useMutation(createCoachMutation());
  const editCoach = useMutation(editCoachMutation());
  const mutationError = createCoach.error ?? editCoach.error;

  const form = useForm({
    defaultValues: {
      userId: coach?.user.id?.toString() ?? "",
      bio: coach?.bio ?? "",
      certification: coach?.certification ?? "",
    } as CoachFormValues,
    onSubmit: async ({ value }) => {
      const user = isEdit
        ? coach!.user
        : availableUsers.find((u) => u.id === Number(value.userId));
      if (!user) return;

      const body: CoachDto = {
        user,
        bio: value.bio.trim(),
        certification: value.certification.trim(),
      };

      if (isEdit) {
        await editCoach.mutateAsync({ path: { id: coachId! }, body });
      } else {
        await createCoach.mutateAsync({ body });
      }
      onSuccess(user.fullName);
    },
  });

  const required = (label: string) => ({
    onChange: ({ value }: { value: string }) =>
      !value?.trim() ? `${label} je povinné pole` : undefined,
  });

  return (
    <Container style={{ maxWidth: "600px" }}>
      <h1 className="text-center mb-4">
        {isEdit ? "Upravit trenéra" : "Nový trenér"}
      </h1>

      {mutationError && (
        <Alert variant="danger">Uložení se nepovedlo: {mutationError.message}</Alert>
      )}

      <Form
        onSubmit={(e) => {
          e.preventDefault();
          form.handleSubmit();
        }}
      >
        <form.Field
          name="userId"
          validators={required("Uživatel")}
          children={(field) => (
            <Form.Group className="mb-3">
              <Form.Label>Uživatel</Form.Label>
              {isEdit ? (
                <Form.Control
                  disabled
                  value={`${coach?.user.fullName} (${coach?.user.email})`}
                />
              ) : (
                <>
                  <Form.Select
                    value={field.state.value}
                    onBlur={field.handleBlur}
                    onChange={(e) => field.handleChange(e.target.value)}
                    isInvalid={field.state.meta.errors.length > 0}
                  >
                    <option value="">Vyberte uživatele</option>
                    {availableUsers.map((u) => (
                      <option key={u.id} value={u.id}>
                        {u.fullName} ({u.email})
                      </option>
                    ))}
                  </Form.Select>
                  <Form.Control.Feedback type="invalid">
                    {field.state.meta.errors.join(", ")}
                  </Form.Control.Feedback>
                  {availableUsers.length === 0 && (
                    <Form.Text muted>
                      Všichni uživatelé už jsou trenéři. Nejdřív vytvořte nového
                      uživatele.
                    </Form.Text>
                  )}
                </>
              )}
            </Form.Group>
          )}
        />

        <form.Field
          name="certification"
          validators={required("Certifikace")}
          children={(field) => (
            <Form.Group className="mb-3">
              <Form.Label>Certifikace</Form.Label>
              <Form.Control
                value={field.state.value}
                onBlur={field.handleBlur}
                onChange={(e) => field.handleChange(e.target.value)}
                isInvalid={field.state.meta.errors.length > 0}
                placeholder="např. Trenér II. třídy ČTS"
              />
              <Form.Control.Feedback type="invalid">
                {field.state.meta.errors.join(", ")}
              </Form.Control.Feedback>
            </Form.Group>
          )}
        />

        <form.Field
          name="bio"
          validators={required("Bio")}
          children={(field) => (
            <Form.Group className="mb-3">
              <Form.Label>Bio</Form.Label>
              <Form.Control
                as="textarea"
                rows={6}
                value={field.state.value}
                onBlur={field.handleBlur}
                onChange={(e) => field.handleChange(e.target.value)}
                isInvalid={field.state.meta.errors.length > 0}
              />
              <Form.Control.Feedback type="invalid">
                {field.state.meta.errors.join(", ")}
              </Form.Control.Feedback>
            </Form.Group>
          )}
        />

        <form.Subscribe
          selector={(state) => [state.canSubmit, state.isSubmitting]}
          children={([canSubmit, isSubmitting]) => (
            <div className="mt-3">
              <Button disabled={!canSubmit} type="submit" className="me-2">
                {isSubmitting ? "..." : "Uložit"}
              </Button>
              <Button
                type="button"
                variant="secondary"
                onClick={() => navigate("/admin/treneri")}
              >
                Zpět
              </Button>
            </div>
          )}
        />
      </Form>
    </Container>
  );
};

export default AdminCoachForm;
