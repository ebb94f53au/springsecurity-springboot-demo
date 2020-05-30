package com.siyang.springsecurityspringbootdemo.security.filter;

import com.alibaba.fastjson.JSON;
import com.siyang.springsecurityspringbootdemo.common.exception.BadRequestException;
import com.siyang.springsecurityspringbootdemo.common.utils.SpringContextHolder;
import com.siyang.springsecurityspringbootdemo.security.token.SecurityProperties;
import com.siyang.springsecurityspringbootdemo.security.token.TokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author /
 */
@Slf4j
public class TokenFilter extends GenericFilterBean {

   private final TokenProvider tokenProvider;


   public TokenFilter(TokenProvider tokenProvider) {
      this.tokenProvider = tokenProvider;
   }

   @Override
   public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
      HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
      HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
      String token = resolveToken(httpServletRequest);
      String requestRri = httpServletRequest.getRequestURI();

      //如果 token不为空 并且 验证token没有问题

      if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {
         //得到认证对象
         Authentication authentication = tokenProvider.getAuthentication(token);
         //将认证对象 进行建立安全上下文
         SecurityContextHolder.getContext().setAuthentication(authentication);
         log.debug("set Authentication to security context for '{}', uri: {}", authentication.getName(), requestRri);
      } else {
         log.debug("no valid JWT token found, uri: {}", requestRri);

      }
      filterChain.doFilter(servletRequest, servletResponse);
   }

   /**
    * 从request中获得token
    * @param request
    * @return
    */
   private String resolveToken(HttpServletRequest request) {
      //获得me.zhengjie.modules.security.config.SecurityProperties的安全配置数据
      SecurityProperties properties = SpringContextHolder.getBean(SecurityProperties.class);
      //获得header中的安全配置的header：“Authorization”，HttpServletRequest从request-》coyoteRequest-》headers-》headers中获得header
      String bearerToken = request.getHeader(properties.getHeader());

      if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(properties.getTokenStartWith())) {
         //返回token中的header=》“bearer ”之后的内容
         return bearerToken.substring(7);
      }
      return null;
   }
}
