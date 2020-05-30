package com.siyang.springsecurityspringbootdemo.security.token;

import com.alibaba.druid.util.StringUtils;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author /
 */
@Slf4j
@Component
public class TokenProvider implements InitializingBean {

   private final SecurityProperties properties;
   private static final String AUTHORITIES_KEY = "auth";
   private Key key;

   public TokenProvider(SecurityProperties properties) {
      this.properties = properties;
   }

   //实例化类之前调用方法
   @Override
   public void afterPropertiesSet() {
      byte[] keyBytes = Decoders.BASE64.decode(properties.getBase64Secret());
      this.key = Keys.hmacShaKeyFor(keyBytes);
   }

   /**
    * 创建token
    * @param authentication
    * @return
    */
   public String createToken(Authentication authentication) {
      String authorities = authentication.getAuthorities().stream()
         .map(GrantedAuthority::getAuthority)
         .collect(Collectors.joining(","));

      long now = (new Date()).getTime();
      Date validity = new Date(now + properties.getTokenValidityInSeconds());

      return Jwts.builder()
         .setSubject(authentication.getName())
         .claim(AUTHORITIES_KEY, authorities)
         .signWith(key, SignatureAlgorithm.HS512)
         .setExpiration(validity)//过期时间
         .compact();
   }

   /**
    * 获得认证信息
    * @param token
    * @return
    */
   public Authentication getAuthentication(String token) {
      Claims claims = Jwts.parser()
         .setSigningKey(key)
         .parseClaimsJws(token)
         .getBody();
       Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();

       // 如果权限信息为空，则默认为guest 游客权限
      if(!StringUtils.isEmpty(claims.get(AUTHORITIES_KEY).toString())){

          authorities =
                  Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                          .map(SimpleGrantedAuthority::new)
                          .collect(Collectors.toList());
      }else {
          SimpleGrantedAuthority guest = new SimpleGrantedAuthority("guest");
          authorities.add(guest);

      }


      User principal = new User(claims.getSubject(), "", authorities);
      // springsecurity中是这个格式
      return new UsernamePasswordAuthenticationToken(principal, token, authorities);
   }

   /**
    * 验证token
    * @param authToken
    * @return
    */
   public boolean validateToken(String authToken) {
      try {
         Jwts.parser().setSigningKey(key).parseClaimsJws(authToken);
         return true;
      } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
         log.info("Invalid JWT signature.");
         e.printStackTrace();
      } catch (ExpiredJwtException e) {
         log.info("Expired JWT token.");
         e.printStackTrace();
      } catch (UnsupportedJwtException e) {
         log.info("Unsupported JWT token.");
         e.printStackTrace();
      } catch (IllegalArgumentException e) {
         log.info("JWT token compact of handler are invalid.");
         e.printStackTrace();
      }
      return false;
   }

   public String getToken(HttpServletRequest request){
      final String requestHeader = request.getHeader(properties.getHeader());
      if (requestHeader != null && requestHeader.startsWith(properties.getTokenStartWith())) {
         return requestHeader.substring(7);
      }
      return null;
   }
}
