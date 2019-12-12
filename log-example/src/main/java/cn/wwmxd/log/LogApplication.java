package cn.wwmxd.log;

import cn.wwmxd.EnableOperateLog;
import cn.wwmxd.util.SpringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableOperateLog
public class LogApplication {

    @Autowired
    SpringUtil springUtil;

    public static void main(String[] args) {
        SpringApplication.run(LogApplication.class, args);
    }

}
