package com.techshop.admin.user;

import com.techshop.admin.paging.SearchRepository;
import com.techshop.common.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserRepository extends SearchRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.email= :email")
    public User getUserByEmail(@Param("email") String email);

    @Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
    @Modifying
    public void updateEnabledStatus(Long id, boolean enabled);

    //    @Query("SELECT u FROM User u WHERE u.firstName LIKE %?1% OR u.lastName LIKE  %?1% " +
//            "OR u.email LIKE  %?1%")
//    public Page<User> findAll(String keyword, Pageable pageable);
    @Query("SELECT u FROM User u WHERE CONCAT(u.id, ' ', u.email, ' ', u.firstName, ' ', u.lastName) LIKE  %?1%")
    public Page<User> findAll(String keyword, Pageable pageable);


}