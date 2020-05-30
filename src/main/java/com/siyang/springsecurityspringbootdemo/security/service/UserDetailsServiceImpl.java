package com.siyang.springsecurityspringbootdemo.security.service;

import com.siyang.springsecurityspringbootdemo.bean.Permission;
import com.siyang.springsecurityspringbootdemo.bean.Role;
import com.siyang.springsecurityspringbootdemo.bean.User;
import com.siyang.springsecurityspringbootdemo.security.vo.JwtUser;
import com.siyang.springsecurityspringbootdemo.service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Zheng Jie
 * @date 2018-11-22
 */
//放到ioc容器中
@Service("userDetailsService")
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true, rollbackFor = Exception.class)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserService userService;


    public UserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username){

        User user = userService.getUserByUsername(username);
        if(user == null) {
            throw new BadCredentialsException("坏的凭证");
        }
        return createJwtUser(user);
    }

    private UserDetails createJwtUser(User user) {
        // 将permission转化为 GrantedAuthority
        Set<Role> roles = user.getRoles();
        Set<String> permissions = new HashSet<>();
        roles.stream().forEach(role -> {
            permissions.addAll(
                    role.getPermissions().stream().map(Permission::getPermissionValue).collect(Collectors.toSet())
            );

        });
        Collection<GrantedAuthority> collect = permissions.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        JwtUser jwtUser = new JwtUser(user.getId(), user.getUsername(), user.getPassword(), collect);
        return jwtUser;
    }
}
