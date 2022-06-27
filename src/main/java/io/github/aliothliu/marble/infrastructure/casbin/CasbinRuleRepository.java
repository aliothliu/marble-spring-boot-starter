package io.github.aliothliu.marble.infrastructure.casbin;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CasbinRuleRepository extends CrudRepository<CasbinRule, Integer>, JpaSpecificationExecutor<CasbinRule> {

    List<CasbinRule> findAll();
}
