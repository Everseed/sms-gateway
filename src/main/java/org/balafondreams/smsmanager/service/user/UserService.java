package org.balafondreams.smsmanager.service.user;

import lombok.RequiredArgsConstructor;
import org.balafondreams.smsmanager.domain.entities.security.Token;
import org.balafondreams.smsmanager.domain.entities.security.TokenType;
import org.balafondreams.smsmanager.domain.entities.user.ERole;
import org.balafondreams.smsmanager.domain.entities.user.Role;
import org.balafondreams.smsmanager.domain.entities.user.User;
import org.balafondreams.smsmanager.domain.exception.DuplicateResourceException;
import org.balafondreams.smsmanager.domain.exception.InvalidPasswordException;
import org.balafondreams.smsmanager.domain.exception.InvalidRoleException;
import org.balafondreams.smsmanager.domain.exception.ResourceNotFoundException;
import org.balafondreams.smsmanager.domain.mapper.UserMapper;
import org.balafondreams.smsmanager.domain.models.user.UserDTO;
import org.balafondreams.smsmanager.domain.models.user.UserRegistrationDTO;
import org.balafondreams.smsmanager.domain.models.user.UserUpdateDTO;
import org.balafondreams.smsmanager.repository.RoleRepository;
import org.balafondreams.smsmanager.repository.SearchResult;
import org.balafondreams.smsmanager.repository.TokenRepository;
import org.balafondreams.smsmanager.repository.UserRepository;
import org.balafondreams.smsmanager.repository.UserSearchCriteria;
import org.balafondreams.smsmanager.repository.UserSpecification;
import org.balafondreams.smsmanager.security.JwtTokenProvider;
import org.balafondreams.smsmanager.security.UserPrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new UserPrincipal(user);
    }

    public UserDTO getUser(Long userId) {
        User user =  userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
        return userMapper.toDto(user);
    }

    /**
     * Récupère l'utilisateur actuellement connecté
     */
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
    }

    /**
     * Authentification et génération de token
     */
    public String authenticateAndGetToken(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );



        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(authentication);

        // Stocker le token
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Token token = new Token();
        token.setToken(jwt);
        token.setType(TokenType.ACCESS);
        User user = new User(); user.setId(userPrincipal.getId());
        token.setUser(user);
        token.setExpiryDate(jwtTokenProvider.getExpirationDateFromToken(jwt));
        tokenRepository.save(token);
        return jwt;
    }

    /**
     * Inscription d'un nouvel utilisateur
     */
    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        // Vérifier si l'username existe déjà
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + registrationDTO.getUsername());
        }

        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + registrationDTO.getEmail());
        }

        // Créer le nouvel utilisateur
        User user = userMapper.toEntity(registrationDTO);
        user.setPassword(passwordEncoder.encode(registrationDTO.getPassword()));

        // Assigner le rôle USER par défaut
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.setRoles(Set.of(userRole));

        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    /**
     * Mise à jour d'un utilisateur
     */
    public UserDTO updateUser(Long userId, UserUpdateDTO updateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Vérifier si l'utilisateur actuel a le droit de modifier cet utilisateur
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(userId) && !hasRole(currentUser.getId(), ERole.ROLE_ADMIN)) {
            throw new AccessDeniedException("Not authorized to update this user");
        }

        // Vérifier le mot de passe actuel si fourni
        if (updateDTO.getCurrentPassword() != null) {
            if (!passwordEncoder.matches(updateDTO.getCurrentPassword(), user.getPassword())) {
                throw new InvalidPasswordException("Current password is incorrect");
            }
        }

        // Vérifier si le nouveau username est disponible
        if (updateDTO.getUsername() != null &&
                !updateDTO.getUsername().equals(user.getUsername()) &&
                userRepository.existsByUsername(updateDTO.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + updateDTO.getUsername());
        }

        // Vérifier si le nouvel email est disponible
        if (updateDTO.getEmail() != null &&
                !updateDTO.getEmail().equals(user.getEmail()) &&
                userRepository.existsByEmail(updateDTO.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + updateDTO.getEmail());
        }

        // Mise à jour des champs
        userMapper.updateEntity(updateDTO, user);

        // Mise à jour du mot de passe si nécessaire
        if (updateDTO.getNewPassword() != null) {
            user.setPassword(passwordEncoder.encode(updateDTO.getNewPassword()));
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    /**
     * Mise à jour des rôles d'un utilisateur
     */
    public UserDTO updateUserRoles(Long userId, Set<String> roleNames) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Set<Role> roles = roleNames.stream()
                .map(name -> ERole.valueOf("ROLE_" + name.toUpperCase()))
                .map(roleEnum -> roleRepository.findByName(roleEnum)
                        .orElseThrow(() -> new InvalidRoleException("Role not found: " + roleEnum)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        User updatedUser = userRepository.save(user);
        return userMapper.toDto(updatedUser);
    }

    /**
     * Désactivation d'un utilisateur
     */
    public void deactivateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setEnabled(false);
        userRepository.save(user);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 6) {
            throw new InvalidPasswordException("Password must be at least 6 characters long");
        }
        // Ajouter d'autres règles de validation si nécessaire
        if (!password.matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$")) {
            throw new InvalidPasswordException(
                    "Password must contain at least one digit, one lowercase, one uppercase, " +
                            "and one special character"
            );
        }
    }

    /**
     * Réactivation d'un utilisateur
     */
    public void activateUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setEnabled(true);
        userRepository.save(user);
    }

    /**
     * Changement de mot de passe
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Recherche d'utilisateurs
     */
    public Page<UserDTO> searchUsers(UserSearchCriteria criteria, Pageable pageable) {
        validateAdminAccess(); // Seuls les admins peuvent rechercher des utilisateurs
        Specification<User> spec = UserSpecification.withFilters(criteria);
        return userRepository.findAll(spec, pageable)
                .map(userMapper::toDto);
    }

    /**
     * Recherche d'utilisateurs par rôle
     */
    public List<UserDTO> getUsersByRole(ERole role) {
        validateAdminAccess();

        return userRepository.findByRolesName(role)
                .stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Recherche avancée avec rapports
     */
    public SearchResult<UserDTO> searchUsersWithStats(UserSearchCriteria criteria, Pageable pageable) {
        validateAdminAccess();

        Specification<User> spec = UserSpecification.withFilters(criteria);
        Page<User> userPage = userRepository.findAll(spec, pageable);

        // Statistiques sur les résultats
        Map<ERole, Long> roleDistribution = userPage.getContent().stream()
                .flatMap(user -> user.getRoles().stream())
                .collect(Collectors.groupingBy(
                        Role::getName,
                        Collectors.counting()
                ));

        long activeCount = userPage.getContent().stream()
                .filter(User::isEnabled)
                .count();

        return SearchResult.<UserDTO>builder()
                .content(userPage.map(userMapper::toDto))
                .stats(Map.of(
                        "roleDistribution", roleDistribution,
                        "activeUsers", activeCount,
                        "totalUsers", userPage.getTotalElements()
                ))
                .build();
    }
    /**
     * Récupération des statistiques utilisateur
     */
    public Map<String, Object> getUserStatistics(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Map<String, Object> stats = new HashMap<>();
        stats.put("messageCount", user.getMessages().size());
        stats.put("contactCount", user.getContacts().size());
        stats.put("templateCount", user.getTemplates().size());
        stats.put("roles", user.getRoles().stream()
                .map(role -> role.getName().toString())
                .collect(Collectors.toSet()));

        // Ajouter d'autres statistiques pertinentes
        return stats;
    }

    /**
     * Vérification des permissions
     */
    public boolean hasRole(Long userId, ERole role) {
        return userRepository.hasRole(userId, role);
    }

    /**
     * Validation des permissions admin
     */
    public void validateAdminAccess() {
        User currentUser = getCurrentUser();
        if (!hasRole(currentUser.getId(), ERole.ROLE_ADMIN)) {
            throw new AccessDeniedException("Admin access required");
        }
    }


}