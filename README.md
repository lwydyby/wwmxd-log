 

### 简介
   当对业务内容进行编辑时，记录何人何时何ip进行何种改动（包含了原值和修改后的值），保存到数据库中


### 环境
- maven
- jdk 1.8
- spring boot 1.5.5 release
- mysql 5.0+
- mybatis-plus
- fastjson
- aop
### 使用
1. 在需要记录的方法上使用注解EnableGameleyLog
参数如下：
```
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface EnableGameleyLog {
    /**
     * 操作的中文说明 可以直接调用ModifyName
     * @return
     */
    String name() default "";

    /**
     * 获取编辑信息的解析类，目前为使用id获取，复杂的解析需要自己实现，默认不填写
     * 则使用默认解析类
     * @return
     */
    Class parseclass() default DefaultContentParse.class;

    /**
     * 查询数据库所调用的class文件
     * @return
     */
    Class serviceclass() default IService.class;

    /**
     * 前台字段名称
     */
    String[] feildName() default {"id"};

}
```
简单例子：
 ```
 @EnableGameleyLog(name = ModifyName.SAVE,serviceclass = DemoService.class)
    public BaseResponse addDemo(@RequestBody Demo demo){
        ...
    }
```
2.编写解析类，默认的解析类为使用id查询，自定义的解析类请继承ContentParser接口，并在注解中赋值
```
 
/**
 * 基础解析类
 * 单表编辑时可以直接使用id来查询
 * 如果为多表复杂逻辑，请自行编写具体实现类
 * @author zk
 * @date 2018-03-02
 */
public class DefaultContentParse implements ContentParser {
    @Override
    public Object getResult(Map<String,Object> feildValues, EnableGameleyLog enableGameleyLog) {
        Assert.isTrue(feildValues.containsKey("id"),"未解析到id值，请检查前台传递参数是否正确");
        Object result= feildValues.get("id");
        Integer id=0;
        if(result instanceof String){
            id= Integer.parseInt((String) result);

        }else if(result instanceof Integer){
            id= (Integer) result;
        }
        IService service= null;
        Class cls=enableGameleyLog.serviceclass();
        service = (IService) SpringUtil.getBean(cls);

        return  service.selectById(id);
    }


}
 
```
3.默认的操作方式有：
 ```
public class ModifyName {
    public final static String SAVE="新建";
    public final static String UPDATE="编辑";
    public final static String DELETE="删除";
}
```
4.如需记录操作字段中文请在entity中使用DataName注解
 如：
 ```
@DataName(name="操作日期")
	    private String modifydate;
```
5.在启动类上增加控制注解，只有该注解存在时才会启用该记录
```
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
@EnableLogAspect
public class UserServiceApplication {
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}
}

```

### 展示图
![输入图片说明](https://gitee.com/uploads/images/2018/0305/115255_5d615e74_1463938.png "深度截图_选择区域_20180305115212.png")


### 建表语句
```
DROP TABLE IF EXISTS `operatelog`;
CREATE TABLE `operatelog`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作人',
  `modifydate` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作日期',
  `modifyname` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作名词',
  `modifyobject` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作对象',
  `modifycontent` varchar(3000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '操作内容',
  `modifyip` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'ip',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;
```