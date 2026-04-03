package org.example.orderfoodafter.common;

/**
 * 查询条件实体类
 * 封装数据库查询条件，支持多种查询操作符（等于、大于、小于、模糊查询等）
 * 
 * @author 李吉隆
 * @date 2025-11-25
 */
public class WhereEntity {
    private String column;
    private String type;
    private String value;
/**
 * getColumn方法
 *
 * @author 李吉隆
 */

    public String getColumn() {
        return column;
    }
/**
 * setColumn方法
 *
 * @author 李吉隆
 */

    public void setColumn(String conlumn) {
        this.column = conlumn;
    }
/**
 * getType方法
 *
 * @author 李吉隆
 */

    public String getType() {
        return type;
    }
/**
 * setType方法
 *
 * @author 李吉隆
 */

    public void setType(String type) {
        this.type = type;
    }
/**
 * getValue方法
 *
 * @author 李吉隆
 */

    public String getValue() {
        return value;
    }
/**
 * setValue方法
 *
 * @author 李吉隆
 */

    public void setValue(String value) {
        this.value = value;
    }
}
