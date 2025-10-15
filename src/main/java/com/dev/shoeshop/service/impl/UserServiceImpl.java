package com.dev.shoeshop.service.impl;

import com.dev.shoeshop.converter.UserDTOConverter;
import com.dev.shoeshop.dto.AddressDTO;
import com.dev.shoeshop.dto.UserDTO;
import com.dev.shoeshop.entity.Address;
import com.dev.shoeshop.entity.PasswordResetToken;
import com.dev.shoeshop.entity.Users;
import com.dev.shoeshop.entity.Role;
import com.dev.shoeshop.repository.AddressRepository;
import com.dev.shoeshop.repository.PasswordResetTokenRepository;
import com.dev.shoeshop.repository.RoleRepository;
import com.dev.shoeshop.repository.UserRepository;
import com.dev.shoeshop.service.EmailService;
import com.dev.shoeshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    // Constructor injection (final): rõ ràng, testable, clean code → được khuyến nghị.
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    
    @Autowired
    private UserRepository userRepository2;
    @Autowired
    private UserDTOConverter userDTOConverter;
    @Autowired
    private AddressRepository addressRepository;

    @Override
    public Users findUserByEmail(String email) {
        System.out.println(email);
        return userRepository.findByEmail(email);
    }

    @Override
    public List<UserDTO> getAllShipper(Long i) {
        List<Users> listUser = userRepository2.findByRoleRoleId(i);
        List<UserDTO> listUserDTO = new ArrayList<>();

        return listUser.stream()
                .map(userDTOConverter::toUserDTO)
                .toList(); // Java 16+
    }

    @Override
    public List<UserDTO> findByFullnameAndRole(String name, long roleId) {
        List<Users> listUser = userRepository2.findByRoleRoleIdAndFullnameContaining(roleId,name);
        return listUser.stream()
                .map(userDTOConverter::toUserDTO)
                .toList(); // Java 16+
    }
    
    @Override
    public List<AddressDTO> getUserAddresses(Long userId) {
        List<Address> addresses = addressRepository.findByUserId(userId);
        
        return addresses.stream()
            .map(this::convertToAddressDTO)
            .collect(Collectors.toList());
    }
    
    private AddressDTO convertToAddressDTO(Address address) {
        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setAddressLine(address.getAddress_line());
        dto.setCity(address.getCity());
//        dto.setPostalCode(address.getPostal_code());
        dto.setCountry(address.getCountry());
        dto.setAddress(dto.getFullAddress()); // Use helper method for display
        
        // Check if this is default address (would need UserAddress relationship)
        dto.setIsDefault(false); // This would need to be determined from UserAddress table
        
        return dto;
    }
    
    // Password reset methods implementation
    @Override
    public String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000); // Generate 6-digit code
        return String.valueOf(code);
    }

    @Override
    @Transactional
    public void sendPasswordResetCode(String email) {
        // Check if user exists
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Email không tồn tại trong hệ thống.");
        }

        // Generate verification code
        String verificationCode = generateVerificationCode();

        // Mark all previous tokens as used
        passwordResetTokenRepository.markAllTokensAsUsedForEmail(email);

        // Create new token
        PasswordResetToken token = PasswordResetToken.builder()
                .email(email)
                .verificationCode(verificationCode)
                .isUsed(false)
                .build();

        passwordResetTokenRepository.save(token);

        // Send email
        emailService.sendPasswordResetEmail(email, verificationCode);
    }

    @Override
    public boolean verifyResetCode(String email, String code) {
        Optional<PasswordResetToken> tokenOpt = passwordResetTokenRepository
                .findByEmailAndVerificationCodeAndIsUsedFalse(email, code);

        if (tokenOpt.isEmpty()) {
            return false;
        }

        PasswordResetToken token = tokenOpt.get();
        return !token.isExpired();
    }

    @Override
    @Transactional
    public void resetPassword(String email, String newPassword) {
        // Find user
        Users user = userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Email không tồn tại trong hệ thống.");
        }

        // Encode and update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Mark all tokens as used
        passwordResetTokenRepository.markAllTokensAsUsedForEmail(email);
    }
    
    @Override
    @Transactional
    public void sendRegistrationVerificationCode(String email, String verificationCode) {
        // Mark all previous tokens as used
        passwordResetTokenRepository.markAllTokensAsUsedForEmail(email);

        // Create new token
        PasswordResetToken token = PasswordResetToken.builder()
                .email(email)
                .verificationCode(verificationCode)
                .isUsed(false)
                .build();

        passwordResetTokenRepository.save(token);

        // Send email
        emailService.sendRegistrationVerificationEmail(email, verificationCode);
    }
    
    @Override
    @Transactional
    public void registerNewUser(String email, String fullname, String password) {
        // Check if user already exists
        Users existingUser = userRepository.findByEmail(email);
        if (existingUser != null) {
            throw new RuntimeException("Email đã được đăng ký trong hệ thống.");
        }

        // Get default role (user role with id = 3 or find by name "user")
        Role userRole = roleRepository.findById(3L)
                .orElseGet(() -> roleRepository.findByRoleName("user")
                        .orElseThrow(() -> new RuntimeException("Default role not found")));

        // Create new user
        Users newUser = Users.builder()
                .email(email)
                .fullname(fullname)
                .password(passwordEncoder.encode(password))
                .role(userRole)
                .provider("LOCAL")
                .isActive(true)
                .build();

        userRepository.save(newUser);

        // Mark all verification tokens as used
        passwordResetTokenRepository.markAllTokensAsUsedForEmail(email);
    }
}
