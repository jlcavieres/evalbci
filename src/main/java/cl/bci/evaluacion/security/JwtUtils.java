package cl.bci.evaluacion.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class JwtUtils {

	    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
	    private static final long EXPIRATION_TIME_MILLIS = 86400000; // 1 day in milliseconds

	    public String generateToken(String subject) {
	        Date now = new Date();
	        Date expiration = new Date(now.getTime() + EXPIRATION_TIME_MILLIS);

	        return Jwts.builder()
	                .setSubject(subject)
	                .setIssuedAt(now)
	                .setExpiration(expiration)
	                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
	                .compact();
	    }

	    public boolean validateToken(String token) {
	        try {
	            Claims claims = Jwts.parser()
	                    .setSigningKey(SECRET_KEY)
	                    .parseClaimsJws(token)
	                    .getBody();

	            // Check if the token is not expired
	            return !claims.getExpiration().before(new Date());
	        } catch (Exception e) {
	            // Token validation failed
	            return false;
	        }
	    }	    


	    public String extractSubject(String token) {
	        try {
	            Claims claims = Jwts.parser()
	                    .setSigningKey(SECRET_KEY)
	                    .parseClaimsJws(token)
	                    .getBody();

	            return claims.getSubject();
	        } catch (Exception e) {
	            // Token extraction failed
	            return null;
	        }
	    }
    
}