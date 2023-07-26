package com.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserApplicationContext {
    private Class configClass;
    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionHashMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Object> beanMap = new ConcurrentHashMap<>();
    private String singletonStr = "singleton";


    public UserApplicationContext(Class configClass) {

        // 扫描项目创建BeanDefinition
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
                                Component component = clazz.getAnnotation(Component.class);
                                String beanName = component.value();
                                if ("".equals(beanName)) {
                                    beanName = Introspector.decapitalize(clazz.getSimpleName());
                                }

                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setClazz(clazz);
                                if (clazz.isAnnotationPresent(Scope.class)) {
                                    Scope scope = clazz.getAnnotation(Scope.class);
                                    beanDefinition.setScope(scope.value());
                                } else {
                                    beanDefinition.setScope(singletonStr);
                                }
                                beanDefinitionHashMap.put(beanName, beanDefinition);
                            }
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                    }

                }
            }

            // 创建单例bean
            for (Map.Entry<String, BeanDefinition> entry : beanDefinitionHashMap.entrySet()) {
                if (singletonStr.equals(entry.getValue().getScope())) {
                    Object singleBean = createBean(entry.getKey(),entry.getValue());
                    beanMap.put(entry.getKey(),singleBean);
                }
            }

        }
    }

    private Object createBean(String beanName, BeanDefinition beanDefinition) {
        Class clazz = beanDefinition.getClazz();
        try {
            Object instance = clazz.getConstructor().newInstance();

            // 依赖注入
            Field[] declaredFields = instance.getClass().getDeclaredFields();
            for (Field f : declaredFields) {
                if (f.isAnnotationPresent(AutoWired.class)) {
                    f.setAccessible(true);
                    f.set(instance,getBean(f.getName()));
                }
            }

            if (instance instanceof BeanNameAware) {
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            return instance;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
    public Object getBean(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionHashMap.get(beanName);
        if (beanDefinition == null) {
            throw new NullPointerException();
        }

        if (singletonStr.equals(beanDefinition.getScope())) {
            Object singleBean = beanMap.get(beanName);
            if (singleBean==null) {
                singleBean = createBean(beanName,beanDefinition);
            }
            return singleBean;
        } else {
            return createBean(beanName,beanDefinition);
        }
    }
}
