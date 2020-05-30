package com.siyang.springsecurityspringbootdemo.controller;

import com.siyang.springsecurityspringbootdemo.bean.User;
import com.siyang.springsecurityspringbootdemo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author siyang
 * @create 2020-05-29 12:00
 */
@RestController
@RequestMapping("/")
public class UserController {
    @Autowired
    UserService userService;

    @GetMapping("getAll")
    @PreAuthorize("hasAuthority('read')")
    public ResponseEntity getAllUsers(){
        List<User> all = userService.findAll();
        Map<String, Object> map = new HashMap<>();
        map.put("list",all);

        return ResponseEntity.ok(map);

    }

}
