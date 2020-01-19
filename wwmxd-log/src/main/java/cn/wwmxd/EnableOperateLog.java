package cn.wwmxd;

import cn.wwmxd.Interceptor.ModifyAspect;
import cn.wwmxd.parser.DefaultContentParse;
import cn.wwmxd.util.SpringUtil;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author liwei
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import({ModifyAspect.class, SpringUtil.class, DefaultContentParse.class})
public @interface EnableOperateLog {
}
