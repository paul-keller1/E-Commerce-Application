package com.app.security;

import com.app.config.AppConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JWTService {

	private String secretkey = "";

	/*@
	  private invariant secretkey != null;
	@*/

	/*@
	  public normal_behavior
	  ensures secretkey != null;
	@*/
	public JWTService() {
		try {
			KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
			SecretKey sk = keyGen.generateKey();
			secretkey = Base64.getEncoder().encodeToString(sk.getEncoded());
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/*@
	  public normal_behavior
	  requires email != null;
	  ensures \result != null;
	@*/
	public String generateToken(String email) {
		Map<String, Object> claims = new HashMap<>();
		return Jwts.builder()
				.claims()
				.add(claims)
				.subject(email)
				.issuedAt(new Date(System.currentTimeMillis()))
				.expiration(new Date(System.currentTimeMillis() + AppConstants.JWT_TOKEN_VALIDITY))
				.and()
				.signWith(getKey())
				.compact();
	}


	/*@
	  private normal_behavior
	  ensures \result != null;
	@*/
	private SecretKey getKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretkey);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	/*@
	  public normal_behavior
	  requires token != null;
	  ensures \result != null;
	@*/
	public String extractUserName(String token) {
		return extractClaim(token, Claims::getSubject);
	}

	/*@
	  private normal_behavior
	  requires token != null;
	  requires claimResolver != null;
	  ensures \result != null;
	@*/
	private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
		final Claims claims = extractAllClaims(token);
		return claimResolver.apply(claims);
	}

	/*@
	  private normal_behavior
	  requires token != null;
	  ensures \result != null;
	@*/
	private Claims extractAllClaims(String token) {
		return Jwts.parser()
				.verifyWith(getKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	/*@
	  public normal_behavior
	  requires token != null;
	  requires userDetails != null;
	@*/
	public boolean validateToken(String token, UserDetails userDetails) {
		final String userName = extractUserName(token);
		return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

	/*@
	  private normal_behavior
	  requires token != null;
	@*/
	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	/*@
	  private normal_behavior
	  requires token != null;
	  ensures \result != null;
	@*/
	private Date extractExpiration(String token) {
		return extractClaim(token, Claims::getExpiration);
	}
}
