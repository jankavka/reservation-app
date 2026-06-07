import { Container, Table } from "react-bootstrap";
import { useQuery } from "@tanstack/react-query";
import formatDate from "../../components/DateFormat";
import {
  getProfileOptions,
  showCurrentUserOptions,
} from "../../api/@tanstack/react-query.gen";

const AdminProfile = () => {
  const { data: current, isPending: isCurrentPending } = useQuery({
    ...showCurrentUserOptions({}),
  });

  const { data: profile, isPending } = useQuery({
    ...getProfileOptions({ path: { username: current.username } }),
    enabled: !isCurrentPending
  });

  const roleFormat = (items: Array<string>) => {
    return items?.join(", ");
  };

//   if (isError) {
//     console.log(error.message);
//   }


  //   console.log(current);

  if (!isPending) {
    return (
      <Container className="text-center">
        <h1>Admin profil</h1>
        <div>
          <Table>
            <tbody>
              <tr>
                <td>Jméno</td>
                <td>{profile?.fullName}</td>
              </tr>
              <tr>
                <td>Email</td>
                <td>{profile?.email}</td>
              </tr>
              <tr>
                <td>Telefonní číslo</td>
                <td>{profile?.telephoneNumber}</td>
              </tr>
              <tr>
                <td>Role</td>
                <td>{roleFormat(profile?.roles)}</td>
              </tr>
              <tr>
                <td>Vytvořeno</td>
                <td>{formatDate(new Date(profile?.createdAt))}</td>
              </tr>
            </tbody>
          </Table>
        </div>
      </Container>
    );
  }
};

export default AdminProfile;
