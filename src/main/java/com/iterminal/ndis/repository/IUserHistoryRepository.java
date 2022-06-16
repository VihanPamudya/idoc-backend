
package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.UserHistory;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IUserHistoryRepository extends JpaRepository<UserHistory, String> {

    public List<UserHistory> findByEpfNumber(String epfNumber, Sort performedDateTime);

}
