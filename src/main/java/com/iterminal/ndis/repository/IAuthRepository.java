package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IAuthRepository extends JpaRepository<User, String> {

    User findByEpfNumberAndPassword(String userName, String password);

}
