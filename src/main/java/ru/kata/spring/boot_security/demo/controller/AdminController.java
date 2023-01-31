package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.role.RoleService;
import ru.kata.spring.boot_security.demo.service.role.RoleServiceImpl;
import ru.kata.spring.boot_security.demo.service.user.UserService;
import ru.kata.spring.boot_security.demo.service.user.UserServiceImpl;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserServiceImpl userService, RoleServiceImpl roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/")
    public String userPage(@ModelAttribute("add_user")
                           @Valid
                           User addUser,
                           Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userService.loadUserByEmail(userDetails.getUsername());

        Set<Role> allRole = roleService.findAll();

        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("user", user);
        model.addAttribute("roles", allRole);

        return "/admin";
    }

    @PostMapping("/")
    public String addUser(@ModelAttribute
                          @Valid User user,
                          @RequestParam(value = "role", required = false) String userRole) {

        Role role = roleService.findByRole(userRole);
        role.addUserToRole(user);

        userService.addUser(user);

        return "redirect:/admin/";
    }

    @PatchMapping("/")
    public String saveUser(@ModelAttribute
                           @Valid User user,
                           @ModelAttribute("rolesForUser") Set<Role> roles) {

        //Устанавливыаю роли
        for (Role role : roles) {
            user.addRoleToUser(role);
        }

        userService.updateUser(user);
        return "redirect:/admin/";
    }


    @DeleteMapping("/remove")
    public String removeUser(@RequestParam(value = "id", required = false) Long id) {
        userService.deleteUser(id);
        return "redirect:/admin/";
    }
}
