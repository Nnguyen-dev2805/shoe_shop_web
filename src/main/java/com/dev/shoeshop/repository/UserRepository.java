package com.dev.shoeshop.repository;

import com.dev.shoeshop.dto.UserDTO;
import com.dev.shoeshop.entity.Role;
import com.dev.shoeshop.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>{
    // JPQL
//    @Query("SELECT u FROM User u WHERE u.email = :email")
//    User findByEmail(@Param("email") String email);
    Users findByEmail(String email);

    List<Users> findByRoleRoleId(Long roleId);


    List<Users> findByRoleRoleIdAndFullnameContaining(long roleId,String name);

    /**
     * Lấy tất cả users có role cụ thể
     */
    @Query("SELECT u FROM Users u WHERE u.role.roleName = :roleName")
    List<Users> findByRoleName(@Param("roleName") String roleName);
    
    /**
     * Lấy tất cả shippers (users có role = shipper)
     */
    @Query("SELECT u FROM Users u WHERE u.role.roleName = 'shipper'")
    List<Users> findAllShippers();

//    @Query("SELECT u FROM Users u WHERE LOWER(u.fullname) LIKE LOWER(CONCAT('%', :name, '%')) AND u.role.roleId = :roleid")
//    List<Users> findByRoleIdAndContainName(String name, int roleid);
//    Users findUsersById(int userId);

//    long countByRole(Role role);
}
