package com.alphateckplus.potify.user.infrastructure.primary.security;

// Importation du port de repository pour accéder aux données utilisateur (Couplage faible via interface)
import com.alphateckplus.potify.user.application_service.secondary.user.UserRepositoryPort;
// Importation du modèle de domaine User
import com.alphateckplus.potify.user.domain.model.User;
// Utilisation de Collectors pour transformer les listes
import java.util.stream.Collectors;
// Lombok pour l'injection de dépendances par constructeur
import lombok.RequiredArgsConstructor;
// Spring Security pour la gestion des autorités (Rôles)
import org.springframework.security.core.authority.SimpleGrantedAuthority;
// Interfaces standard de Spring Security
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
// Annotation Service pour la détection par Spring
import org.springframework.stereotype.Service;

/**
 * Implémentation de UserDetailsService pour charger les données utilisateur.
 * Cette classe fait le pont entre notre domaine (User) et le système de sécurité de Spring.
 * Elle respecte le principe de Single Responsibility en se chargeant uniquement de la récupération des détails utilisateur.
 */
@Service // Marque cette classe comme un service Spring
@RequiredArgsConstructor // Génère un constructeur pour les champs 'final' (Injection de dépendances)
public class UserDetailsServiceImpl implements UserDetailsService {

    // Port vers le repository (Abstraction pour respecter le couplage faible)
    private final UserRepositoryPort userRepositoryPort;

    /**
     * Charge un utilisateur par son email (utilisé comme username).
     * @param email L'identifiant de l'utilisateur.
     * @return UserDetails utilisable par Spring Security.
     * @throws UsernameNotFoundException Si l'utilisateur n'existe pas.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Recherche de l'utilisateur via le port
        User user = userRepositoryPort.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email: " + email));

        // Construction de l'objet UserDetails de Spring Security à partir de notre modèle de domaine
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail()) // L'email sert d'identifiant
                .password(user.getPassword()) // Mot de passe encodé
                // Transformation des rôles et de leurs permissions du domaine en GrantedAuthority Spring Security
                .authorities(userRepositoryPort.findRolesByUserId(user.getId()).stream()
                        .flatMap(role -> {
                            java.util.List<org.springframework.security.core.GrantedAuthority> auths = new java.util.ArrayList<>();
                            auths.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
                            role.getPermissions().forEach(perm -> auths.add(new SimpleGrantedAuthority(perm.getCode())));
                            return auths.stream();
                        })
                        .collect(Collectors.toList()))
                .disabled(!user.isEnabled()) // État d'activation du compte
                .accountLocked(!user.isAccountNonLocked()) // État de verrouillage
                .build();
    }
}
