package gov.va.vro.mockshared.rest;

import gov.va.vro.mockshared.jwt.JwtAppConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@Component
@AllArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
  private JwtAppConfig props;

  private static String getToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      return null;
    }

    return header.split(" ")[1].trim();
  }

  @Override
  protected void doFilterInternal(
      @NotNull HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull FilterChain filterChain)
      throws ServletException, IOException {

    String token = getToken(request);
    if (token == null) {
      filterChain.doFilter(request, response);
      return;
    }
    Claims claims =
        Jwts.parserBuilder()
            .setSigningKey(props.getSecret().getBytes())
            .build()
            .parseClaimsJws(token)
            .getBody();

    UsernamePasswordAuthenticationToken authentication =
        getUsernamePasswordAuthenticationToken(claims);

    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);
  }

  @NotNull
  private static UsernamePasswordAuthenticationToken getUsernamePasswordAuthenticationToken(
      Claims claims) {
    UserDetails details =
        new UserDetails() {
          @Override
          public Collection<? extends GrantedAuthority> getAuthorities() {
            return null;
          }

          @Override
          public String getPassword() {
            return null;
          }

          @Override
          public String getUsername() {
            return claims.getAudience();
          }

          @Override
          public boolean isAccountNonExpired() {
            return true;
          }

          @Override
          public boolean isAccountNonLocked() {
            return true;
          }

          @Override
          public boolean isCredentialsNonExpired() {
            return false;
          }

          @Override
          public boolean isEnabled() {
            return true;
          }
        };

    return new UsernamePasswordAuthenticationToken(details, null, null);
  }
}
