package cn.wwmxd.parser;

import cn.wwmxd.EnableModifyLog;
import cn.wwmxd.service.IService;
import cn.wwmxd.util.ReflectionUtils;
import cn.wwmxd.util.SpringUtil;
import org.aspectj.lang.JoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 基础解析类
 * 单表编辑时可以直接使用id来查询
 * 如果为多表复杂逻辑，请自行编写具体实现类
 * @author lw
 */
@Component
public class DefaultContentParse implements ContentParser {

    @Override
    public Object getOldResult(JoinPoint joinPoint, EnableModifyLog enableModifyLog) {
        Object info = joinPoint.getArgs()[0];
        Object id = ReflectionUtils.getFieldValue(info, "id");
        Assert.notNull(id,"未解析到id值，请检查前台传递参数是否正确");
        Class idType=enableModifyLog.idType();
        if(idType.isInstance(id)){
            Class cls=enableModifyLog.serviceClass();
            IService service = (IService) SpringUtil.getBean(cls);
            Object result=service.selectById(idType.cast(id));
            return  result;
        }else {
            throw new RuntimeException("请核实id type");
        }
    }

    @Override
    public Object getNewResult(JoinPoint joinPoint, EnableModifyLog enableModifyLog) {
        return getOldResult(joinPoint,enableModifyLog);
    }


}
