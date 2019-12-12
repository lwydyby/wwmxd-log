package cn.wwmxd.parser;

import cn.wwmxd.EnableModifyLog;
import cn.wwmxd.service.IService;
import cn.wwmxd.util.SpringUtil;
import org.springframework.util.Assert;

import java.util.Map;

/**
 * 基础解析类
 * 单表编辑时可以直接使用id来查询
 * 如果为多表复杂逻辑，请自行编写具体实现类
 * @author lw
 */
public class DefaultContentParse implements ContentParser {
    @Override
    public Object getResult(Map<String,Object> fieldValues, EnableModifyLog enableModifyLog) {
        Assert.isTrue(fieldValues.containsKey("id"),"未解析到id值，请检查前台传递参数是否正确");
        Object result= fieldValues.get("id");
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
