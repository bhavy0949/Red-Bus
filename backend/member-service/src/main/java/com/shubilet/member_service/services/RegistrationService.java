package com.shubilet.member_service.services;

import com.shubilet.member_service.dataTransferObjects.requests.AdminRegistrationDTO;
import com.shubilet.member_service.dataTransferObjects.requests.CompanyRegistrationDTO;
import com.shubilet.member_service.dataTransferObjects.requests.CustomerRegistrationDTO;

public interface RegistrationService {
    boolean registerCustomer(CustomerRegistrationDTO dto);
    boolean registerCompany(CompanyRegistrationDTO dto);
    boolean registerAdmin(AdminRegistrationDTO dto);
    boolean isUserExistsByEmail(String email);
}
