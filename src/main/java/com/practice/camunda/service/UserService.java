package com.practice.camunda.service;

import com.practice.camunda.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    public List<String> getUserRoles() {
        return List.of(Role.CASHBACK_PRODUCT_DEVELOPMENT_MAKER, Role.CASHBACK_PRODUCT_DEVELOPMENT_CHECKER, Role.CASHBACK_CARDS_CHECKER);
    }
}
