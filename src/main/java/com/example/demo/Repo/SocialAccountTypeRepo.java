package com.example.demo.Repo;

import com.example.demo.domain.SocialAccountType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SocialAccountTypeRepo extends JpaRepository<SocialAccountType, String> {
    SocialAccountType findBySocialName(String socialName);
}
