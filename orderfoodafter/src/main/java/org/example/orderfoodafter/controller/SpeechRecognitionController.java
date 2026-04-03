package org.example.orderfoodafter.controller;

import org.example.orderfoodafter.common.R;
import org.example.orderfoodafter.service.PythonSpeechRecognitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

/**
 * 语音识别控制器
 * 提供语音转文字的接口（使用 Python + SpeechRecognition，完全免费、开源、无限制）
 * 
 * @author iFlow
 * @date 2026-03-16
 */
@RestController
@RequestMapping("/speech")
@CrossOrigin
public class SpeechRecognitionController {

    @Autowired
    private PythonSpeechRecognitionService speechRecognitionService;

    /**
     * 语音识别接口
     * 接收音频文件，返回识别出的文本
     * 
     * @param file 音频文件（支持 WAV、PCM 等格式）
     * @return 识别结果
     */
    @PostMapping("/recognize")
    public R recognize(@RequestParam("file") MultipartFile file) {
        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                return R.error("上传文件不能为空");
            }
            
            // 检查文件大小（限制为 10MB）
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (file.getSize() > maxSize) {
                return R.error("文件大小不能超过 10MB");
            }
            
            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || (!contentType.contains("audio") && !file.getOriginalFilename().toLowerCase().endsWith(".wav"))) {
                return R.error("请上传音频文件（WAV 格式）");
            }
            
            // 读取音频数据
            byte[] audioData = file.getBytes();
            
            // 调用语音识别服务
            String result = speechRecognitionService.recognize(audioData);
            
            if (result == null || result.trim().isEmpty()) {
                return R.error("未能识别到语音，请重新录制");
            }
            
            // 返回识别结果
            return R.ok()
                .addData("text", result)
                .addData("message", "语音识别成功");
            
        } catch (IllegalArgumentException e) {
            return R.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("语音识别失败: " + e.getMessage());
        }
    }

    /**
     * 检查语音识别服务状态
     * 
     * @return 服务状态
     */
    @GetMapping("/status")
    public R status() {
        try {
            return R.ok()
                .addData("status", "available")
                .addData("message", "语音识别服务可用（使用 Python + SpeechRecognition）");
        } catch (Exception e) {
            return R.error("语音识别服务不可用: " + e.getMessage());
        }
    }

    /**
     * 测试Python输出（用于调试）
     * 
     * @return 测试结果
     */
    @GetMapping("/test-output")
    public R testOutput() {
        try {
                /**
     * ProcessBuilder
     * 
     * @author 李吉隆
     */
            ProcessBuilder processBuilder = new ProcessBuilder(
                "python", "-u", "test_python_output.py"
            );
            
                /**
     * File
     * 
     * @author 李吉隆
     */
            File projectDir = new File(System.getProperty("user.dir"));
            processBuilder.directory(projectDir);
            processBuilder.redirectErrorStream(true);
            
            Process process = processBuilder.start();
            
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
                new InputStreamReader(process.getInputStream(), "GBK")
            );
            
                /**
     * StringBuilder
     * 
     * @author 李吉隆
     */
            StringBuilder output = new StringBuilder();
            String line;
            System.out.println("\n=== 开始测试Python输出 ===");
            while ((line = reader.readLine()) != null) {
                System.out.println("[Python测试] " + line);
                System.out.flush();
                output.append(line).append("\n");
            }
            System.out.println("=== 测试结束 ===\n");
            
            int exitCode = process.waitFor();
            
            return R.ok()
                .addData("exitCode", exitCode)
                .addData("output", output.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return R.error("测试失败: " + e.getMessage());
        }
    }
}