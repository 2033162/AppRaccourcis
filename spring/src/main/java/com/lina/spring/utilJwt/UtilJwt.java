package com.lina.spring.utilJwt;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.Key;

import io.jsonwebtoken.*;

import java.util.Base64;
import java.util.Date;
import java.util.Properties;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;

public class UtilJwt {
  private static final Properties prop = new Properties();

  private static String getPropertie(String sKey) {
    if (prop.isEmpty()) {
      try {
        File initialFile = new File("spring/src/main/resources/application.properties");
        InputStream inputStream = new FileInputStream(initialFile);
        prop.load(inputStream);
        inputStream.close();
      } catch (Exception e) {
        e.printStackTrace(System.out);
      }
    }

    if (prop.isEmpty()) {
      return "";
    } else {
      return prop.getProperty(sKey, "");
    }
  }
  private static String getSecretKey() {
    String phraseSecrete = getPropertie("PhraseSecrete");
    SecretKey secretKey = new SecretKeySpec(phraseSecrete.getBytes(), "AES");
    String sSecretKey = Base64.getUrlEncoder().encodeToString(secretKey.getEncoded());
    return sSecretKey;
  }

  public static String genereJWT(String nomUtilisateur, String prenom, String nomFamille) {

    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    long nowMillis = System.currentTimeMillis();
    Date now = new Date(nowMillis);

    // expiration dans 2 heures
    long expMillis = nowMillis + (2L * 60L * 60L * 1000L);
    Date exp = new Date(expMillis);

    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(getSecretKey());
    Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

    JwtBuilder builder = Jwts.builder()
      .setId(nomUtilisateur)
      .setIssuedAt(now)
      .setExpiration(exp)
      .setSubject(prenom + " " + nomFamille)
      .claim("prenom", prenom)
      .claim("nomFamille", nomFamille)
      .signWith(signatureAlgorithm, signingKey);


    return builder.compact();
  }

  public static Claims decodeJWT(String jwt) {

    Claims claims = Jwts.parser()
      .setSigningKey(DatatypeConverter.parseBase64Binary(getSecretKey()))
      .parseClaimsJws(jwt).getBody();
    return claims;
  }
}
