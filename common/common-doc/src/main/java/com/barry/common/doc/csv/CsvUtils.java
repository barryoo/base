package com.barry.common.doc.csv;


import com.opencsv.bean.*;

import java.io.InputStreamReader;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * @date 2020-12-21
 */
public final class CsvUtils {
    private CsvUtils() {
    }

    public static <T> MappingStrategy<T> getPositionMapping(Class<T> clazz) {
        ColumnPositionMappingStrategy<T> mapper = new ColumnPositionMappingStrategy<>();
        mapper.setType(clazz);
        return mapper;
    }

    public static <T> MappingStrategy<T> getNameMapping(Class<T> clazz) {
        HeaderColumnNameMappingStrategy<T> strategy = new HeaderColumnNameMappingStrategy<>();
        strategy.setType(clazz);
        return strategy;
    }

    public static <T> MappingStrategy<T> getOrderByMapping(Class<T> clazz) {
        CustomMappingStrategy<T> strategy = new CustomMappingStrategy<>();
        strategy.setType(clazz);
        return strategy;
    }

    public static <T> MappingStrategy<T> getMapMapping(Class<T> clazz, Map<String, String> columnMapping) {
        HeaderColumnNameTranslateMappingStrategy<T> strategy = new HeaderColumnNameTranslateMappingStrategy<>();
        strategy.setType(clazz);
        strategy.setColumnMapping(columnMapping);
        return strategy;
    }

    public static <T> List<T> csvToBean(MappingStrategy<T> mappingStrategy, InputStreamReader inputStream, Character separator, Character quoteChar,
            Integer skipLines) throws Exception {
        CsvToBean<T> csvToBean = new CsvToBeanBuilder<T>(inputStream)
                .withSeparator((null == separator) ? ',' : separator)
                .withQuoteChar((null == quoteChar) ? '\'' : quoteChar)
                .withMappingStrategy(mappingStrategy)
                .withSkipLines((null == skipLines) ? 0 : separator)
                .build();
        return csvToBean.parse();
    }


    public static <T> void beanToCsv(MappingStrategy<T> mappingStrategy, Writer writer, Character separator, List<T> data) throws Exception {
        StatefulBeanToCsvBuilder<T> builder = new StatefulBeanToCsvBuilder<>(writer);
        StatefulBeanToCsv<T> beanToCsv = builder.withMappingStrategy(mappingStrategy)
                .withSeparator((null == separator) ? ',' : separator).withApplyQuotesToAll(false).build();
        beanToCsv.write(data);
    }
}
