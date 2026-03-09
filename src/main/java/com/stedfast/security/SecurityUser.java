package com.stedfast.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class SecurityUser extends User {
    private final String userId;

    public SecurityUser(String username, String password, Collection<? extends GrantedAuthority> authorities,
            String userId) {
        super(username, password, authorities);
        this.userId = userId;
    }
}
