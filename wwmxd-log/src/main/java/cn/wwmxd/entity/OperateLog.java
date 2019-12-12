package cn.wwmxd.entity;


import cn.wwmxd.DataName;
import cn.wwmxd.util.ModifyName;
import lombok.Data;
import lombok.ToString;


/**
 * @author WWMXD
 */
@Data
@ToString
public class OperateLog {
    private static final long serialVersionUID = 1L;
    //
    @DataName(name = "")
    private Integer id;


    @DataName(name = "操作人")
    private String username;

    //操作日期
    @DataName(name = "操作日期")
    private String modifyDate;

    //操作名词
    @DataName(name = "操作名词")
    private ModifyName modifyName;

    //操作对象
    @DataName(name = "操作对象")
    private String modifyObject;

    //操作内容

    @DataName(name = "操作内容")
    private String modifyContent;

    //ip

    @DataName(name = "IP")
    private String modifyIp;

    private Object oldObject;

    private Object newObject;



}