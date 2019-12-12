package cn.wwmxd.log.controller;

import cn.wwmxd.EnableModifyLog;
import cn.wwmxd.log.entity.Example;
import cn.wwmxd.log.service.ExampleService;
import cn.wwmxd.util.ModifyName;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * @author liwei
 * @title: TestController
 * @projectName wwmxd-log
 * @description: TODO
 * @date 2019-12-12 14:51
 */
@RestController
public class TestController {

    @GetMapping("/test")
    @EnableModifyLog(modifyType = ModifyName.GET,handleName = "查询",needDefaultCompare=true)
    public String test(){
        return "hello world";
    }

    @PostMapping("/test")
    @EnableModifyLog(modifyType = ModifyName.SAVE,handleName = "保存",needDefaultCompare=true)
    public Example saveTest(){
        Example example=new Example();
        example.setId("test");
        example.setName(UUID.randomUUID().toString());
        return example;
    }

    @PutMapping("/test")
    @EnableModifyLog(modifyType = ModifyName.UPDATE,serviceclass= ExampleService.class,handleName = "更新",needDefaultCompare=true)
    public String updateTest(@RequestBody Example example){
        System.out.println(example);
        return "update success";
    }

    @DeleteMapping("/test/{id}")
    @EnableModifyLog(modifyType = ModifyName.DELETE,serviceclass= ExampleService.class,handleName = "删除",needDefaultCompare=true)
    public String updateTest(@PathVariable String id){
        System.out.println(id);
        return "delete success";
    }

}
