package spes.store.anno;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface StorageType {
    /* Data Type or Name or Summary */
    String value();
    /* describe what was supported for read or write */
    String desc();
}
