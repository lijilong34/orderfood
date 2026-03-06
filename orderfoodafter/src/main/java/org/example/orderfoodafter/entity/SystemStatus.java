package org.example.orderfoodafter.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统状态实体类
 * 存储系统监控数据
 */
@Data
public class SystemStatus {
    /**
     * 系统运行时间（秒）
     */
    private long uptime;
    
    /**
     * CPU使用率（%）
     */
    private double cpuUsage;
    
    /**
     * 内存使用情况
     */
    private MemoryInfo memory;
    
    /**
     * 磁盘使用情况
     */
    private DiskInfo disk;
    
    /**
     * JVM内存使用情况
     */
    private JvmMemoryInfo jvmMemory;
    
    /**
     * 数据库连接池状态
     */
    private DbPoolInfo dbPool;
    
    /**
     * API请求统计
     */
    private ApiStats apiStats;
    
    /**
     * 监控时间
     */
    private LocalDateTime monitorTime;
    
    /**
     * 内存信息类
     */
    @Data
    public static class MemoryInfo {
        /**
         * 总内存（MB）
         */
        private long total;
        
        /**
         * 已使用内存（MB）
         */
        private long used;
        
        /**
         * 可用内存（MB）
         */
        private long available;
        
        /**
         * 使用率（%）
         */
        private double usage;
    }
    
    /**
     * 磁盘信息类
     */
    @Data
    public static class DiskInfo {
        /**
         * 总容量（GB）
         */
        private long total;
        
        /**
         * 已使用容量（GB）
         */
        private long used;
        
        /**
         * 可用容量（GB）
         */
        private long available;
        
        /**
         * 使用率（%）
         */
        private double usage;
    }
    
    /**
     * JVM内存信息类
     */
    @Data
    public static class JvmMemoryInfo {
        /**
         * 堆内存总量（MB）
         */
        private long heapTotal;
        
        /**
         * 堆内存已使用（MB）
         */
        private long heapUsed;
        
        /**
         * 非堆内存总量（MB）
         */
        private long nonHeapTotal;
        
        /**
         * 非堆内存已使用（MB）
         */
        private long nonHeapUsed;
        
        /**
         * 堆内存使用率（%）
         */
        private double heapUsage;
        
        /**
         * 非堆内存使用率（%）
         */
        private double nonHeapUsage;
    }
    
    /**
     * 数据库连接池信息类
     */
    @Data
    public static class DbPoolInfo {
        /**
         * 活跃连接数
         */
        private int activeConnections;
        
        /**
         * 最大连接数
         */
        private int maxConnections;
        
        /**
         * 空闲连接数
         */
        private int idleConnections;
        
        /**
         * 连接使用率（%）
         */
        private double usage;
    }
    
    /**
     * API请求统计类
     */
    @Data
    public static class ApiStats {
        /**
         * 总请求数
         */
        private long totalRequests;
        
        /**
         * 成功请求数
         */
        private long successRequests;
        
        /**
         * 失败请求数
         */
        private long failedRequests;
        
        /**
         * 平均响应时间（毫秒）
         */
        private double avgResponseTime;
    }
}
