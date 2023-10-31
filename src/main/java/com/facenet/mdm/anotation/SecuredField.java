package com.facenet.mdm.anotation;

import java.lang.annotation.*;

@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface SecuredField {
    String value() default "";
}
