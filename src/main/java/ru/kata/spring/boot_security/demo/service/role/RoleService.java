package ru.kata.spring.boot_security.demo.service.role;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.Set;

public interface RoleService {
    void saveRole(Role role);
    Role findByRole(String role);
    Set<Role> findAll();
}
