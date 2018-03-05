package cn.wwmxd.util;

import cn.wwmxd.service.OperatelogService;
import cn.wwmxd.service.impl.OperatelogServiceImpl;
import org.springframework.context.annotation.Bean;

public class ModifyAutoConfig {

    @Bean
    public OperatelogService operatelogService(){
        return new OperatelogServiceImpl();
    }



}
