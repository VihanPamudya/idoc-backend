package com.iterminal.ndis.service.impl;

import com.iterminal.exception.AuthorizationFailedException;
import com.iterminal.ndis.repository.IUserRepository;
import com.iterminal.ndis.util.DataUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


@Slf4j
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final IUserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        com.iterminal.ndis.model.User user = null;
        Optional<com.iterminal.ndis.model.User> foundUser = userRepository.findById(username);
        if (foundUser.isPresent()) {
            user = foundUser.get();
            System.out.println("authenticate---> : true");
        } else {
            System.out.println("authenticate---> : false");
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return new User(user.getEpfNumber(), user.getPassword(), new ArrayList<>());
    }

    public void authorization(String username, String requestURI) throws AuthorizationFailedException {

        try {
            System.out.println("request URI---> : " + requestURI);
            String featureAction = DataUtil.generateFeatureAction(requestURI);
            System.out.println("feature Action---> : " + featureAction);
            int count = userRepository.getUserFeatureActionCount(username, featureAction);
            if (count < 1) {
                System.out.println("authorization---> : false");
                throw new AuthorizationFailedException("You don't have permission to execute current action");
            }
            System.out.println("authorization---> : true");

        } catch (Exception ex) {
            throw new AuthorizationFailedException(ex.getMessage());
        }
    }
}
