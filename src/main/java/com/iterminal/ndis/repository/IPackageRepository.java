package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.Package;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IPackageRepository extends JpaRepository<Package, Long> {
}
