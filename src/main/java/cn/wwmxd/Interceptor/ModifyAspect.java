package cn.wwmxd.Interceptor;


import cn.wwmxd.DataName;
import cn.wwmxd.EnableModifyLog;
import cn.wwmxd.entity.OperateLog;
import cn.wwmxd.parser.ContentParser;
import cn.wwmxd.service.OperatelogService;
import cn.wwmxd.util.BaseContextHandler;
import cn.wwmxd.util.ClientUtil;
import cn.wwmxd.util.ModifyName;
import cn.wwmxd.util.ReflectionUtils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
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
import java.lang.reflect.Field;
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

    private OperateLog operateLog=new OperateLog();

    private Object oldObject;

    private Object newObject;

    private Map<String,Object> fieldValues;

    private Map<String ,Object> oldMap;

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
        operateLog.setModifyIp(ClientUtil.getClientIp(request));
        operateLog.setModifyDate(sdf.format(new Date()));
        String handelName=EnableModifyLog.handleName();
        if("".equals(handelName)){
            operateLog.setModifyObject(request.getRequestURL().toString());
        }else {
            operateLog.setModifyObject(handelName);
        }
        operateLog.setModifyName(EnableModifyLog.name());
        operateLog.setModifyContent("");
        if(ModifyName.UPDATE.equals(EnableModifyLog.name())){
            for(String feild:feilds){
                fieldValues=new HashMap<>();
                Object result= ReflectionUtils.getFieldValue(info,feild);
                fieldValues.put(feild,result);
            }
            try {
                ContentParser contentParser= (ContentParser) EnableModifyLog.parseclass().newInstance();
                oldObject=contentParser.getResult(fieldValues,EnableModifyLog);
                oldMap= (Map<String, Object>) objectToMap(oldObject);
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
                newObject=contentParser.getResult(fieldValues,enableModifyLog);
            } catch (Exception e) {
                logger.error("service加载失败:",e);
            }

            try {
                Map<String ,Object> newMap= (Map<String, Object>) objectToMap(newObject);
                StringBuilder str=new StringBuilder();
                oldMap.forEach((k,v)->{
                    Object newResult=newMap.get(k);
                    if(v!=null&&!v.equals(newResult)){
                        Field field=ReflectionUtils.getAccessibleField(newObject,k);
                        DataName dataName=field.getAnnotation(DataName.class);
                        if(dataName!=null){
                            str.append("【").append(dataName.name()).append("】从【")
                                    .append(v).append("】改为了【").append(newResult).append("】;\n");
                        }else {
                            str.append("【").append(field.getName()).append("】从【")
                                    .append(v).append("】改为了【").append(newResult).append("】;\n");
                        }
                    }

                });
                operateLog.setModifyContent(str.toString());

            } catch (Exception e) {
                logger.error("比较异常",e);
            }
        }
        operatelogService.insert(operateLog);


    }


    private  Map<?, ?> objectToMap (Object obj) {
        if (obj == null) {
            return null;
        }
        ObjectMapper mapper=new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //如果使用JPA请自己打开这条配置
        //mapper.addMixIn(Object.class, IgnoreHibernatePropertiesInJackson.class);
        Map<?, ?> mappedObject = mapper.convertValue(obj, Map.class);

        return mappedObject;
    }

}
