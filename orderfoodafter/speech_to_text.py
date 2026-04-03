#!/usr/bin/env python
# -*- coding: utf-8 -*-
"""
语音识别脚本（完全免费、开源、离线、无限制）
使用 faster-whisper 进行语音识别

依赖安装：
pip install faster-whisper

使用方法：
python speech_to_text.py 音频文件路径

输出：
识别出的文本
"""

import sys
import os

# 强制无缓冲输出
sys.stdout.reconfigure(encoding='utf-8', line_buffering=True)
sys.stderr.reconfigure(encoding='utf-8', line_buffering=True)

# 设置国内镜像站
os.environ["HF_ENDPOINT"] = "https://hf-mirror.com"
os.environ["HF_HUB_ENABLE_HF_TRANSFER"] = "0"
os.environ["HF_HUB_DOWNLOAD_TIMEOUT"] = "600"

# 立即输出
print("Python脚本已启动...", flush=True)
print(f"使用镜像站: {os.environ.get('HF_ENDPOINT', '默认')}", flush=True)

from faster_whisper import WhisperModel

# 模型配置（使用轻量级模型，首次运行会自动下载）
MODEL_SIZE = "base"  # 可选: tiny, base, small, medium, large
COMPUTE_TYPE = "int8"  # 使用 int8 量化，减少内存占用

# 模型缓存目录
MODEL_CACHE_DIR = os.path.join(os.path.dirname(__file__), "whisper_models")

# 全局模型实例
model = None

def load_model():
    """加载 Whisper 模型"""
    global model
    if model is None:
        try:
            print(f"\n{'='*60}", flush=True)
            print(f"正在准备 Whisper 模型: {MODEL_SIZE}", flush=True)
            print(f"模型大小: 约 {get_model_size_mb(MODEL_SIZE)} MB", flush=True)
            print(f"缓存目录: {MODEL_CACHE_DIR}", flush=True)
            print(f"{'='*60}\n", flush=True)
            
            # 检查模型文件
            model_path = os.path.join(MODEL_CACHE_DIR, MODEL_SIZE)
            
            if not os.path.exists(model_path):
                print("⏳ 模型不存在，开始下载...", flush=True)
                print("（这可能需要几分钟，请耐心等待...）\n", flush=True)
                
                # 手动下载并显示详细进度
                from huggingface_hub import snapshot_download
                from huggingface_hub.utils import tqdm as hf_tqdm
                
                # 强制显示进度
                os.environ["HF_HUB_DISABLE_TEAMS_EXPERIMENT"] = "1"
                
                try:
                    # 使用 snapshot_download 并启用进度显示
                    print("正在从镜像站下载模型...", flush=True)
                    model_dir = snapshot_download(
                        repo_id=f"guillaumekln/faster-whisper-{MODEL_SIZE}",
                        cache_dir=MODEL_CACHE_DIR,
                        local_dir=model_path,
                        local_dir_use_symlinks=False,
                        resume_download=True,
                        local_files_only=False,
                        # 这些参数会自动启用进度显示
                        etag_timeout=100,
                        endpoint="https://hf-mirror.com"  # 使用国内镜像加速
                    )
                    print(f"\n{'='*60}", flush=True)
                    print(f"✓ 模型下载完成！", flush=True)
                    print(f"✓ 模型位置: {model_dir}", flush=True)
                    print(f"{'='*60}\n", flush=True)
                except Exception as e:
                    print(f"\n⚠️ 下载时出现问题: {e}", flush=True)
                    print("尝试使用官方源下载...", flush=True)
                    # 如果镜像失败，尝试官方源
                    model_dir = snapshot_download(
                        repo_id=f"guillaumekln/faster-whisper-{MODEL_SIZE}",
                        cache_dir=MODEL_CACHE_DIR,
                        local_dir=model_path,
                        local_dir_use_symlinks=False,
                        resume_download=True
                    )
            else:
                print("✓ 模型文件已存在，跳过下载", flush=True)
            
            print("\n⏳ 正在加载模型到内存...", flush=True)
            
            # 加载模型
            model = WhisperModel(
                MODEL_SIZE,
                compute_type=COMPUTE_TYPE,
                download_root=MODEL_CACHE_DIR,
                device="cpu"
            )
            
            print(f"\n{'='*60}", flush=True)
            print("✓ 模型加载成功！", flush=True)
            print("✓ 现在可以开始语音识别了", flush=True)
            print(f"{'='*60}\n", flush=True)
            
        except Exception as e:
            print(f"\n{'='*60}", flush=True)
            print(f"❌ ERROR: 模型加载失败: {e}", flush=True)
            print(f"{'='*60}\n", flush=True)
            import traceback
            traceback.print_exc()
            print("\n提示：", flush=True)
            print("1. 请确保网络连接正常（首次运行需要下载模型）", flush=True)
            print("2. 如果下载中断，可以重新运行，会自动续传", flush=True)
            print("3. 模型下载后会自动缓存，无需再次下载", flush=True)
            print(f"4. 缓存目录: {MODEL_CACHE_DIR}", flush=True)
            raise

