package wide.core.framework.storage;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import wide.core.framework.game.GameBuild;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface GamebuildDependency
{
    GameBuild build();
}
