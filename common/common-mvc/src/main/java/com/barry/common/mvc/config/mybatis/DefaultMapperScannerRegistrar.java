package com.barry.common.mvc.config.mybatis;

import com.barry.common.mvc.base.CrudMapper;
import com.barry.common.core.exception.ApplicationException;
import com.barry.common.core.exception.SystemErrorCode;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.mybatis.spring.annotation.MapperScannerRegistrar;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * <p>Mapper scanner 扩展</p>
 *
 * @author chenpeng
 * Create time Dec 13, 2018 7:38:49 PM
 */
public class DefaultMapperScannerRegistrar extends MapperScannerRegistrar {

    private static final String PKG_SEPARATOR = ".";
    private static final String PKG_DEFAULT_PARTTERN = "%s.**.persistence.mapper";

    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);

        // this check is needed in Spring 3.1
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }

        scanner.setMarkerInterface(CrudMapper.class);
        scanner.registerFilters();
        scanner.doScan(resolvePackage());
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private String resolvePackage() {
        List<String> pkgs = Lists.newArrayList(Splitter.on(PKG_SEPARATOR)
                .split(this.getClass().getPackage().getName()));
        if (pkgs.size() == 0 || StringUtils.isEmpty(pkgs.get(0))) {
            throw new ApplicationException(SystemErrorCode.SYS_SYSTEM_PKG_RESOLVE_ERROR,
                    "根 package 路径未找到，[" + this.getClass().getName() + "] 类需放在包下");
        }
        return String.format(PKG_DEFAULT_PARTTERN, pkgs.get(0));
    }
}
