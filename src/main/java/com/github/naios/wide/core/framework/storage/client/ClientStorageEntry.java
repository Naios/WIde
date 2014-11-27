package com.github.naios.wide.core.framework.storage.client;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ClientStorageEntry
{
    int idx();

    String name() default "";

    boolean key() default false;

    String description() default "";
}
