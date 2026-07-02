package com.telco.members.tcos.security;

import com.telco.members.tcos.model.Members;
import com.telco.members.tcos.repository.MembersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User.UserBuilder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


@Service
public class MembersDetailService implements UserDetailsService {

    @Autowired
    private MembersRepository membersRepository;

    @Override
    public UserDetails loadUserByUsername(String mid) throws UsernameNotFoundException {

        Members members = (Members) membersRepository.findBymid(mid);
        if (members == null)
            throw new UsernameNotFoundException("Member not found with mid: " + mid);

        return new CustomUserDetails(members);
    }
}