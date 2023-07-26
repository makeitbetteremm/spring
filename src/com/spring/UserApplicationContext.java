package com.spring;

import java.io.File;
import java.net.URL;

public class UserApplicationContext {
    private Class configClass;

    public UserApplicationContext(Class configClass) {
        this.configClass = configClass;
        if (configClass.isAnnotationPresent(ComponentScan.class)) {
            ComponentScan componentScan = (ComponentScan) configClass.getDeclaredAnnotation(ComponentScan.class);
            String path = componentScan.value();
            path = path.replace(".", "/");
            System.out.println(path);

            ClassLoader classLoader = UserApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);
            File file = new File( resource.getFile());
            System.out.println(file);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    String fileName = f.getAbsolutePath();
                    if (fileName.endsWith("class")) {
                        System.out.println(fileName);
                        String className = fileName.substring(fileName.indexOf("com"), fileName.lastIndexOf(".class"));
                        className = className.replace('\\','.');
                        try {
                            Class<?> clazz = classLoader.loadClass(className);
                            if (clazz.isAnnotationPresent(Component.class)) {

                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                    }

                }
            }

        }
    }

    public Object getBean(String beanName) {
        return null;
    }
}
