package org.example.orderfoodafter.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * 通用工具类
 * 提供常用的工具方法，包括查询条件构建等功能
 * 
 * @author 李吉隆
 * @date 2025-11-16
 */
@Component
    public class CommontUtil {
/**
 * getWhere方法
 *
 * @author 李吉隆
 */
    public QueryWrapper getWhere(List where) throws IOException {
        QueryWrapper<Object> objectQueryWrapper = new QueryWrapper<>();//条件查询对象
            /**
     * ObjectMapper
     * 
     * @author 李吉隆
     */
        ObjectMapper objectMapper = new ObjectMapper();
        List<WhereEntity> whereEntityList = objectMapper.readValue(objectMapper.writeValueAsString(where), new TypeReference<List<WhereEntity>>() {
        });
        if (whereEntityList != null) {
            System.out.println(whereEntityList);
            for (WhereEntity whereEntity : whereEntityList) {
                System.out.println(whereEntity.getColumn() + "==" + whereEntity.getType() + "==" + whereEntity.getValue());
                if (whereEntity.getType().equals("like")) {
                    objectQueryWrapper.like(StringUtils.hasLength(whereEntity.getValue()), whereEntity.getColumn(), whereEntity.getValue());
                } else if (whereEntity.getType().equals("eq")) {
                    objectQueryWrapper.eq(StringUtils.hasLength(whereEntity.getValue()), whereEntity.getColumn(), whereEntity.getValue());
                } else if (whereEntity.getType().equals("ge")) {
                    objectQueryWrapper.ge(StringUtils.hasLength(whereEntity.getValue()), whereEntity.getColumn(), whereEntity.getValue());
                } else if (whereEntity.getType().equals("le")) {
                    objectQueryWrapper.le(StringUtils.hasLength(whereEntity.getValue()), whereEntity.getColumn(), whereEntity.getValue());
                } else if (whereEntity.getType().equals("notEqual")) {
                    objectQueryWrapper.ne(whereEntity.getValue() != null, whereEntity.getColumn(), whereEntity.getValue());
                } else if (whereEntity.getType().equals("gt")) {
                    objectQueryWrapper.gt(whereEntity.getValue() != null, whereEntity.getColumn(), whereEntity.getValue());
                } else if (whereEntity.getType().equals("ne")) {
                    objectQueryWrapper.ne(whereEntity.getValue() != null, whereEntity.getColumn(), whereEntity.getValue());
                } else if (whereEntity.getType().equals("in")) {
                    objectQueryWrapper.in(whereEntity.getValue() != null, whereEntity.getColumn(), whereEntity.getValue());
                }
            }
        }
        return objectQueryWrapper;
    }
}