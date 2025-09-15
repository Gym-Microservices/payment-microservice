package com.gym.payment_microservice.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class GatewayAuthenticationToken extends AbstractAuthenticationToken {
    
    private final String userId;
    private final String username;
    private final String email;
    
    public GatewayAuthenticationToken(String userId, String username, String email, List<String> roles) {
        super(convertRolesToAuthorities(roles));
        this.userId = userId;
        this.username = username;
        this.email = email;
        setAuthenticated(true);
    }
    
    private static Collection<? extends GrantedAuthority> convertRolesToAuthorities(List<String> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.startsWith("ROLE_") ? role : "ROLE_" + role))
                .collect(Collectors.toList());
    }
    
    @Override
    public Object getCredentials() {
        return null; // No credentials needed, authentication was done by Gateway
    }
    
    @Override
    public Object getPrincipal() {
        return username;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getEmail() {
        return email;
    }
}
