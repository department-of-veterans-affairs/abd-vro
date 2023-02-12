package gov.va.vro.mockshared;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@AllArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {
  private JwtAppConfig props;

  private static String getToken(HttpServletRequest request) {
    String header = request.getHeader("Authorization");
    if (header == null || header.isEmpty() || !header.startsWith("Bearer ")) {
      return null;
    }

    String token = header.split(" ")[1].trim();
    return token;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    String token = getToken(request);
    if (token == null) {
      filterChain.doFilter(request, response);
      return;
    }

    byte[] secretBytes = props.getSecret().getBytes(StandardCharsets.UTF_8);
    Claims claims = Jwts.parser().setSigningKey(secretBytes).parseClaimsJws(token).getBody();

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

    UsernamePasswordAuthenticationToken authentication =
        new UsernamePasswordAuthenticationToken(details, null, null);

    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

    SecurityContextHolder.getContext().setAuthentication(authentication);

    filterChain.doFilter(request, response);
  }
}
