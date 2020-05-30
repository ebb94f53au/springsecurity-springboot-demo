package com.siyang.springsecurityspringbootdemo.security.filter;

import com.siyang.springsecurityspringbootdemo.security.token.TokenProvider;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 过滤器配置
 * 可以直接在security配置文件中配置
 * .addFilterBefore(new TokenFilter(tokenProvider),UsernamePasswordAuthenticationFilter.class);
 * @author /
 */
public class TokenConfigurer extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {

    private final TokenProvider tokenProvider;

    public TokenConfigurer(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void configure(HttpSecurity http) {
        TokenFilter customFilter = new TokenFilter(tokenProvider);
        //在UsernamePasswordAuthenticationFilter前增加Tokenfilter过滤器
        http.addFilterBefore(customFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
