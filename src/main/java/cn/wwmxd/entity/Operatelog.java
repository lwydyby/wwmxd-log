package cn.wwmxd.entity;


import cn.wwmxd.DataName;
import lombok.Data;
//import lombok.Data;


/**
 * @author WWMXD
 */
@Data
public class Operatelog{
    private static final long serialVersionUID = 1L;
    //
    @DataName(name = "")
    private Integer id;


    @DataName(name = "操作人")
    private String username;

    //操作日期
    @DataName(name = "操作日期")
    private String modifydate;

    //操作名词
    @DataName(name = "操作名词")
    private String modifyname;

    //操作对象
    @DataName(name = "操作对象")
    private String modifyobject;

    //操作内容

    @DataName(name = "操作内容")
    private String modifycontent;

    //ip

    @DataName(name = "IP")
    private String modifyip;



}