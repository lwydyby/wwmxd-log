package cn.wwmxd;

import cn.wwmxd.parser.DefaultContentParse;
import cn.wwmxd.service.IService;
import cn.wwmxd.util.ModifyName;

import java.lang.annotation.*;

/**
 * 记录编辑详细信息的标注
 * @author lw
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface EnableModifyLog {
    /**
     * @return 操作的中文说明 可以直接调用ModifyName
     */
    ModifyName modifyType() default ModifyName.UPDATE;

    /**
     * @return 获取编辑信息的解析类，目前为使用id获取，复杂的解析需要自己实现，默认不填写
     *       则使用默认解析类
     */
    Class parseclass() default DefaultContentParse.class;

    /**
     * @return 查询数据库所调用的class文件
     */
    Class serviceclass() default IService.class;

    /**
     * @return 前台字段名称
     */
    String[] feildName() default {"id"};
    /**
     * @return 具体业务操作名称
     */
    String handleName() default "";

    /**
     * @return 是否需要默认的改动比较
     */
    boolean needDefaultCompare() default false;

    Class idType() default String.class;
}
