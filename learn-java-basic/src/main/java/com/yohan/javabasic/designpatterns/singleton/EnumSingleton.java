package com.yohan.javabasic.designpatterns.singleton;

/**
 * 枚举实现单例模式.
 *
 * @author yinhou.liu
 * @Date 2024/01/05
 */
public class EnumSingleton {
    /**
     * 私有化构造函数.
     */
    private EnumSingleton() {
    }

    /**
     * 对外方法,获取 EnumSingleton 类.
     *
     * @return {@link EnumSingleton}
     */
    public static EnumSingleton getInstance() {
        return Singleton.INSTANCE.getInstance();
    }


    /**
     * 静态枚举类实现单例.
     */
    static enum Singleton {
        /**
         * 枚举对象,天生为单例.
         */
        INSTANCE;

        private EnumSingleton enumSingleton;

        /**
         * 私有化枚举的构造函数.
         */
        private Singleton() {
            enumSingleton = new EnumSingleton();
        }

        public EnumSingleton getInstance() {
            return enumSingleton;
        }
    }

}



