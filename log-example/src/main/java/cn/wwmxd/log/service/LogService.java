package cn.wwmxd.log.service;

import cn.wwmxd.entity.OperateLog;
import cn.wwmxd.service.OperatelogService;
import org.springframework.stereotype.Service;

/**
 * @author liwei
 * @title: LogService
 * @projectName wwmxd-log
 * @description: TODO
 * @date 2019-12-12 14:49
 */
@Service
public class LogService implements OperatelogService {
    @Override
    public void insert(OperateLog operatelog) {
        //这里写自定义保存方式 由于是example,这里就直接输出到控制台了
        System.out.println(operatelog);
    }
}
