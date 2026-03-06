package org.example.orderfoodafter.common;

import org.example.orderfoodafter.entity.User;
import org.example.orderfoodafter.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class UpdateUserRedis {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private UserService userService;

    // 修正：void方法不返回值，删除错误的return语句
    public void cheangeuserinfoconut(){
        // 定义匹配规则
        String pattern = "userlogindata" + "*";
        // 存储所有匹配的键（一次性收集）
        Set<String> allMatchKeys = new HashSet<>();

        // 使用scan命令遍历所有匹配的键（非阻塞）
        Cursor<byte[]> cursor = redisTemplate.execute((RedisCallback<Cursor<byte[]>>) connection -> {
            ScanOptions scanOptions = ScanOptions.scanOptions()
                    .match(pattern)  // 匹配前缀
                    .count(1000)     // 单次扫描量（仅为Redis遍历建议值，不影响最终全量收集）
                    .build();
            return connection.scan(scanOptions);
        });

        try {
            // 一次性收集所有匹配的键
            if (cursor != null) { // 防止cursor为空导致空指针
                while (cursor.hasNext()) {
                    String key = new String(cursor.next());
                    allMatchKeys.add(key);
                }
            }

            // 一次性删除所有收集到的键（移除return，直接执行删除）
            if (!allMatchKeys.isEmpty()) {
                Long deletedCount = redisTemplate.delete(allMatchKeys);
                // 可选：打印删除数量，便于调试
                System.out.println("成功删除 " + deletedCount + " 个userlogindata相关键");
            }

            // 重新写入用户数据到Redis
            List<User> userList = userService.list();
            for (User user : userList) {
                redisTemplate.opsForValue().set("userlogindata:" + user.getPhone(), user.getPassword());
            }
        } catch (Exception e) {
            // 捕获异常并打印，避免程序崩溃
            e.printStackTrace();
            throw new RuntimeException("更新Redis用户信息失败", e);
        } finally {
            // 必须关闭游标，释放Redis连接资源
            if (cursor != null) {
                try {
                    cursor.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void updateuserinfo(String phone,String password){
        redisTemplate.opsForValue().getAndSet(phone,password);
    }
}
