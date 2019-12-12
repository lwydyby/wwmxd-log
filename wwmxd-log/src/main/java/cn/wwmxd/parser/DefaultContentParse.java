package cn.wwmxd.parser;

import cn.wwmxd.EnableModifyLog;
import cn.wwmxd.service.IService;
import cn.wwmxd.util.ReflectionUtils;
import cn.wwmxd.util.SpringUtil;
import org.aspectj.lang.JoinPoint;
import org.springframework.util.Assert;

/**
 * 基础解析类
 * 单表编辑时可以直接使用id来查询
 * 如果为多表复杂逻辑，请自行编写具体实现类
 * @author lw
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
