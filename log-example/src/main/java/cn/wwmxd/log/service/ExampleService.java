package cn.wwmxd.log.service;

import cn.wwmxd.log.entity.Example;
import cn.wwmxd.service.IService;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author liwei
 * @title: ExampleService
 * @projectName wwmxd-log
 * @description: TODO
 * @date 2019-12-12 15:02
 */
@Service
public class ExampleService  implements IService<Example,String> {

    @Override
    public Example selectById(String id) {
        Example example=new Example();
        example.setId("test");
        //模拟更新 每次数据不一样
        example.setName(UUID.randomUUID().toString());
        return example;
    }
}
