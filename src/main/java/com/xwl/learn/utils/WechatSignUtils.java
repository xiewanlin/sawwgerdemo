package com.xwl.learn.utils;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Charsets;
import java.beans.PropertyDescriptor;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

/**
 * @Author: xiewanlin
 * @Date: 2020/5/19
 */
public class WechatSignUtils {

  /**
   * 请求对象的属性名与Key关系
   */
  private Map<Class,Map<String,String>> keyMap = new ConcurrentHashMap<>();

  public String getSign(Object req){
    String src = Arrays.stream(BeanUtils.getPropertyDescriptors(req.getClass()))
        .map(desc -> new Entry<String, Object>(getKey(desc,req),getValue(desc,req)) {})
        .filter(entry -> Objects.nonNull(entry.getValue())
            &&StringUtils.isNotBlank(entry.getValue().toString())
            &&!StringUtils.equals("class",entry.getKey()))
        .sorted(Comparator.comparing(Entry::getKey))
        .map(entry ->MessageFormat.format("{0}={1}",entry.getKey(),entry.getValue()))
        .peek(System.out::println)
        .collect(Collectors.joining("&"))
        .concat(MessageFormat.format("&key={0}","secret"));
    return DigestUtils.md5Hex(src.getBytes(Charsets.UTF_8)).toUpperCase();
  }

  /**
   * 提取键值
   * @param desc
   * @param req
   * @return
   */
  private String getKey(PropertyDescriptor desc,Object req){
    return getKeyMap(req).getOrDefault(desc.getDisplayName(),desc.getDisplayName());
  }

  private Map<String, String> getKeyMap(Object req){
    if(!keyMap.containsKey(req.getClass())){
      Map<String,String> beanKeys = new ConcurrentHashMap<>();
      Class tempClass = req.getClass();
      do{
        Arrays.stream(tempClass.getDeclaredFields())
            .collect(Collectors.toMap(field -> field.getName(),
                field -> field.getAnnotation(JacksonXmlProperty.class).localName(),
                (x,y)->x,()->beanKeys));
      }while((tempClass = tempClass.getSuperclass())!=Object.class);
      keyMap.putIfAbsent(req.getClass(),beanKeys);
      return beanKeys;
    }else{
      return keyMap.get(req.getClass());
    }
  }

  private Object getValue(PropertyDescriptor desc,Object obj){
    try {
      return desc.getReadMethod().invoke(obj);
    } catch (Exception e){
    }
    return null;
  }

  static class Entry<K,V>{
    K key;
    V value;

    public Entry(K key, V value) {
      this.key = key;
      this.value = value;
    }

    public K getKey() {
      return key;
    }

    public V getValue() {
      return value;
    }
  }
}
