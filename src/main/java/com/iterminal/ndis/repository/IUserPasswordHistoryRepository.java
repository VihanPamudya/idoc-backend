package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.UserPasswordHistory;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IUserPasswordHistoryRepository extends JpaRepository<UserPasswordHistory, Long> {

    public List<UserPasswordHistory> findByEpfNumberOrderByCreatedDateTimeDesc(String epfNumber);

}
