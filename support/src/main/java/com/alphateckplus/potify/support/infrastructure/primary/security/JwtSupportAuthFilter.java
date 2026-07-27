package com.alphateckplus.potify.support.infrastructure.primary.security;

import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtSupportAuthFilter extends OncePerRequestFilter {

    private final JwtSupportUtils jwtSupportUtils;
    private final UserEntityRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authHeader.substring(7);
        try {
            final String username = jwtSupportUtils.extractUsername(token);
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtSupportUtils.isTokenValid(token)) {
                    Optional<UserEntity> userOpt = userRepository.findWithRolesByEmail(username);
                    List<GrantedAuthority> authorities = new ArrayList<>();
                    userOpt.ifPresent(user -> user.getRoles().forEach(role -> {
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
                        role.getPermissions().forEach(perm -> authorities.add(new SimpleGrantedAuthority(perm.getCode())));
                    }));

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            username, null, authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            log.error("Erreur de validation JWT dans le filtre support: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}
