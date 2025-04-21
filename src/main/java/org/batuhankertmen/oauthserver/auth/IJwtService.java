package org.batuhankertmen.oauthserver.auth;

import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

public interface IJwtService {
    String generateAccessToken(Map<String, Object> extraClaims,  Map<String, Object> extraHeaderClaims,UserDetails userDetails, String audience)  throws IOException, InvalidKeySpecException, NoSuchAlgorithmException ;
    String generateIdToken(HashMap<String, Object> claims, UserDetails userDetails, String audience) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException;
    String generateIdToken(HashMap<String, Object> claims, UserDetails userDetails, String audience, String nonce) throws IOException, InvalidKeySpecException, NoSuchAlgorithmException;
    String getExpirationDate();
}
