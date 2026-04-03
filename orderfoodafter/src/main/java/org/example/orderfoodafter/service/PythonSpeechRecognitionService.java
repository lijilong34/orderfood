package org.example.orderfoodafter.service;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * 语音识别服务（使用 Python + SpeechRecognition）
 * 
 * 这是一个完全免费、开源、无限制的方案
 * 需要安装：Python 3.8+, SpeechRecognition 库
 * 
 * @author iFlow
 * @date 2026-03-16
 */
@Service
public class PythonSpeechRecognitionService {

    // Python 脚本路径
    private static final String PYTHON_SCRIPT_PATH = "speech_to_text.py";

    /**
     * 识别语音文件
     * 
     * @param audioFilePath 音频文件路径
     * @return 识别出的文本
     */
    public String recognize(String audioFilePath) {
        try {
            // 构建 Python 命令
                /**
     * ProcessBuilder
     * 
     * @author 李吉隆
     */
            ProcessBuilder processBuilder = new ProcessBuilder(
                "C:\\Users\\lijil\\miniconda3\\python.exe", "-u", PYTHON_SCRIPT_PATH, audioFilePath
            );
            
            // 设置工作目录为项目根目录
                /**
     * File
     * 
     * @author 李吉隆
     */
            File projectDir = new File(System.getProperty("user.dir"));
            processBuilder.directory(projectDir);
            
            // 合并错误输出到标准输出（用于调试）
            processBuilder.redirectErrorStream(true);
            
            // 启动进程
            Process process = processBuilder.start();
            
            // 使用UTF-8编码读取输出
                /**
     * BufferedReader
     * 
     * @author 李吉隆
     */
            BufferedReader reader = new BufferedReader(
                    /**
     * InputStreamReader
     * 
     * @author 李吉隆
     */
                new InputStreamReader(process.getInputStream(), "UTF-8")
            );
            
                /**
     * StringBuilder
     * 
     * @author 李吉隆
     */
            StringBuilder output = new StringBuilder();
            String line;
            
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            
            // 等待进程结束
            int exitCode = process.waitFor();
            
            if (exitCode != 0) {
                // 只在失败时显示错误
                System.err.println("语音识别失败，退出码: " + exitCode);
                System.err.println("Python输出: " + output);
                    /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
                throw new RuntimeException("Python 脚本执行失败，退出码: " + exitCode + "\n输出: " + output);
            }
            
            String result = output.toString().trim();
            
            // 提取实际的识别结果（过滤掉所有日志行）
            String[] lines = result.split("\n");
                /**
     * StringBuilder
     * 
     * @author 李吉隆
     */
            StringBuilder finalResult = new StringBuilder();
            for (String l : lines) {
                l = l.trim();
                // 跳过所有日志行，只保留实际识别结果
                if (!l.startsWith("Python脚本已启动") &&
                    !l.startsWith("使用镜像站") &&
                    !l.startsWith("==========") &&
                    !l.startsWith("正在准备") &&
                    !l.startsWith("模型大小") &&
                    !l.startsWith("缓存目录") &&
                    !l.startsWith("✓") &&
                    !l.startsWith("⏳") &&
                    !l.startsWith("⚠️") &&
                    !l.startsWith("❌") &&
                    !l.startsWith("ERROR") &&
                    !l.startsWith("正在从镜像站") &&
                    !l.startsWith("Fetching") &&
                    !l.startsWith("C:") &&
                    !l.startsWith("D:") &&
                    !l.startsWith("提示：") &&
                    !l.startsWith("An error happened") &&
                    !l.startsWith("huggingface_hub") &&
                    !l.contains("warnings.warn") &&
                    !l.isEmpty()) {
                    finalResult.append(l);
                }
            }
            
            result = finalResult.toString().trim();
            
            if (result.isEmpty()) {
                    /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
                throw new RuntimeException("未能识别到语音内容");
            }
            
            return result;
            
        } catch (Exception e) {
                /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
            throw new RuntimeException("语音识别失败: " + e.getMessage(), e);
        }
    }

    /**
     * 识别语音数据（保存为临时文件后识别）
     * 
     * @param audioData 音频数据
     * @return 识别出的文本
     */
    public String recognize(byte[] audioData) {
        try {
            // 创建临时文件
            File tempFile = File.createTempFile("speech_", ".wav");
            tempFile.deleteOnExit();
            
            // 保存音频数据到临时文件
            java.nio.file.Files.write(tempFile.toPath(), audioData);
            
            // 调用识别方法
            String result = recognize(tempFile.getAbsolutePath());
            
            // 删除临时文件
            tempFile.delete();
            
            return result;
            
        } catch (Exception e) {
                /**
     * RuntimeException
     * 
     * @author 李吉隆
     */
            throw new RuntimeException("语音识别失败: " + e.getMessage(), e);
        }
    }
}
