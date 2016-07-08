package com.neopi.demo;

import java.io.Serializable;

/**
 * Created by Neo on 2016/7/4.
 *
 * 静态的成员内部类的方式實現單例模式
 */
public final class ImageLoadUtils implements Serializable{

  /**
   * 静态的成员内部类持有单例对象,避免了饿汉模式下的即使不使用也初始化但里对象,也避免了懒汉模式下线程安全的问题
   *
   * 该内部类的实例与外部类的实例没有绑定关系，
   * 而且只有被调用到才会装载，从而实现了延迟加载
   *
   */
  private static class ImageLoaderHolder{
    /**
     * 静态初始化器，由JVM来保证线程安全
     */
    public static final ImageLoadUtils INSTANCE = new ImageLoadUtils();
  }

  /**
   * 私有化构造
   */
  private ImageLoadUtils(){

  }

  /**
   * 公布出获取单例的方法
   *
   * 如果在使用单例模式的时候需要传入context,
   * 请使用getApplicationContext(),在静态成员内部类中为构造传入上下文
   * @return
   */
  public ImageLoadUtils getInstance(){
    return  ImageLoaderHolder.INSTANCE;
  }




}
