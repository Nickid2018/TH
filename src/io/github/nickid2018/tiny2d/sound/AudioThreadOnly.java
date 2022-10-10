package io.github.nickid2018.tiny2d.sound;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.SOURCE)
@Documented
public @interface AudioThreadOnly {
}
