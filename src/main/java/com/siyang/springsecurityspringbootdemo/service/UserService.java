package com.siyang.springsecurityspringbootdemo.service;

import com.siyang.springsecurityspringbootdemo.bean.User;
import com.siyang.springsecurityspringbootdemo.common.BaseService;

/**
 * @author siyang
 * @create 2020-05-29 10:54
 */
public interface UserService extends BaseService<User> {

    public User getUserByUsername(String username);

}
