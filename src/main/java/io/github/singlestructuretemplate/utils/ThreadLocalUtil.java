package io.github.singlestructuretemplate.utils;


// ThreadLocal 工具类
// 核心作用：解决同一个请求链路中，不同组件（如拦截器、Controller、Service）之间的数据共享问题（只能存一个对象，调用第二次 set 时，会把第一次存的对象覆盖掉）
public class ThreadLocalUtil {
    // 私有化构造方法，防止外部实例化
    private ThreadLocalUtil() {
    }

    //提供ThreadLocal对象,
    private static final ThreadLocal THREAD_LOCAL = new ThreadLocal();

    //根据键获取值
    public static <T> T get(){
        return (T) THREAD_LOCAL.get();
    }
	
    //存储键值对
    public static void set(Object value){
        THREAD_LOCAL.set(value);
    }


    //清除ThreadLocal 防止内存泄漏
    public static void remove(){
        THREAD_LOCAL.remove();
    }
}
