package com.telco.members.tcos.security;

import com.telco.members.tcos.model.Members;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {

    private final Members member;

    public CustomUserDetails(Members member) {
        this.member = member;
    }

    public String getFirstName() {
        return member.getFirstName();
    }

    public String getLastName() {
        return member.getLastName();
    }

    public String getMid() {
        return member.getMid();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // or List.of(new SimpleGrantedAuthority("ROLE_MEMBERS"))
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public String getUsername() {
        return member.getMid();  // used for authentication
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
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public Members getMember() {
        return member;
    }
}