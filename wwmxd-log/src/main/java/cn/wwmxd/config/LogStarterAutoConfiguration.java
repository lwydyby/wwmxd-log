package cn.wwmxd.config;

import cn.wwmxd.Interceptor.ModifyAspect;
import cn.wwmxd.parser.DefaultContentParse;
import cn.wwmxd.util.SpringUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author liwei
 */
@Configuration
@Import({ModifyAspect.class, SpringUtil.class, DefaultContentParse.class})
public class LogStarterAutoConfiguration {
}
