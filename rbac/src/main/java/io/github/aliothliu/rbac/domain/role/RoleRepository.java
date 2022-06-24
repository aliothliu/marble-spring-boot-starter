package io.github.aliothliu.rbac.domain.role;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends PagingAndSortingRepository<Role, String>, JpaSpecificationExecutor<Role> {

    boolean existsByCode(RoleCode code);

    Optional<Role> findByCode(RoleCode code);

    List<Role> findByType(Type type);
}
