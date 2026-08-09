package com.alphateckplus.potify.support.infrastructure.primary.websocket;

import com.alphateckplus.potify.support.infrastructure.secondary.presence.AdminPresenceRegistry;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketPresenceListener {

    private final AdminPresenceRegistry adminPresenceRegistry;

    @EventListener
    public void handleSessionConnect(SessionConnectEvent event) {
        handleUserPresence(event.getUser());
    }

    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        handleUserPresence(event.getUser());
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        Principal principal = event.getUser();
        if (principal instanceof Authentication auth && isAdmin(auth)) {
            adminPresenceRegistry.unregisterAdmin(principal.getName());
            log.info("WebSocket disconnect: Admin {} est déconnecté.", principal.getName());
        }
    }

    private void handleUserPresence(Principal principal) {
        if (principal instanceof Authentication auth && isAdmin(auth)) {
            adminPresenceRegistry.registerAdminActivity(principal.getName());
            log.info("WebSocket activity: Admin {} est détecté en ligne.", principal.getName());
        }
    }

    private boolean isAdmin(Authentication auth) {
        if (auth == null || auth.getAuthorities() == null) return false;
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.startsWith("ROLE_ADMIN")
                        || role.equals("ROLE_SUPER_ADMIN")
                        || role.equals("ROLE_MODERATEUR")
                        || role.equals("ROLE_SUPPORT_AGENT"));
    }
}
