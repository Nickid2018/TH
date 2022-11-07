package io.github.nickid2018.tiny2d;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface RenderThreadOnly {
}
