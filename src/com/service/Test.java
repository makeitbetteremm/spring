package com.service;

import com.spring.UserApplicationContext;

public class Test {
    public static void main(String[] args) {
        UserApplicationContext  applicationContext = new UserApplicationContext(AppConfig.class);

        UserInterface userService = (UserInterface) applicationContext.getBean("userService");
        userService.test();

    }
}
