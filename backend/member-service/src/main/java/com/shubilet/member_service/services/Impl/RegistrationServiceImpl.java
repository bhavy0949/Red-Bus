package com.shubilet.member_service.services.Impl;

import com.shubilet.member_service.common.enums.Gender;
import com.shubilet.member_service.common.enums.Role;
import com.shubilet.member_service.dataTransferObjects.requests.AdminRegistrationDTO;
import com.shubilet.member_service.dataTransferObjects.requests.CompanyRegistrationDTO;
import com.shubilet.member_service.dataTransferObjects.requests.CustomerRegistrationDTO;
import com.shubilet.member_service.models.AdminInfo;
import com.shubilet.member_service.models.CompanyInfo;
import com.shubilet.member_service.models.CustomerInfo;
import com.shubilet.member_service.models.User;
import com.shubilet.member_service.repositories.AdminInfoRepository;
import com.shubilet.member_service.repositories.CompanyInfoRepository;
import com.shubilet.member_service.repositories.CustomerInfoRepository;
import com.shubilet.member_service.repositories.UserRepository;
import com.shubilet.member_service.services.RegistrationService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationServiceImpl implements RegistrationService {

    private final UserRepository userRepository;
    private final CustomerInfoRepository customerInfoRepository;
    private final AdminInfoRepository adminInfoRepository;
    private final CompanyInfoRepository companyInfoRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationServiceImpl(
            UserRepository userRepository,
            CustomerInfoRepository customerInfoRepository,
            AdminInfoRepository adminInfoRepository,
            CompanyInfoRepository companyInfoRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.customerInfoRepository = customerInfoRepository;
        this.adminInfoRepository = adminInfoRepository;
        this.companyInfoRepository = companyInfoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public boolean registerCustomer(CustomerRegistrationDTO dto) {
        User user = new User(
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                Role.CUSTOMER
        );
        userRepository.save(user);

        CustomerInfo info = new CustomerInfo(
                user,
                dto.getName(),
                dto.getSurname(),
                Gender.fromValue(dto.getGender())
        );
        customerInfoRepository.save(info);
        return true;
    }

    @Override
    @Transactional
    public boolean registerCompany(CompanyRegistrationDTO dto) {
        User user = new User(
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                Role.COMPANY
        );
        userRepository.save(user);

        CompanyInfo info = new CompanyInfo(user, dto.getTitle());
        companyInfoRepository.save(info);
        return true;
    }

    @Override
    @Transactional
    public boolean registerAdmin(AdminRegistrationDTO dto) {
        User user = new User(
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword()),
                Role.ADMIN
        );
        userRepository.save(user);

        AdminInfo info = new AdminInfo(user, dto.getName(), dto.getSurname());
        adminInfoRepository.save(info);
        return true;
    }

    @Override
    public boolean isUserExistsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
