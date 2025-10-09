package com.dev.shoeshop.service;

import com.dev.shoeshop.dto.AddressDTO;
import com.dev.shoeshop.dto.UserDTO;
import com.dev.shoeshop.entity.Users;

import java.util.List;

public interface UserService {
//    long count();
//    void delete(Users user);
//    List<Users> findAll();
//    Page<Users> findAll(Pageable pageable);
//    void saveUser(Users user);
    Users findUserByEmail(String email);

    List<UserDTO> getAllShipper(Long i);

    List<UserDTO> findByFullnameAndRole(String name, long roleId);
    
    // Address related methods
    List<AddressDTO> getUserAddresses(Long userId);
    String generateVerificationCode();
    void sendPasswordResetCode(String email);
    void resetPassword(String email, String newPassword);
    boolean verifyResetCode(String email, String code);
//    List<Users> findByFullNameAndRole(String name, int roleId);
//    Users findUserByUserID(int userId);
//    void updateUser(Users user, String fullName, String address, String phone);
}
