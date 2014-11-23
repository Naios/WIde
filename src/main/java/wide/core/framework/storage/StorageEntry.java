package wide.core.framework.storage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface StorageEntry
{
    int idx();

    String name() default "";
    
    boolean key() default false;

    String description() default "";
}
