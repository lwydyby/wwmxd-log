package cn.wwmxd.Interceptor;


import cn.wwmxd.DataName;
import cn.wwmxd.EnableModifyLog;
import cn.wwmxd.entity.OperateLog;
import cn.wwmxd.parser.ContentParser;
import cn.wwmxd.parser.DefaultContentParse;
import cn.wwmxd.service.OperatelogService;
import cn.wwmxd.util.*;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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


    @Autowired
    private OperatelogService operatelogService;
    @Autowired
    private DefaultContentParse defaultContentParse;


    @Around("@annotation(enableModifyLog)")
    public void around(ProceedingJoinPoint joinPoint,EnableModifyLog enableModifyLog) throws  Throwable{
        Map<String, Object> oldMap=new HashMap<>();
        OperateLog operateLog = new OperateLog();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        //当不传默认modifyType时 根据Method类型自动匹配
        setAnnotationType(request,enableModifyLog);

        // fixme 1.0.9开始不再提供自动存入username功能,请在存储实现类中自行存储
        operateLog.setModifyIp(ClientUtil.getClientIp(request));
        operateLog.setModifyDate(new Date());
        //如果不传,则默认存入请求的URI
        String handelName = enableModifyLog.handleName();
        if ("".equals(handelName)) {
            operateLog.setModifyObject(request.getRequestURL().toString());
        } else {
            operateLog.setModifyObject(handelName);
        }
        operateLog.setModifyName(enableModifyLog.modifyType());
        operateLog.setModifyContent("");
        if (ModifyName.UPDATE.equals(enableModifyLog.modifyType())) {
            try {
                ContentParser contentParser = (ContentParser) SpringUtil.getBean(enableModifyLog.parseclass());
                Object oldObject = contentParser.getOldResult(joinPoint, enableModifyLog);
                operateLog.setOldObject(oldObject);
                if (enableModifyLog.needDefaultCompare()) {
                    oldMap = (Map<String, Object>) objectToMap(oldObject);
                }
            } catch (Exception e) {
                logger.error("service加载失败:", e);
            }
        }
        //执行service TODO 是否需要Catch Exception
        Object object=joinPoint.proceed();
        if (ModifyName.UPDATE.equals(enableModifyLog.modifyType())) {
            ContentParser contentParser;
            try {
                contentParser = (ContentParser) SpringUtil.getBean(enableModifyLog.parseclass());
                object = contentParser.getNewResult(joinPoint, enableModifyLog);
                operateLog.setNewObject(object);
            } catch (Exception e) {
                logger.error("service加载失败:", e);
            }
            //默认不进行比较，可以自己在logService中自定义实现，降低对性能的影响
            if (enableModifyLog.needDefaultCompare()) {
               operateLog.setModifyContent(defaultDealUpdate(object,oldMap));
            }
        }else{
            //除了更新外,默认把返回的对象存储到log中
            operateLog.setNewObject(object);
        }

        operatelogService.insert(operateLog);
    }

    private String defaultDealUpdate(Object newObject,Map<String, Object> oldMap){
        try {
            Map<String, Object> newMap = (Map<String, Object>) objectToMap(newObject);
            StringBuilder str = new StringBuilder();
            Object finalNewObject = newObject;
            oldMap.forEach((k, v) -> {
                Object newResult = newMap.get(k);
                if (v != null && !v.equals(newResult)) {
                    Field field = ReflectionUtils.getAccessibleField(finalNewObject, k);
                    DataName dataName = field.getAnnotation(DataName.class);
                    if (dataName != null) {
                        str.append("【").append(dataName.name()).append("】从【")
                                .append(v).append("】改为了【").append(newResult).append("】;\n");
                    } else {
                        str.append("【").append(field.getName()).append("】从【")
                                .append(v).append("】改为了【").append(newResult).append("】;\n");
                    }
                }

            });
            return str.toString();

        } catch (Exception e) {
            logger.error("比较异常", e);
            throw new RuntimeException("比较异常",e);
        }
    }

    private Map<?, ?> objectToMap(Object obj) {
        if (obj == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //如果使用JPA请自己打开这条配置
        //mapper.addMixIn(Object.class, IgnoreHibernatePropertiesInJackson.class);
        Map<?, ?> mappedObject = mapper.convertValue(obj, Map.class);

        return mappedObject;
    }

    private void setAnnotationType(HttpServletRequest request,EnableModifyLog modifyLog){
        if(!modifyLog.modifyType().equals(ModifyName.NONE)){
            return;
        }
        String method=request.getMethod();
        if(RequestMethod.GET.name().equalsIgnoreCase(method)){
            ReflectAnnotationUtil.updateValue(modifyLog,"modifyType",ModifyName.GET);
        }else if(RequestMethod.POST.name().equalsIgnoreCase(method)){
            ReflectAnnotationUtil.updateValue(modifyLog,"modifyType",ModifyName.SAVE);
        }else if(RequestMethod.PUT.name().equalsIgnoreCase(method)){
            ReflectAnnotationUtil.updateValue(modifyLog,"modifyType",ModifyName.UPDATE);
        }else if(RequestMethod.DELETE.name().equalsIgnoreCase(method)){
            ReflectAnnotationUtil.updateValue(modifyLog,"modifyType",ModifyName.DELETE);
        }

    }

}
