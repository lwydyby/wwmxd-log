 
 ![badge](https://action-badges.now.sh/lwydyby/wwmxd-log)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.gitee.lwydyby/wwmxd-log/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.gitee.lwydyby/wwmxd-log)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

### 简介
   当对业务内容进行编辑时，记录何人何时何ip进行何种改动（包含了原值和修改后的值），保存到数据库中（1.0.3版本开始不再局限于数据库,可以自定义保存方式）
    
   2019/12/12 更新：优化了operateLog对象存储的信息,增加自定义logService的可操作性。并增加示例代码，方便上手
    
   2019/07/02 更新：鉴于总有人QQ问我如何使用，增加starter的功能自动加载，可以直接pom中引用直接使用
   
   2020/01/19 更新: 解决并发问题,同时项目转至github(后边和gitee同步更新),使用github actions自动上传至maven官方仓库
   
   github地址： https://github.com/lwydyby/wwmxd-log
   
```
 <dependency>
  <groupId>com.gitee.lwydyby</groupId>
  <artifactId>wwmxd-log</artifactId>
  <version>${latest.version}</version>
</dependency>
```


### 环境
- maven
- jdk 1.8
- spring boot 1.5.5 release
- fastjson
- aop
### 使用
1. 在需要记录的方法上使用注解EnableModifyLog 参数如下：
 ```java
 @Documented
 @Retention(RetentionPolicy.RUNTIME)
 @Target({ElementType.METHOD})
 public @interface EnableModifyLog {
     /**
          * @return 操作的中文说明 可以直接调用ModifyName
          */
         ModifyName modifyType() default ModifyName.UPDATE;
     
         /**
          * @return 获取编辑信息的解析类，目前为使用id获取，复杂的解析需要自己实现，默认不填写
          *       则使用默认解析类
          */
         Class parseclass() default DefaultContentParse.class;
     
         /**
          * @return 查询数据库所调用的class文件
          */
         Class serviceclass() default IService.class;
     
         /**
          * @return 具体业务操作名称
          */
         String handleName() default "";
     
         /**
          * @return 是否需要默认的改动比较
          */
         boolean needDefaultCompare() default false;
     
         /**
          * @return id的类型
          */
         Class idType() default String.class;
 
 }
```
简单例子：
 ```java
 @EnableModifyLog(modifyType = ModifyName.SAVE,serviceclass = DemoService.class)
    public BaseResponse addDemo(@RequestBody Demo demo){
        ...
    }
```
2.编写解析类，默认的解析类为使用id查询，自定义的解析类请继承ContentParser接口，并在注解中赋值
```java
 
/**
 * 基础解析类
 * 单表编辑时可以直接使用id来查询
 * 如果为多表复杂逻辑，请自行编写具体实现类
 * @author zk
 * @date 2018-03-02
 */
public class DefaultContentParse implements ContentParser {
      @Override
        public Object getResult(JoinPoint joinPoint, EnableModifyLog enableModifyLog) {
            Object info = joinPoint.getArgs()[0];
            Object result = ReflectionUtils.getFieldValue(info, "id");
            Assert.notNull(result,"未解析到id值，请检查前台传递参数是否正确");
            Class idType=enableModifyLog.idType();
            if(idType.isInstance(result)){
                Class cls=enableModifyLog.serviceclass();
                IService service = (IService) SpringUtil.getBean(cls);
                return  service.selectById(idType.cast(result));
            }else {
                throw new RuntimeException("请核实id type");
            }
        }


}
 
```
3.自定义查询方式实现IService接口 (不局限与直接查数据库，如果考虑到性能压力，可以从缓存中取)
```java
public interface IService<T,S> {
    T selectById(S id);
}
```



4.自行根据需求实现OperatelogService接口（jpa/mybatis都可以）

```java
public interface OperatelogService {
    void insert(Operatelog operatelog);
}
```


5.默认的操作方式有：
 ```java
public enum  ModifyName {
    SAVE,UPDATE,DELETE,GET;
}
```
6.如需记录操作字段中文请在entity中使用DataName注解
 如：
 ```java
@DataName(name="操作日期")
	    private String modifydate;
```

6.将用户信息存入BaseContextHandler（如果需要记录操作人，请在拦截器上自行注入）



### 展示图
![输入图片说明](https://gitee.com/uploads/images/2018/0305/115255_5d615e74_1463938.png "深度截图_选择区域_20180305115212.png")


### 建表语句 

仅供参考,可以自定义自己的
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
