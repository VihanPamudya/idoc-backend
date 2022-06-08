package com.iterminal.ndis.repository;

import com.iterminal.ndis.dto.response.UserBasicDto;
import com.iterminal.ndis.model.User;
import com.iterminal.ndis.model.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface IUserRepository extends JpaRepository<User, String> {

    @Query(value = "select count(*) from user u inner join user_role ur on u.epf_number = ur.epf_number inner join role_feature_action rfa on rfa.role_id = ur.role_id  inner join feature_action fa on fa.id = rfa.feature_action_id where u.epf_number =?1 and (fa.name =?2 or fa.name ='*')",
            nativeQuery = true)
    int getUserFeatureActionCount(String username, String featureAction);

    public User findByToken(String token);

    long countByEmail(String email);

    List<User> findAllByStatus(String status);

    List<User> findAllByLocked(boolean isLocked);

    @Query(value = "select epf_number, username from user where epf_number =?1", nativeQuery = true)
    public UserBasicDto findUserWithBasic(String id);

    @Query("SELECT u from User u where u.userName=?1")
    Optional<User> findByName(String name);

    int countUsersByStatusEquals(String status);

}
