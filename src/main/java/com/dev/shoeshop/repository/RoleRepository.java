package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    List<Role> findAll();
    Role findByRoleName(String roleName);
}
