package com.iterminal.ndis.repository;

import com.iterminal.ndis.model.FeatureAction;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface IFeatureActionRepository extends JpaRepository<FeatureAction, Long> {

   public Optional<FeatureAction> findByName(String name);
}
