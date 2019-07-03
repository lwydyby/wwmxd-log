package cn.wwmxd;

import com.baomidou.mybatisplus.service.IService;
import cn.wwmxd.parser.DefaultContentParse;

import java.lang.annotation.*;

/**
 * 记录编辑详细信息的标注
 * @author lw
 * @date 2018-03-02
 */
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
    /**
     * 具体业务操作名称
     */
    String handleName() default "";
}
