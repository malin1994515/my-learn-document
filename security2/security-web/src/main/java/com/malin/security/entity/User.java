package com.malin.security.entity;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
}
