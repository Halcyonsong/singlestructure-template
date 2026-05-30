package io.github.singlestructuretemplate.utils;

import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ConvertUtils {

    public static <T, R> R convert(T source, Class<R> targetClass) {
        if (source == null) {
            return null;
        }
        try {
            R target = targetClass.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (Exception e) {
            throw new RuntimeException("对象转换失败: " + e.getMessage(), e);
        }
    }

    public static <T, R> List<R> convertList(List<T> sourceList, Class<R> targetClass) {
        if (sourceList == null || sourceList.isEmpty()) {
            return Collections.emptyList();
        }
        return sourceList.stream()
                .map(source -> convert(source, targetClass)) // 复用单体转换逻辑
                .collect(Collectors.toList());
    }
}