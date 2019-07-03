 

### 简介
   当对业务内容进行编辑时，记录何人何时何ip进行何种改动（包含了原值和修改后的值），保存到数据库中

   2019/07/02 更新：鉴于总有人QQ问我如何使用，增加starter的功能自动加载，可以直接pom中引用直接使用
   官方库地址：
   
```
 <dependency>
  <groupId>com.gitee.lwydyby</groupId>
  <artifactId>wwmxd-log</artifactId>
  <version>1.0.2-Release</version>
</dependency>
```


### 环境
- maven
- jdk 1.8
- spring boot 1.5.5 release
- mysql 5.0+
- mybatis-plus
- fastjson
- aop
### 使用
1. 在需要记录的方法上使用注解EnableModifyLog
 参数如下：
 ```
 @Documented
 @Retention(RetentionPolicy.RUNTIME)
 @Target({ElementType.METHOD})
 public @interface EnableModifyLog {
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
 @EnableModifyLog(name = ModifyName.SAVE,serviceclass = DemoService.class)
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
    public Object getResult(Map<String,Object> feildValues, EnableModifyLog enableModifyLog) {
        Assert.isTrue(feildValues.containsKey("id"),"未解析到id值，请检查前台传递参数是否正确");
        Object result= feildValues.get("id");
        Integer id=0;
        if(result instanceof String){
            id= Integer.parseInt((String) result);

        }else if(result instanceof Integer){
            id= (Integer) result;
        }
        IService service= null;
        Class cls=enableModifyLog.serviceclass();
        service = (IService) SpringUtil.getBean(cls);

        return  service.selectById(id);
    }


}
 
```

3.自行根据需求实现OperatelogService接口（jpa/mybatis都可以）

```
public interface OperatelogService {
    void insert(Operatelog operatelog);
}
```


4.默认的操作方式有：
 ```
public class ModifyName {
    public final static String SAVE="新建";
    public final static String UPDATE="编辑";
    public final static String DELETE="删除";
}
```
5.如需记录操作字段中文请在entity中使用DataName注解
 如：
 ```
@DataName(name="操作日期")
	    private String modifydate;
```

6.将用户信息存入BaseContextHandler（如果需要记录操作人，请在拦截器上自行注入）


主要aop实现类，代码如下：

```
package cn.wwmxd.Interceptor;



import cn.wwmxd.EnableModifyLog;
import cn.wwmxd.util.ClientUtil;
import cn.wwmxd.entity.Operatelog;
import cn.wwmxd.parser.ContentParser;
import cn.wwmxd.service.OperatelogService;
import cn.wwmxd.util.BaseContextHandler;
import cn.wwmxd.util.ModifyName;
import cn.wwmxd.util.ReflectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 拦截@EnableGameleyLog注解的方法
 * 将具体修改存储到数据库中
 * Created by wwmxd on 2018/03/02.
 */
@Aspect
@Component
public class ModifyAspect {

    private final static Logger logger = LoggerFactory.getLogger(ModifyAspect.class);

    private Operatelog operateLog=new Operatelog();

    private Object oldObject;

    private Object newObject;

    private Map<String,Object> feildValues;

    @Autowired
    private OperatelogService operatelogService;

    @Before("@annotation(EnableModifyLog)")
    public void doBefore(JoinPoint joinPoint, EnableModifyLog EnableModifyLog){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Object info=joinPoint.getArgs()[0];
        String[] feilds=EnableModifyLog.feildName();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        operateLog.setUsername(BaseContextHandler.getName());
        operateLog.setModifyip(ClientUtil.getClientIp(request));
        operateLog.setModifydate(sdf.format(new Date()));
        String handelName=EnableModifyLog.handleName();
        if("".equals(handelName)){
            operateLog.setModifyobject(request.getRequestURL().toString());
        }else {
            operateLog.setModifyobject(handelName);
        }
        operateLog.setModifyname(EnableModifyLog.name());
        operateLog.setModifycontent("");
        if(ModifyName.UPDATE.equals(EnableModifyLog.name())){
            for(String feild:feilds){
                feildValues=new HashMap<>();
                Object result= ReflectionUtils.getFieldValue(info,feild);
                feildValues.put(feild,result);
            }
            try {
                ContentParser contentParser= (ContentParser) EnableModifyLog.parseclass().newInstance();
                oldObject=contentParser.getResult(feildValues,EnableModifyLog);
            } catch (Exception e) {
                logger.error("service加载失败:",e);
            }
        }else{
            if(ModifyName.UPDATE.equals(EnableModifyLog.name())){
                logger.error("id查询失败，无法记录日志");
            }
        }

    }

    @AfterReturning(pointcut = "@annotation(enableModifyLog)",returning = "object")
    public void doAfterReturing(Object object, EnableModifyLog enableModifyLog){
        if(ModifyName.UPDATE.equals(enableModifyLog.name())){
            ContentParser contentParser= null;
            try {
                contentParser = (ContentParser) enableModifyLog.parseclass().newInstance();
                newObject=contentParser.getResult(feildValues,enableModifyLog);
            } catch (Exception e) {
                logger.error("service加载失败:",e);
            }

            try {
                List<Map<String ,Object>> changelist= ReflectionUtils.compareTwoClass(oldObject,newObject);
                StringBuilder str=new StringBuilder();
                for(Map<String,Object> map:changelist){
                    str.append("【"+map.get("name")+"】从【"+map.get("old")+"】改为了【"+map.get("new")+"】;\n");
                }
                operateLog.setModifycontent(str.toString());

            } catch (Exception e) {
                logger.error("比较异常",e);
            }
        }
        operatelogService.insert(operateLog);


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
