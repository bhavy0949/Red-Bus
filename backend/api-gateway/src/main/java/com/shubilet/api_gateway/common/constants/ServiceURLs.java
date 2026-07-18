package com.shubilet.api_gateway.common.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * Holds the fully-qualified URLs of every downstream service endpoint.
 *
 * The host:port portion is not hardcoded — it comes from configuration
 * (application.properties, which reads the redbus-config ConfigMap in
 * Kubernetes), so the same image runs against any environment without a
 * rebuild. Only the request paths, which are part of the service contract
 * and do not vary per environment, are defined here.
 *
 * The fields stay static so existing call sites (ServiceURLs.X) are unchanged;
 * they are populated once at startup by {@link #init()}.
 */
@Component
public class ServiceURLs {

    @Value("${services.expedition.base-url}")
    private String expeditionBaseUrl;

    @Value("${services.member.base-url}")
    private String memberBaseUrl;

    @Value("${services.security.base-url}")
    private String securityBaseUrl;

    // Expedition Service URLs
    public static String EXPEDITION_SERVICE_SEARCH_EXPEDITION_URL;
    public static String EXPEDITION_SERVICE_SEARCH_SEAT_URL;
    public static String EXPEDITION_SERVICE_GET_CUSTOMER_TICKETS_SEAT_URL;
    public static String EXPEDITION_SERVICE_BUY_TICKET;
    public static String EXPEDITION_SERVICE_CREATE_EXPEDITION_URL;
    public static String EXPEDITION_SERVICE_GET_COMPANY_EXPEDITIONS_ALL_URL;
    public static String EXPEDITION_SERVICE_GET_COMPANY_EXPEDITIONS_FUTURE_URL;
    public static String EXPEDITION_SERVICE_GET_COMPANY_EXPEDITION_DETAIL_URL;
    public static String EXPEDITION_SERVICE_GET_CUSTOMER_CARDS_URL;
    public static String EXPEDITION_SERVICE_BLOCK_SEAT_URL;
    public static String EXPEDITION_SERVICE_UNBLOCK_SEAT_URL;


    // Member Service URLs
    public static String MEMBER_SERVICE_CREDENTIALS_CHECK_URL;

    public static String MEMBER_SERVICE_CUSTOMER_REGISTRATION_URL;
    public static String MEMBER_SERVICE_COMPANY_REGISTRATION_URL;
    public static String MEMBER_SERVICE_ADMIN_REGISTRATION_URL;

    public static String MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_NAME_URL;
    public static String MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_SURNAME_URL;
    public static String MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_GENDER_URL;
    public static String MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_EMAIL_URL;
    public static String MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_PASSWORD_URL;
    public static String MEMBER_SERVICE_FAVORITE_COMPANY_ADD_URL;
    public static String MEMBER_SERVICE_FAVORITE_COMPANY_DELETE_URL;
    public static String MEMBER_SERVICE_CARD_ADD_URL;
    public static String MEMBER_SERVICE_CARD_DELETE_URL;


    public static String MEMBER_SERVICE_GET_COMPANY_NAMES_URL;
    public static String MEMBER_SERVICE_GET_CUSTOMER_NAMES_URL;

    public static String MEMBER_SERVICE_GET_CUSTOMER_PROFILE_URL;
    public static String MEMBER_SERVICE_GET_COMPANY_PROFILE_URL;
    public static String MEMBER_SERVICE_GET_ADMIN_PROFILE_URL;

    public static String MEMBER_SERVICE_GET_UNVERIFIED_ADMINS_URL;
    public static String MEMBER_SERVICE_GET_UNVERIFIED_COMPANIES_URL;
    public static String MEMBER_SERVICE_VERIFY_COMPANY_URL;
    public static String MEMBER_SERVICE_VERIFY_ADMIN_URL;


    // Security Service URLs
    public static String SECURITY_SERVICE_CREATE_SESSION_URL;
    public static String SECURITY_SERVICE_DELETE_SESSION_URL;
    public static String SECURITY_SERVICE_CHECK_SESSION_URL;

    public static String SECURITY_SERVICE_CHECK_CUSTOMER_SESSION_URL;
    public static String SECURITY_SERVICE_CHECK_COMPANY_SESSION_URL;
    public static String SECURITY_SERVICE_CHECK_ADMIN_SESSION_URL;

    @PostConstruct
    public void init() {
        // Expedition Service
        EXPEDITION_SERVICE_SEARCH_EXPEDITION_URL = expeditionBaseUrl + "/api/view/customer/availableExpeditions";
        EXPEDITION_SERVICE_SEARCH_SEAT_URL = expeditionBaseUrl + "/api/view/customer/availableSeats";
        EXPEDITION_SERVICE_GET_CUSTOMER_TICKETS_SEAT_URL = expeditionBaseUrl + "/api/view/customer/allTickets";
        EXPEDITION_SERVICE_BUY_TICKET = expeditionBaseUrl + "/api/reservation/buy_ticket";
        EXPEDITION_SERVICE_CREATE_EXPEDITION_URL = expeditionBaseUrl + "/api/expeditions/create";
        EXPEDITION_SERVICE_GET_COMPANY_EXPEDITIONS_ALL_URL = expeditionBaseUrl + "/api/view/company/allExpeditions";
        EXPEDITION_SERVICE_GET_COMPANY_EXPEDITIONS_FUTURE_URL = expeditionBaseUrl + "/api/view/company/activeExpeditions";
        EXPEDITION_SERVICE_GET_COMPANY_EXPEDITION_DETAIL_URL = expeditionBaseUrl + "/api/view/company/expeditionDetails";
        EXPEDITION_SERVICE_GET_CUSTOMER_CARDS_URL = expeditionBaseUrl + "/api/reservation/view_cards";
        EXPEDITION_SERVICE_BLOCK_SEAT_URL = expeditionBaseUrl + "/api/reservation/block_seat";
        EXPEDITION_SERVICE_UNBLOCK_SEAT_URL = expeditionBaseUrl + "/api/reservation/unblock_seat";

        // Member Service
        MEMBER_SERVICE_CREDENTIALS_CHECK_URL = memberBaseUrl + "/api/auth/checkCredentials";

        MEMBER_SERVICE_CUSTOMER_REGISTRATION_URL = memberBaseUrl + "/api/register/customer";
        MEMBER_SERVICE_COMPANY_REGISTRATION_URL = memberBaseUrl + "/api/register/company";
        MEMBER_SERVICE_ADMIN_REGISTRATION_URL = memberBaseUrl + "/api/register/admin";

        MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_NAME_URL = memberBaseUrl + "/api/profile/customer/edit/name";
        MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_SURNAME_URL = memberBaseUrl + "/api/profile/customer/edit/surname";
        MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_GENDER_URL = memberBaseUrl + "/api/profile/customer/edit/gender";
        MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_EMAIL_URL = memberBaseUrl + "/api/profile/customer/edit/email";
        MEMBER_SERVICE_CUSTOMER_PROFILE_EDIT_PASSWORD_URL = memberBaseUrl + "/api/profile/customer/edit/password";
        MEMBER_SERVICE_FAVORITE_COMPANY_ADD_URL = memberBaseUrl + "/api/profile/customer/favoriteCompany/add";
        MEMBER_SERVICE_FAVORITE_COMPANY_DELETE_URL = memberBaseUrl + "/api/profile/customer/favoriteCompany/delete";
        MEMBER_SERVICE_CARD_ADD_URL = memberBaseUrl + "/api/profile/customer/card/add";
        MEMBER_SERVICE_CARD_DELETE_URL = memberBaseUrl + "/api/profile/customer/card/delete";

        MEMBER_SERVICE_GET_COMPANY_NAMES_URL = memberBaseUrl + "/api/get/company/name";
        MEMBER_SERVICE_GET_CUSTOMER_NAMES_URL = memberBaseUrl + "/api/get/customer/name";

        MEMBER_SERVICE_GET_CUSTOMER_PROFILE_URL = memberBaseUrl + "/api/profile/customer/get";
        MEMBER_SERVICE_GET_COMPANY_PROFILE_URL = memberBaseUrl + "/api/profile/company/get";
        MEMBER_SERVICE_GET_ADMIN_PROFILE_URL = memberBaseUrl + "/api/profile/admin/get";

        MEMBER_SERVICE_GET_UNVERIFIED_ADMINS_URL = memberBaseUrl + "/api/verification/get/unverified/admins";
        MEMBER_SERVICE_GET_UNVERIFIED_COMPANIES_URL = memberBaseUrl + "/api/verification/get/unverified/companies";
        MEMBER_SERVICE_VERIFY_COMPANY_URL = memberBaseUrl + "/api/verification/verify/company";
        MEMBER_SERVICE_VERIFY_ADMIN_URL = memberBaseUrl + "/api/verification/verify/admin";

        // Security Service
        SECURITY_SERVICE_CREATE_SESSION_URL = securityBaseUrl + "/api/auth/createSession";
        SECURITY_SERVICE_DELETE_SESSION_URL = securityBaseUrl + "/api/auth/logout";
        SECURITY_SERVICE_CHECK_SESSION_URL = securityBaseUrl + "/api/auth/check";

        SECURITY_SERVICE_CHECK_CUSTOMER_SESSION_URL = securityBaseUrl + "/api/auth/checkCustomer";
        SECURITY_SERVICE_CHECK_COMPANY_SESSION_URL = securityBaseUrl + "/api/auth/checkCompany";
        SECURITY_SERVICE_CHECK_ADMIN_SESSION_URL = securityBaseUrl + "/api/auth/checkAdmin";
    }
}
