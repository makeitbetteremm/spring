package com.service;

import com.spring.AutoWired;
import com.spring.BeanNameAware;
import com.spring.Component;
import com.spring.Scope;

@Component("userService")
@Scope("singleton")
public class UserService implements BeanNameAware {
    @AutoWired
    private OrderService orderService;

    private String beanName;
    public void test() {
        System.out.println(orderService);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }
}
