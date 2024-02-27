package com.barry.common.mvc.restenum;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 注册枚举信息到容器中
 * @date 2022/10/26 10:32 上午
 */
public class RestEnumRegistrar implements ImportBeanDefinitionRegistrar {

    public static final String BASE_PACKAGES = "basePackages";
    public static final String CLASS_NAMES = "classNames";
    public static final String REST_ENUM_BEAN_NAME = "restEnum";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Map<String, Object> attrs = metadata.getAnnotationAttributes(EnableRestEnum.class.getName());
        List<Class<?>> enumClassList = new ArrayList<>();
        // 扫描指定包，获取标记的枚举
        Set<String> basePackages = Arrays.stream((String[]) attrs.get(BASE_PACKAGES)).filter(StringUtils::hasText).collect(Collectors.toSet());
        enumClassList.addAll(this.getRestEnumFromBasePackages(basePackages));
        // 扫描指定类型，获取枚举
        Set<String> classNames = Arrays.stream((String[]) attrs.get(CLASS_NAMES)).filter(StringUtils::hasText).collect(Collectors.toSet());
        enumClassList.addAll(this.getRestEnumFromClassNames(classNames));
        // 解析所有枚举，生成Map<EnumClassName, List>
        Map<String, List<Map<String, Object>>> registerMap = this.generateRegisterMap(enumClassList);
        // 注入到spring容器中
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .genericBeanDefinition(RestEnumContainer.class);
        builder.addConstructorArgValue(registerMap);
        registry.registerBeanDefinition(REST_ENUM_BEAN_NAME, builder.getBeanDefinition());

    }

    private Map<String, List<Map<String, Object>>> generateRegisterMap(List<Class<?>> enumClassList) {
        Map<String, List<Map<String, Object>>> enumClassMap = new HashMap<>(enumClassList.size());
        for (Class<?> clazz : enumClassList) {
            Enum<?>[] enumConstants = (Enum<?>[]) clazz.getEnumConstants();
            List<Map<String, Object>> enumList = new ArrayList<>();
            for (Enum<?> e : enumConstants) {
                Map<String, Object> enumMap = new HashMap<>();
                String keyVal = e.name();
                Object valueVal = e.ordinal();
                int restEnumKeyNum=0, restEnumValueNum = 0;
                for (Field f : e.getClass().getDeclaredFields()) {
                    if (restEnumKeyNum > 1 || restEnumValueNum > 1) {
                        throw new RestEnumException("too many annotation @RestEnumKey/@RestEnumValue in class "+clazz.getName()+". @RestEnumKey/@RestEnumValue just only one.");
                    }
                    f.setAccessible(true);
                    if (f.getType().equals(Enum.class)) {
                        continue;
                    }
                    try {
                        RestEnumKey restEnumKey = f.getAnnotation(RestEnumKey.class);
                        if (restEnumKey != null) {
                            keyVal = String.valueOf(f.get(e));
                            restEnumKeyNum += 1;
                        }
                        RestEnumValue restEnumValue = f.getAnnotation(RestEnumValue.class);
                        if (restEnumValue != null) {
                            valueVal = f.get(e);
                            restEnumValueNum+=1;
                        }
                        RestEnumProperty restEnumProperty = f.getAnnotation(RestEnumProperty.class);
                        if (restEnumProperty != null) {
                            enumMap.put(f.getName(), f.get(e));
                        }
                    } catch (IllegalAccessException ex) {
                        throw new RestEnumException("get rest enum failure during classpath scanning", ex);
                    }

                }
                enumMap.put("key", keyVal);
                enumMap.put("value", valueVal);
                enumList.add(enumMap);
            }
            String key = clazz.getSimpleName();
            if (enumClassMap.containsKey(key)) {
                throw new RestEnumException("rest enum name: '"+key+"' Duplicate.");
            }
            enumClassMap.put(clazz.getSimpleName(), enumList);
        }
        return enumClassMap;
    }

    private List<Class<?>> getRestEnumFromClassNames(Set<String> classNames) {
        List<Class<?>> classCache = new ArrayList<>();
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                classCache.add(clazz);
            } catch (ClassNotFoundException e) {
                throw new RestEnumException("get rest enum failure during classpath scanning", e);
            }
        }
        return classCache;
    }

    private List<Class<?>> getRestEnumFromBasePackages(Set<String> basePackages) {
        List<Class<?>> classCache = new ArrayList<>();
        final String resourcePattern = "/**/*.class";
        for (String basePackage : basePackages) {
            ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(basePackage)
                    + resourcePattern;
            try {
                Resource[] resources = resourcePatternResolver.getResources(pattern);
                MetadataReaderFactory readerFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
                for (Resource resource : resources) {
                    if (!resource.isReadable()){
                        continue;
                    }
                    MetadataReader reader = readerFactory.getMetadataReader(resource);
                    //扫描到的class
                    String className = reader.getClassMetadata().getClassName();
                    Class<?> clazz = Class.forName(className);
                    //判断是否有指定注解
                    RestEnum annotation = clazz.getAnnotation(RestEnum.class);
                    if(annotation == null || !clazz.isEnum()){
                        continue;
                    }
                    classCache.add(clazz);
                }
            } catch (Exception e) {
                throw new RestEnumException("get rest enum failure during classpath scanning", e);
            }
        }
        return classCache;
    }

}
