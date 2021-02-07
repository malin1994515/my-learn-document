package org.example.dao;

import org.example.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<User, Long> {

    User findUserByUsername(String username);
}
