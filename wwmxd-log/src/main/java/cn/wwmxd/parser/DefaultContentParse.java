package cn.wwmxd.parser;

import cn.wwmxd.EnableModifyLog;
import cn.wwmxd.service.IService;
import cn.wwmxd.util.ReflectionUtils;
import cn.wwmxd.util.SpringUtil;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 基础解析类
 * 单表编辑时可以直接使用id来查询
 * 如果为多表复杂逻辑，请自行编写具体实现类
 * @author lw
 */
@Component
public class DefaultContentParse implements ContentParser {
    //默认的缓存
    private ConcurrentHashMap<String,Object> cache;

    @PostConstruct
    public void init(){
        this.cache=new ConcurrentHashMap<>();
    }

    @Override
    public Object getResult(JoinPoint joinPoint, EnableModifyLog enableModifyLog) {
        Object info = joinPoint.getArgs()[0];
        Object id = ReflectionUtils.getFieldValue(info, "id");
        Assert.notNull(id,"未解析到id值，请检查前台传递参数是否正确");
        Class idType=enableModifyLog.idType();
        if(idType.isInstance(id)){
            //如果开启缓存,则不再进行查库
            String key=enableModifyLog.handleName()+id;
            if(cache.containsKey(key)&&enableModifyLog.defaultCache()){
                return cache.get(key);
            }
            Class cls=enableModifyLog.serviceclass();
            IService service = (IService) SpringUtil.getBean(cls);
            Object result=service.selectById(idType.cast(id));
            if(enableModifyLog.defaultCache()){
                cache.put(key,result);
            }
            return  result;
        }else {
            throw new RuntimeException("请核实id type");
        }
    }

    /**
     * 更新缓存里的数据
     * @param joinPoint 切入点
     * @param enableModifyLog 注解
     * @param result 新的结果
     */
    public void updateCache(JoinPoint joinPoint, EnableModifyLog enableModifyLog,Object result){
        Object info = joinPoint.getArgs()[0];
        Object id = ReflectionUtils.getFieldValue(info, "id");
        Assert.notNull(id,"未解析到id值，请检查前台传递参数是否正确");
        Class idType=enableModifyLog.idType();
        if(idType.isInstance(id)) {
            //如果开启缓存,则不再进行查库
            String key = enableModifyLog.handleName() + id;
            cache.put(key,result);
        }
    }

}
