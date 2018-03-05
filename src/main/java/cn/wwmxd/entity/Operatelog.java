package cn.wwmxd.entity;


import cn.wwmxd.DataName;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;


import java.io.Serializable;
//import lombok.Data;


/**
 * 
 *
 * @author WWMXD
 * @email 309980030@qq.com
 * @date 2018-03-02 16:25:03
 */
//@Data
@TableName("operatelog")
public class Operatelog extends Model<Operatelog> {
	private static final long serialVersionUID=1L;
		    //
    @TableId(value="id", type= IdType.AUTO)
		@DataName(name="")
	    private Integer id;
	
		    //操作人
    @TableField("username")
		@DataName(name="操作人")
	    private String username;
	
		    //操作日期
    @TableField("modifydate")
		@DataName(name="操作日期")
	    private String modifydate;
	
		    //操作名词
    @TableField("modifyname")
		@DataName(name="操作名词")
	    private String modifyname;
	
		    //操作对象
    @TableField("modifyobject")
		@DataName(name="操作对象")
	    private String modifyobject;
	
		    //操作内容
    @TableField("modifycontent")
		@DataName(name="操作内容")
	    private String modifycontent;

	//ip
	@TableField("modifyip")
	@DataName(name="IP")
	private String modifyip;

	public String getModifyip() {
		return modifyip;
	}

	public void setModifyip(String modifyip) {
		this.modifyip = modifyip;
	}

	/**
	 * 设置：
	 */
	public void setId(Integer id) {
			this.id = id;
			}
	/**
	 * 获取：
	 */
	public Integer getId() {
			return id;
			}
		/**
	 * 设置：操作人
	 */
	public void setUsername(String username) {
			this.username = username;
			}
	/**
	 * 获取：操作人
	 */
	public String getUsername() {
			return username;
			}
		/**
	 * 设置：操作日期
	 */
	public void setModifydate(String modifydate) {
			this.modifydate = modifydate;
			}
	/**
	 * 获取：操作日期
	 */
	public String getModifydate() {
			return modifydate;
			}
		/**
	 * 设置：操作名词
	 */
	public void setModifyname(String modifyname) {
			this.modifyname = modifyname;
			}
	/**
	 * 获取：操作名词
	 */
	public String getModifyname() {
			return modifyname;
			}
		/**
	 * 设置：操作对象
	 */
	public void setModifyobject(String modifyobject) {
			this.modifyobject = modifyobject;
			}
	/**
	 * 获取：操作对象
	 */
	public String getModifyobject() {
			return modifyobject;
			}
		/**
	 * 设置：操作内容
	 */
	public void setModifycontent(String modifycontent) {
			this.modifycontent = modifycontent;
			}
	/**
	 * 获取：操作内容
	 */
	public String getModifycontent() {
			return modifycontent;
			}
		@Override
	protected Serializable pkVal() {
        return this.id;
	}
	@Override
	public String toString() {
	    return "Operatelog{"+
	"Id="+id+
	"Username="+username+
	"Modifydate="+modifydate+
	"Modifyname="+modifyname+
	"Modifyobject="+modifyobject+
	"Modifycontent="+modifycontent+
				"IP"+modifyip+
		"}";}
}