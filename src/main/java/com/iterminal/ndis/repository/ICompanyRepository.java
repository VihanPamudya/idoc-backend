package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.Company;
import com.iterminal.ndis.model.User;
import com.iterminal.ndis.model.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ICompanyRepository extends JpaRepository<Company, Long> {

    long countByCompanyEmail(String email);

    long countByContactEmail(String email);

    List<Company> findAllByStatus(String status);

    @Query("SELECT g from Company g where g.companyName=?1")
    Optional<Company> findByName(String companyName);

    int countCompaniesByCompanyNameEquals(String name);

}
