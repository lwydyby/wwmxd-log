package cn.wwmxd;

import cn.wwmxd.util.ModifyAutoConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 是否引入详细记录
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import(ModifyAutoConfig.class)
public @interface EnableLogAspect {
}
