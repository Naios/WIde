package wide.core.framework.storage;

import java.lang.annotation.Retention;

import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import wide.core.framework.game.Gamebuild;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface StorageStructure
{
    Gamebuild build();
}
