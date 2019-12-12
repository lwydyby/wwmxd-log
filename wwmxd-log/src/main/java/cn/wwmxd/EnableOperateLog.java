package cn.wwmxd;

import cn.wwmxd.Interceptor.ModifyAspect;
import cn.wwmxd.parser.DefaultContentParse;
import cn.wwmxd.util.SpringUtil;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author liwei
 * @title: EnableLog
 * @projectName wwmxd-log
 * @description: TODO
 * @date 2019-12-12 16:04
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Import({ModifyAspect.class, SpringUtil.class, DefaultContentParse.class})
public @interface EnableOperateLog {
}
