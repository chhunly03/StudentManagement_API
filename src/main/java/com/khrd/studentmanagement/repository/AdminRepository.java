package com.khrd.studentmanagement.repository;

import com.khrd.studentmanagement.model.entity.Admin;
import com.khrd.studentmanagement.model.entity.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AdminRepository extends JpaRepository<Admin, UUID> {
    Optional<Admin> findByEmail(String email);

    List<Admin> findAllByRole(Role role);

    Optional<Admin> findByIdAndRole(UUID id, Role role);

    Admin findAdminByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE Admin a SET a.role = :role WHERE a.email = :email")
    Admin updateAdminRoleByEmail(@Param("email") String email, @Param("role") Role role);

}
