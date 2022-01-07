package com.example.demo.Service.Role;

import com.example.demo.Repo.RoleRepo;
import com.example.demo.domain.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService{
    @Autowired
    private RoleRepo roleRepo;

    @Override
    public Role findByName(String name) {
        return roleRepo.findByName(name);
    }

    @Override
    public Role getRoleMember() {
        return roleRepo.findByName("MEMBER");
    }

    @Override
    public Role getRoleHost() {
        return roleRepo.findByName("HOST");
    }
}
