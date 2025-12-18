package com.vidura.exam.entities;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    // Example of using custom fields for account status checks
    @Override
    public boolean isAccountNonExpired() {
        return true; // Simple implementation
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.getIsProfileBanned();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Simple implementation
    }

    @Override
    public boolean isEnabled() {
        return user.getIsProfileActive() ||!user.getIsProfileBanned();
    }

    public User getUser() {
        return user;
    }
}