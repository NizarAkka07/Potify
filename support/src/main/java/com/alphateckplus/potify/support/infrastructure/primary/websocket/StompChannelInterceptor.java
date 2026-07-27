package com.alphateckplus.potify.support.infrastructure.primary.websocket;

import com.alphateckplus.potify.data_jpa.entity.user.UserEntity;
import com.alphateckplus.potify.data_jpa.repository.user.UserEntityRepository;
import com.alphateckplus.potify.support.infrastructure.primary.security.JwtSupportUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class StompChannelInterceptor implements ChannelInterceptor {

    private final JwtSupportUtils jwtSupportUtils;
    private final UserEntityRepository userRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    String username = jwtSupportUtils.extractUsername(token);
                    if (username != null && jwtSupportUtils.isTokenValid(token)) {
                        Optional<UserEntity> userOpt = userRepository.findByEmail(username);
                        List<GrantedAuthority> authorities = new ArrayList<>();
                        userOpt.ifPresent(user -> user.getRoles().forEach(role -> {
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
                            role.getPermissions().forEach(perm -> authorities.add(new SimpleGrantedAuthority(perm.getCode())));
                        }));

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(username, null, authorities);
                        accessor.setUser(authentication);
                        log.info("WebSocket STOMP authentifié avec succès pour l'utilisateur: {}", username);
                    }
                } catch (Exception e) {
                    log.error("Échec d'authentification STOMP WebSocket: {}", e.getMessage());
                }
            }
        }
        return message;
    }
}
