package cn.wwmxd;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface DataName {
    /**
     * @return 字段名称
     */
    String name() default "";

 }
