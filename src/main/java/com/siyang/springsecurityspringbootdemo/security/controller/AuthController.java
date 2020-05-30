package com.siyang.springsecurityspringbootdemo.security.controller;

import com.siyang.springsecurityspringbootdemo.bean.User;
import com.siyang.springsecurityspringbootdemo.security.token.SecurityProperties;
import com.siyang.springsecurityspringbootdemo.security.token.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 * 授权、根据token获取用户详细信息
 */
@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final SecurityProperties properties;
    private final UserDetailsService userDetailsService;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public AuthController(SecurityProperties properties, UserDetailsService userDetailsService, TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.properties = properties;
        this.userDetailsService = userDetailsService;
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Object> login( User user, HttpServletRequest request){

        //security 认证
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        //将authentication 进行验证
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        //放入安全上下文
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // ①生成令牌JWTS
        String token = tokenProvider.createToken(authentication);
        // 返回 token 与 用户信息
        Map<String,Object> authInfo = new HashMap<String,Object>(2){{
            put("token", properties.getTokenStartWith() + token);
        }};
        return ResponseEntity.ok(authInfo);
    }


}
