package cz.reservation.service.serviceinterface;

import cz.reservation.dto.*;

public interface AuthService {

    LoginResponseDto authenticate(AuthRequestDTO authRequestDTO);

    UserDto createUser(RegistrationRequestDto registrationRequestDto);

    UserDto createUserByAdmin(CreateUserByAdminDto createUserByAdminDto);

    LoginResponseDto refresh(RefreshTokenRequestDto refreshTokenRequestDto);
}
