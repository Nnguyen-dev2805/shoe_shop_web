package com.dev.shoeshop.repository;

import com.dev.shoeshop.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>{
    // JPQL
//    @Query("SELECT u FROM User u WHERE u.email = :email")
//    User findByEmail(@Param("email") String email);
    Users findByEmail(String email);

//    @Query("SELECT u FROM Users u WHERE LOWER(u.fullname) LIKE LOWER(CONCAT('%', :name, '%')) AND u.role.roleId = :roleid")
//    List<Users> findByRoleIdAndContainName(String name, int roleid);
//    Users findUsersById(int userId);

//    long countByRole(Role role);
}
