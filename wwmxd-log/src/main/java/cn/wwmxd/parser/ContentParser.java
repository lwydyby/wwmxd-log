package cn.wwmxd.parser;

import cn.wwmxd.EnableModifyLog;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * 解析接口
 *
 *
 * @author lw
 */

public interface ContentParser {

    final static Logger logger = LoggerFactory.getLogger(ContentParser.class);

    /**
     * 获取信息返回查询出的对象
     * @param joinPoint 查询条件的参数
     * @param enableModifyLog 注解
     * @return 获得的结果
     */
    public Object getResult(JoinPoint joinPoint, EnableModifyLog enableModifyLog);
}

