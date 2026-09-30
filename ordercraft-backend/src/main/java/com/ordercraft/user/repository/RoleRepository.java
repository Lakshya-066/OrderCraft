package com.ordercraft.user.repository;

import com.ordercraft.user.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);

    boolean existsByRoleName(String roleName);

    @Query("SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.id = :roleId")
    long countUsersWithRole(@Param("roleId") Long roleId);
}
