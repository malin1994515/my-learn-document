package com.malin.security.config.security;

import cn.hutool.core.collection.CollUtil;
import com.malin.security.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private static List<User> userList;

    static {
        User super_admin = new User();
        super_admin.setId(1L);
        super_admin.setUsername("super_admin");
        super_admin.setPassword("123456");

        User admin = new User();
        admin.setId(2L);
        admin.setUsername("admin");
        admin.setPassword("123456");

        User user = new User();
        user.setId(2L);
        user.setUsername("admin");
        user.setPassword("123456");

        userList = CollUtil.newArrayList(super_admin, admin, user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userList.stream().filter(s -> s.getUsername().equals(username)).findFirst();
        if (!user.isPresent()) throw new UsernameNotFoundException("用户名未匹配到用户");


        SecurityUserDetail userDetail = new SecurityUserDetail(user.get(), CollUtil.newArrayList("ROLE_user"));
        return userDetail;
    }
}
