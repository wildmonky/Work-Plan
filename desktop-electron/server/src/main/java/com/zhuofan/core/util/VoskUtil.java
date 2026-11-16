package com.zhuofan.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhuofan.core.common.CommonFunc;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class VoskUtil {
    private Model model;
    private String modelPath;

    private ObjectMapper mapper;

    public VoskUtil(String modelPath) throws IOException {
        this.model = new Model(modelPath);
        this.mapper = new ObjectMapper();
    }

    /**
     * 构造函数
     * @param modelPath Vosk模型路径（需提前下载）
     */
    public VoskUtil(String modelPath,boolean flag) {
        this.modelPath = modelPath;
        loadModel();
    }

    /**
     * 加载语音识别模型
     */
    private void loadModel() {
        try {
            System.out.println("正在加载Vosk模型...");
            this.model = new Model(modelPath);
            System.out.println("模型加载成功！");
        } catch (Exception e) {
            System.err.println("模型加载失败: " + e.getMessage());
            throw new RuntimeException("无法加载语音识别模型", e);
        }
    }

    /**
     * 识别音频文件中的语音
     * @param audioFilePath 音频文件路径
     * @return 识别出的文本
     */
    public String recognizeFromFile(String audioFilePath) {
        return recognizeFromFile(audioFilePath, 16000);
    }

    /**
     * 识别音频文件中的语音（可指定采样率）
     * @param audioFilePath 音频文件路径
     * @param sampleRate 采样率（通常为16000或8000）
     * @return 识别出的文本
     */
    public String recognizeFromFile(String audioFilePath, float sampleRate) {
        // 验证文件是否存在
        File audioFile = new File(audioFilePath);
        if (!audioFile.exists()) {
            throw new IllegalArgumentException("音频文件不存在: " + audioFilePath);
        }

        Recognizer recognizer = null;
        AudioInputStream audioStream = null;

        try {
            // 创建识别器，指定采样率
            recognizer = new Recognizer(model, sampleRate);

            // 获取音频文件格式并转换为PCM格式
            audioStream = convertToPCM(audioFilePath, sampleRate);

            // 开始识别
            return processAudioStream(recognizer, audioStream);

        } catch (Exception e) {
            System.err.println("语音识别失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            // 清理资源
            if (recognizer != null) {
                recognizer.close();
            }
            if (audioStream != null) {
                try {
                    audioStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 转换音频文件为PCM格式
     */
    private AudioInputStream convertToPCM(String audioFilePath, float sampleRate)
            throws UnsupportedAudioFileException, IOException {

        AudioInputStream originalStream = AudioSystem.getAudioInputStream(
                new File(audioFilePath));

        // 获取原始音频格式
        AudioFormat originalFormat = originalStream.getFormat();

        // 定义目标音频格式（PCM, 单声道, 16位）
        AudioFormat targetFormat = new AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                sampleRate,   // 采样率
                16,           // 样本位数
                1,            // 单声道
                2,            // 帧大小（字节）
                sampleRate,   // 帧率
                false         // little-endian
        );

        // 如果格式已匹配，直接返回
        if (originalFormat.matches(targetFormat)) {
            return originalStream;
        }

        // 转换为目标格式
        if (AudioSystem.isConversionSupported(targetFormat, originalFormat)) {
            return AudioSystem.getAudioInputStream(targetFormat, originalStream);
        } else {
            throw new UnsupportedAudioFileException("不支持的音频格式或无法转换");
        }
    }

    /**
     * 处理音频流进行识别
     */
    private String processAudioStream(Recognizer recognizer, AudioInputStream audioStream)
            throws IOException {

        AudioFormat format = audioStream.getFormat();
        int bufferSize = (int) (format.getSampleRate() * 0.1); // 100ms缓冲区

        byte[] buffer = new byte[bufferSize];
        int bytesRead;
        StringBuilder fullResult = new StringBuilder();

        System.out.println("开始语音识别...");

        // 分块读取音频数据并识别
        while ((bytesRead = audioStream.read(buffer)) >= 0) {
            if (bytesRead > 0) {
                // 发送音频数据到识别器
                if (recognizer.acceptWaveForm(buffer, bytesRead)) {
                    // 获取部分结果
                    String partialResult = recognizer.getResult();
                    System.out.println("部分结果: " + partialResult);
                }
            }
        }

        // 获取最终结果
        String finalResult = recognizer.getFinalResult();
        System.out.println("最终结果: " + finalResult);

        // 解析JSON结果
        return parseResult(finalResult);
    }

    /**
     * 解析Vosk返回的JSON结果
     */
    private String parseResult(String jsonResult) {
        if (jsonResult == null || jsonResult.isEmpty()) {
            return "";
        }

        try {
            // 简单解析JSON，提取text字段
            // 实际可以使用JSON库如Jackson或Gson
            int textStart = jsonResult.indexOf("\"text\" : \"");
            if (textStart == -1) {
                textStart = jsonResult.indexOf("\"text\":\"");
                if (textStart == -1) return "";
                textStart += 8;
            } else {
                textStart += 10;
            }

            int textEnd = jsonResult.indexOf("\"", textStart);
            if (textEnd == -1) return "";

            return jsonResult.substring(textStart, textEnd);

        } catch (Exception e) {
            System.err.println("解析结果失败: " + e.getMessage());
            return "";
        }
    }

    /**
     * 主识别方法 - 返回分好句的文本
     */
    public String recognizeWithSentences(String audioFilePath) throws Exception {
        // 1. 读取并预处理音频
        System.out.println("begin loadAndConvertAudio");
        byte[] pcmData = loadAndConvertAudio(audioFilePath);
        System.out.println("end loadAndConvertAudio");

        // 2. 创建识别器
        System.out.println("begin Recognizer");
        try (Recognizer recognizer = new Recognizer(model, 12800.0f)) {
            // 3. 处理音频获取结果
            System.out.println("end Recognizer");

            System.out.println(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+"begin acceptWaveForm");
            recognizer.acceptWaveForm(pcmData, pcmData.length);
            System.out.println(CommonFunc.unix2Date(CommonFunc.unixTimeStr())+"end acceptWaveForm");

            System.out.println("begin getResult");
            String resultJson = recognizer.getResult();
            System.out.println("end getResult");

            // 4. 解析结果并分句
            System.out.println("begin processAndFormatResult");
            return processAndFormatResult(resultJson);
        }
    }


    /**
     * 处理结果并智能分句
     */
    private String processAndFormatResult(String jsonResult) throws Exception {
        JsonNode root = mapper.readTree(jsonResult);
        StringBuilder finalText = new StringBuilder();

        if (root.has("text")) {
            String rawText = root.get("text").asText();
            System.out.println("原始识别结果: " + rawText);

            // 方案1：使用简单规则分句
            String sentences = splitIntoSentences(rawText);

            // 方案2：如果模型支持标点，直接使用
            if (hasPunctuation(rawText)) {
                return formatTextWithPunctuation(rawText);
            }

            return sentences;
        }

        return "";
    }

    /**
     * 智能分句算法
     */
    private String splitIntoSentences(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        String[] words = text.split("\\s+");
        StringBuilder sentence = new StringBuilder();
        List<String> sentences = new ArrayList<>();

        // 中文分句规则
        Set<String> sentenceEnders = new HashSet<>(Arrays.asList(
                "的", "了", "吗", "呢", "啊", "吧", "哦", "呀",
                // 可以添加更多语气词作为分句参考
                "然后", "接着", "但是", "不过", "因此", "所以"
        ));

        for (int i = 0; i < words.length; i++) {
            String word = words[i];
            sentence.append(word);

            // 分句条件
            boolean shouldEndSentence = false;

            // 条件1：遇到明显的标点词
            if (word.matches(".*[，。！？；,.!?;]$")) {
                shouldEndSentence = true;
            }
            // 条件2：根据停顿词判断
            else if (sentenceEnders.contains(word) && sentence.length() > 5) {
                // 检查后续单词，如果是新话题的开始
                if (i < words.length - 1) {
                    String nextWord = words[i + 1];
                    if (isNewTopicStart(nextWord)) {
                        shouldEndSentence = true;
                    }
                }
            }
            // 条件3：句子长度控制（避免过长）
            else if (sentence.length() > 50) {
                shouldEndSentence = true;
            }

            if (shouldEndSentence && sentence.length() > 0) {
                sentences.add(sentence.toString().trim());
                sentence = new StringBuilder();
            } else {
                sentence.append(" ");
            }
        }

        // 添加最后一句
        if (sentence.length() > 0) {
            sentences.add(sentence.toString().trim());
        }

        // 格式化输出
        return formatSentences(sentences);
    }

    /**
     * 判断是否是话题开始词
     */
    private boolean isNewTopicStart(String word) {
        Set<String> topicStartWords = new HashSet<>(Arrays.asList(
                "首先", "第一", "其次", "然后", "另外", "还有",
                "不过", "但是", "然而", "因此", "所以", "总之"
        ));
        return topicStartWords.contains(word);
    }

    /**
     * 格式化句子列表
     */
    private String formatSentences(List<String> sentences) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < sentences.size(); i++) {
            String sentence = sentences.get(i);
            // 确保每句以标点结尾
            if (!sentence.matches(".*[。！？.!?]$")) {
                sentence += "。";
            }
            result.append(sentence);
            if (i < sentences.size() - 1) {
                result.append("\n");
            }
        }
        return result.toString();
    }

    /**
     * 检查文本是否包含标点
     */
    private boolean hasPunctuation(String text) {
        return text.matches(".*[，。！？；,.!?;].*");
    }

    /**
     * 格式化带标点的文本
     */
    private String formatTextWithPunctuation(String text) {
        // 替换空格和添加必要标点
        text = text.replaceAll("\\s+", "");

        // 确保以句号结尾
        if (!text.matches(".*[。.!?]$")) {
            text += "。";
        }

        return text;
    }

    /**
     * 加载并转换音频
     */
    private byte[] loadAndConvertAudio(String filePath) throws Exception {
        File audioFile = new File(filePath);
        AudioInputStream ais = AudioSystem.getAudioInputStream(audioFile);
        AudioFormat format = ais.getFormat();

        // 转换为PCM格式
        if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
            AudioFormat pcmFormat = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    16000,
                    16,
                    1,
                    2,
                    16000,
                    false
            );
            ais = AudioSystem.getAudioInputStream(pcmFormat, ais);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = ais.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }

        ais.close();
        return baos.toByteArray();
    }


    /**
     * 使用示例
     */
    public static void main(String[] args) throws Exception{
        // 1. 下载并解压Vosk中文模型到指定目录
        // 模型下载地址：https://alphacephei.com/vosk/models
        String modelPath = "D:\\app\\idea\\oms-server\\ext\\vosk-model-cn-0.22";
        String audioFilePath = "D:\\V001.WAV";

        // 2. 创建识别器
        VoskUtil recognizer = new VoskUtil(modelPath);

        // 3. 进行识别
        System.out.println("begin recognizeWithSentences");
        String result = recognizer.recognizeWithSentences(audioFilePath);
        System.out.println("end recognizeWithSentences");

        if (result != null && !result.isEmpty()) {
            System.out.println("\n=== 识别结果 ===");
            System.out.println(result);
            System.out.println("================");
        } else {
            System.out.println("未识别到有效语音");
        }
    }
}
