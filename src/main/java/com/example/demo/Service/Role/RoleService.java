package com.example.demo.Service.Role;

import com.example.demo.domain.Role;

public interface RoleService {
    Role findByName(String name);
    Role getRoleMember();
    Role getRoleHost();
}
