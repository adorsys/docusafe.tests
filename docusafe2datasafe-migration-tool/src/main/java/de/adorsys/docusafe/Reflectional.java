package de.adorsys.docusafe;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

@UtilityClass
public class Reflectional {

    @SneakyThrows
    public static  <T> T getReflectionally(String fieldName, Object owner) {
        Field field = owner.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return (T) field.get(owner);
    }
}
