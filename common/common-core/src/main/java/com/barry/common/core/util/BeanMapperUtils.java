package com.barry.common.core.util;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.Type;
import ma.glasnost.orika.metadata.TypeFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Bean copy 工具类</p>
 *
 * @author chenpeng
 * Create time 2018年12月3日 下午2:49:47
 */
public final class BeanMapperUtils {

    private static final MapperFactory MAP_NONE_NULL_FIELDS_MAPPER_FACTORY = new DefaultMapperFactory.Builder().mapNulls(false).build();
    private static final MapperFactory MAP_NULL_FIELDS_MAPPER_FACTORY = new DefaultMapperFactory.Builder().mapNulls(true).build();

    private BeanMapperUtils() {
    }

    /**
     * <p><简单的复制出新类型对象</p>
     * <p>
     * 通过source.getClass() 获得源Class
     */
    public static <S, D> D map(S source, Class<D> destinationClass) {
        return buildClassMapperFacade(false).map(source, destinationClass);
    }

    /**
     * <p>极致性能的复制出新类型对象.</p>
     * <p>
     * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
     */
    public static <S, D> D map(S source, Type<S> sourceType, Type<D> destinationType) {
        return buildClassMapperFacade(false).map(source, sourceType, destinationType);
    }

    /**
     * <p>对象拷贝</p>
     * source 非null的字段,会被拷贝的dest对象中
     *
     * @author chenpeng
     * Create at March 6, 2019 at 17:22:38 GMT+8
     */
    public static <S, D> void copy(S source, D dest) {
        buildClassMapperFacade(false).map(source, dest);
    }

    /**
     * <p>简单的复制出新对象列表到ArrayList</p>
     * <p>
     * 不建议使用mapper.mapAsList(Iterable<S>,Class<D>)接口, sourceClass需要反射，实在有点慢
     */
    public static <S, D> List<D> mapList(Iterable<S> sourceList, Class<S> sourceClass, Class<D> destinationClass) {
        return buildClassMapperFacade(false).mapAsList(sourceList,
                TypeFactory.valueOf(sourceClass),
                TypeFactory.valueOf(destinationClass));
    }

    /**
     * <p>极致性能的复制出新类型对象到ArrayList.<p>
     * <p>
     * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
     */
    public static <S, D> List<D> mapList(Iterable<S> sourceList, Type<S> sourceType, Type<D> destinationType) {
        return buildClassMapperFacade(false).mapAsList(sourceList, sourceType, destinationType);
    }

    /**
     * <p>简单复制出新对象列表到数组<p>
     * <p>
     * 通过source.getComponentType() 获得源Class
     */
    public static <S, D> D[] mapArray(final D[] destination, final S[] source, final Class<D> destinationClass) {
        return buildClassMapperFacade(false).mapAsArray(destination, source, destinationClass);
    }

    /**
     * <p>极致性能的复制出新类型对象到数组<p>
     * <p>
     * 预先通过BeanMapper.getType() 静态获取并缓存Type类型，在此处传入
     */
    public static <S, D> D[] mapArray(D[] destination, S[] source, Type<S> sourceType, Type<D> destinationType) {
        return buildClassMapperFacade(false).mapAsArray(destination, source, sourceType, destinationType);
    }

    private static MapperFacade buildClassMapperFacade(boolean mapNulls) {
        return (mapNulls
                ? MAP_NULL_FIELDS_MAPPER_FACTORY.getMapperFacade()
                : MAP_NONE_NULL_FIELDS_MAPPER_FACTORY.getMapperFacade());
    }

    /**
     * 抽取Bean的字段映射 ,并根据mappings进行相似性过滤.
     *
     * @param clazz
     * @param mappings
     * @param prefix
     * @param suffix
     * @return map key是mappings的key. value是field
     */
    public static Map<String, Field> mappingField(Class<?> clazz,List<String> mappings, String prefix, String suffix) {
        Map<String, Field> map = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            String mapping = mappings.stream().filter(m->StringUtils.startsWithIgnoreCase(fieldName, prefix + m + suffix)).findFirst().orElse(null);
            if(StringUtils.isNotBlank(mapping)){
                map.put(mapping, field);
            }
        }
        return map;
    }

    /**
     * 根据MappingField 获取对应field的值
     *
     * @param mappingField
     * @param bean
     * @return map的key是mappings的key map的value是field的值
     * @throws IllegalAccessException
     */
    public static Map<String, Object> toMapByMappingFiled(Map<String, Field> mappingField, Object bean) throws IllegalAccessException {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<String, Field> entry : mappingField.entrySet()) {
            Field field = entry.getValue();
            if(field!=null){
                field.setAccessible(true);
                map.put(entry.getKey(), field.get(bean));
            }
        }
        return map;
    }

}
