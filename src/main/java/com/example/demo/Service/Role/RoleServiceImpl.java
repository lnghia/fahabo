package com.example.demo.Service.Role;

import com.example.demo.Repo.RoleRepo;
import com.example.demo.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    RoleRepo roleRepo;

    @Override
    public Role findByRoleName(String name) {
        return roleRepo.getByRoleName(name);
    }

    @Override
    public void updateRole(Role role) {
        roleRepo.save(role);
    }
}
