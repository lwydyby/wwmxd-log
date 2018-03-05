package cn.wwmxd.parser;

import cn.wwmxd.EnableGameleyLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;


/**
 * 解析接口
 *
 *
 * @author lw
 * @date 2018-03-02
 */

public interface ContentParser {

    final static Logger logger = LoggerFactory.getLogger(ContentParser.class);

    /**
     * 获取信息返回查询出的对象
     * @param feildValues 查询条件的参数值
     * @param enableGameleyLog 注解
     * @return
     */
    public Object getResult(Map<String, Object> feildValues, EnableGameleyLog enableGameleyLog);
}

