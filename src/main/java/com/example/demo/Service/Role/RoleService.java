package com.example.demo.Service.Role;

import com.example.demo.domain.Role;
import org.springframework.stereotype.Service;

public interface RoleService {
    Role findByRoleName(String name);
    void updateRole(Role role);
}
