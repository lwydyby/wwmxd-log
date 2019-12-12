package cn.wwmxd.config;

import cn.wwmxd.Interceptor.ModifyAspect;
import cn.wwmxd.util.SpringUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author liwei
 * @title: LogStarterAutoConfiguration
 * @projectName wwmxd-log
 * @description: TODO
 * @date 2019-12-12 15:59
 */
@Configuration
@Import({ModifyAspect.class, SpringUtil.class})
public class LogStarterAutoConfiguration {
}
