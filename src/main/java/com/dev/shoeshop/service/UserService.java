package com.dev.shoeshop.service;

import com.dev.shoeshop.entity.Users;

public interface UserService {
//    long count();
//    void delete(Users user);
//    List<Users> findAll();
//    Page<Users> findAll(Pageable pageable);
//    void saveUser(Users user);
    Users findUserByEmail(String email);
//    List<Users> findByFullNameAndRole(String name, int roleId);
//    Users findUserByUserID(int userId);
//    void updateUser(Users user, String fullName, String address, String phone);
}
