package com.barry.common.core.util;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;

import java.util.Objects;
import java.util.Optional;

/**
 * @author barry chen
 * @date 2023/6/9 11:09
 */
@Slf4j
public class OptTest {

    @Test
    public void test() {
        Person person = new Person();
        person.setName("asdf");
        person.setAge(1);
        Opt.ofJavaUtil(Optional.ofNullable(person), Objects::nonNull)
                .peek(p -> {
                    p.setName(p.getName() + " peek");
                }).filter(p -> p.getName().equals("asdf peek"))
                .map(p -> {
                    Person p2 = new Person();
                    p2.setName(p.getName() + " map");
                    p2.setAge(1);
                    return p2;
                }).mapCol(p -> Lists.newArrayList(p))
                .present(CollectionUtils::isNotEmpty)
                .or(() -> Opt.ofEmptyable(Lists.newArrayList()))
                .peek(l->{
                    System.out.println(l.get(0).getName());
                })
                .filter(CollectionUtils::isNotEmpty)
                .mapStr(l-> l.stream().findFirst().get().getName())
                .ifMatchOrElse(n->n.equalsIgnoreCase("asdf peek map"), n->log.info("match"), ()->log.info("not match"));
    }

    @Data
    public static class Person {
        String name;
        int age;
    }

}