def recognize_speech(audio_file_path):
    """
    识别语音文件（完全离线）
    
    Args:
        audio_file_path: 音频文件路径
        
    Returns:
        识别出的文本
    """
    # 检查文件是否存在
    if not os.path.exists(audio_file_path):
        print(f"ERROR: 文件不存在: {audio_file_path}")
        return None
    
    try:
        # 确保模型已加载
        load_model()
        
        # 进行语音识别（强制使用简体中文）
        segments, info = model.transcribe(
            audio_file_path,
            language="zh",  # 中文
            beam_size=5,
            vad_filter=True,  # 启用语音活动检测
            vad_parameters=dict(
                min_silence_duration_ms=500,
                speech_pad_ms=30
            ),
            # 额外参数提高简体中文识别准确率
            word_timestamps=False,
            condition_on_previous_text=True,
            initial_prompt="这是一个简体中文的语音识别，使用中国大陆的标准普通话。"  # 提示模型使用简体中文
        )
        
        # 提取识别结果
        result_text = ""
        for segment in segments:
            result_text += segment.text
        
        result_text = result_text.strip()
        
        # 繁体转简体（确保结果始终为简体中文）
        result_text = convert_to_simplified(result_text)
        
        return result_text
        
    except Exception as e:
        print(f"ERROR: 识别失败: {e}")
        return None

def test_installation():
    """测试语音识别功能"""
    try:
        from faster_whisper import WhisperModel
        print("✓ faster-whisper 已安装")
        
        # 检查模型缓存目录
        if os.path.exists(MODEL_CACHE_DIR):
            print(f"✓ 模型缓存目录: {MODEL_CACHE_DIR}")
        else:
            print(f"ℹ️  模型缓存目录: {MODEL_CACHE_DIR}（首次运行时会自动下载模型）")
        
        print(f"\n提示：")
        print(f"  - 使用模型: {MODEL_SIZE}")
        print(f"  - 模型大小: 约 {get_model_size_mb(MODEL_SIZE)} MB")
        print(f"  - 首次运行会自动下载模型，需要网络连接")
        print(f"  - 下载后完全离线运行")
        print(f"\n使用方法: python speech_to_text.py 音频文件路径")
        return True
    except ImportError as e:
        print(f"❌ 缺少依赖: {e}")
        print("请运行: pip install faster-whisper")
        return False

def get_model_size_mb(model_size):
    """获取模型大小（MB）"""
    sizes = {
        "tiny": 74,
        "base": 145,
        "small": 486,
        "medium": 1.5 * 1024,
        "large": 3.0 * 1024
    }
    return sizes.get(model_size, 0)

def main():
    """主函数"""
    # 测试模式
    if len(sys.argv) > 1 and sys.argv[1] == "--test":
        if test_installation():
            sys.exit(0)
        else:
            sys.exit(1)
    
    # 正常识别模式
    if len(sys.argv) < 2:
        print("ERROR: 请提供音频文件路径")
        print("使用方法: python speech_to_text.py 音频文件路径")
        print("测试安装: python speech_to_text.py --test")
        sys.exit(1)
    
    audio_file_path = sys.argv[1]
    
    # 识别语音
    result = recognize_speech(audio_file_path)
    
    if result:
        print(result)
        sys.exit(0)
    else:
        sys.exit(1)

if __name__ == "__main__":
    main()