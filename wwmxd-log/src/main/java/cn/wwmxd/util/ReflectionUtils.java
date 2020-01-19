package cn.wwmxd.util;

import cn.wwmxd.DataName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 反射工具类.
 * 提供调用getter/setter方法, 访问私有变量, 调用私有方法, 获取泛型类型Class, 被AOP过的真实类等工具函数.
 * @author calvin
 * @version 2013-01-15
 */
@SuppressWarnings("rawtypes")
public class ReflectionUtils {

	private static final String SETTER_PREFIX = "set";

	private static final String GETTER_PREFIX = "get";

	private static final String CGLIB_CLASS_SEPARATOR = "$$";

	private static Logger logger = LoggerFactory.getLogger(ReflectionUtils.class);


	/**
	 * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
	 * @param obj 读取的对象
	 * @param fieldName 读取的列
	 * @return 属性值
	 */
	public static Object getFieldValue(final Object obj, final String fieldName) {
		Field field = getAccessibleField(obj, fieldName);

		if (field == null) {
			throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
		}

		Object result = null;
		try {
			result = field.get(obj);
		} catch (IllegalAccessException e) {
			logger.error("不可能抛出的异常{}", e.getMessage());
		}
		return result;
	}


	/**
	 *  循环向上转型, 获取对象的DeclaredField, 并强制设置为可访问.
	 *   如向上转型到Object仍无法找到, 返回null.
	 * @param obj  查找的对象
	 * @param fieldName  列名
	 * @return 列
	 */
	public static Field getAccessibleField(final Object obj, final String fieldName) {
		for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
			try {
				Field field = superClass.getDeclaredField(fieldName);
				makeAccessible(field);
				return field;
			} catch (NoSuchFieldException e) {//NOSONAR
				// Field不在当前类定义,继续向上转型
				continue;// new add
			}
		}
		return null;
	}

	/**
	 * 改变private/protected的成员变量为public，尽量不调用实际改动的语句，避免JDK的SecurityManager抱怨。
	 * @param field  列
	 */

	public static void makeAccessible(Field field) {
		if ((!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(field.getDeclaringClass().getModifiers()) || Modifier
				.isFinal(field.getModifiers())) && !field.isAccessible()) {
			field.setAccessible(true);
		}
	}





	/**
	 * 获取两个对象同名属性内容不相同的列表
	 * @param class1 old对象
	 * @param class2 new对象
	 * @return  区别列表
	 * @throws ClassNotFoundException 异常
	 * @throws IllegalAccessException 异常
	 */
	public static List<Map<String ,Object>> compareTwoClass(Object class1, Object class2) throws ClassNotFoundException, IllegalAccessException {
		List<Map<String,Object>> list=new ArrayList<Map<String, Object>>();
		//获取对象的class
		Class<?> clazz1 = class1.getClass();
		Class<?> clazz2 = class2.getClass();
		//获取对象的属性列表
		Field[] field1 = clazz1.getDeclaredFields();
		Field[] field2 = clazz2.getDeclaredFields();
		StringBuilder sb=new StringBuilder();
		//遍历属性列表field1
		for(int i=0;i<field1.length;i++) {
			//遍历属性列表field2
			for (int j = 0; j < field2.length; j++) {
				//如果field1[i]属性名与field2[j]属性名内容相同
				if (field1[i].getName().equals(field2[j].getName())) {
					if (field1[i].getName().equals(field2[j].getName())) {
						field1[i].setAccessible(true);
						field2[j].setAccessible(true);
						//如果field1[i]属性值与field2[j]属性值内容不相同
						if (!compareTwo(field1[i].get(class1), field2[j].get(class2))) {
							Map<String, Object> map2 = new HashMap<String, Object>();
							DataName name=field1[i].getAnnotation(DataName.class);
							String fieldName="";
							if(name!=null){
								fieldName=name.name();
							}else {
								fieldName=field1[i].getName();
							}
							map2.put("name", fieldName);
							map2.put("old", field1[i].get(class1));
							map2.put("new", field2[j].get(class2));
							list.add(map2);
						}
						break;
					}
				}
			}
		}
		return list;

	}
	/**
	 * 对比两个数据是否内容相同
	 *
	 * @param  object1  比较对象1
	 * @param  object2  比较对象2
	 * @return boolean类型
	 */
	public static boolean compareTwo(Object object1,Object object2){

		if(object1==null&&object2==null){
			return true;
		}
		if(object1==null&&object2!=null){
			return false;
		}
		if(object1.equals(object2)){
			return true;
		}
		return false;
	}
}

