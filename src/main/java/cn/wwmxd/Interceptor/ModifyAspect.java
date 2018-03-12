package cn.wwmxd.Interceptor;


import cn.wwmxd.EnableGameleyLog;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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
@ConditionalOnBean(OperatelogService.class)
public class ModifyAspect {

    private final static Logger logger = LoggerFactory.getLogger(ModifyAspect.class);

    private Operatelog operateLog=new Operatelog();

    private Object oldObject;

    private Object newObject;

    private Map<String,Object> feildValues;

    @Autowired
    private OperatelogService operatelogService;

    @Before("@annotation(enableGameleyLog)")
    public void doBefore(JoinPoint joinPoint, EnableGameleyLog enableGameleyLog){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Object info=joinPoint.getArgs()[0];
        String[] feilds=enableGameleyLog.feildName();
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        operateLog.setUsername(BaseContextHandler.getName());
        operateLog.setModifyip(ClientUtil.getClientIp(request));
        operateLog.setModifydate(sdf.format(new Date()));
        String handelName=enableGameleyLog.handleName();
        if("".equals(handelName)){
            operateLog.setModifyobject(request.getRequestURL().toString());
        }else {
            operateLog.setModifyobject(handelName);
        }
        operateLog.setModifyname(enableGameleyLog.name());
        operateLog.setModifycontent("");
        if(ModifyName.UPDATE.equals(enableGameleyLog.name())){
            for(String feild:feilds){
                feildValues=new HashMap<>();
                Object result= ReflectionUtils.getFieldValue(info,feild);
                feildValues.put(feild,result);
            }
            try {
                ContentParser contentParser= (ContentParser) enableGameleyLog.parseclass().newInstance();
                oldObject=contentParser.getResult(feildValues,enableGameleyLog);
            } catch (Exception e) {
                logger.error("service加载失败:",e);
            }
        }else{
            if(ModifyName.UPDATE.equals(enableGameleyLog.name())){
                logger.error("id查询失败，无法记录日志");
            }
        }

    }

    @AfterReturning(pointcut = "@annotation(enableGameleyLog)",returning = "object")
    public void doAfterReturing(Object object, EnableGameleyLog enableGameleyLog){
        if(ModifyName.UPDATE.equals(enableGameleyLog.name())){
            ContentParser contentParser= null;
            try {
                contentParser = (ContentParser) enableGameleyLog.parseclass().newInstance();
                newObject=contentParser.getResult(feildValues,enableGameleyLog);
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
