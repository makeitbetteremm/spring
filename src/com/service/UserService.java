package com.service;

import com.spring.*;

@Component("userService")
@Scope("singleton")
public class UserService implements BeanNameAware, InitializingBean,UserInterface {
    @AutoWired
    private OrderService orderService;

    private String beanName;
    @Override
    public void test() {
        System.out.println(orderService);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println(beanName);
    }
}
