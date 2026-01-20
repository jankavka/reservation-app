package cz.reservation.service.serviceinterface;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

public interface JwtService {

    String generateToken(String email);

    String extractUserName(String token);

    Date extractExpiration(String token);

    Boolean validateToken(String token, UserDetails userDetails);

    long getJwtExpiration();


}
