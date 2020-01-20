package cn.wwmxd.util;

/**
 * @author liwei
 */
public class ObjectUtil {
    /**
     * 判断两个对象是否为同一对象
     * instanceof
     * isInstance
     * isAssignableFrom
     *
     * 注意：任何一个元素为 null，则认为是不同类型。
     * @param one 第一个元素
     * @param two 第二个元素
     * @return 是否为同一对象
     */
    public static boolean isSameType(Object one, Object two) {
        if(ObjectUtil.isNull(one)
                || ObjectUtil.isNull(two)) {
            return false;
        }
        Class clazzOne = one.getClass();

        return clazzOne.isInstance(two);
    }

    /**
     * 不是同一个类型
     *
     * @param one 第一个元素
     * @param two 第二个元素
     * @return 是否为不同对象
     */
    public static boolean isNotSameType(Object one, Object two) {
        return !isSameType(one, two);
    }


    /**
     * 判断当前对象是否为空
     * - 对象为空
     * - 空字符串
     * - 空集合/map
     * - 空数组
     * - 自定义空类型
     *
     * @param object 对象
     * @return 是否为空
     */
    public static boolean isNull(Object object) {
        return null == object;
    }

    /**
     * 判断对象是否非null
     *
     * @param object 元素
     * @return {@code true} 非空
     */
    public static boolean isNotNull(Object object) {
        return !isNull(object);
    }






}
