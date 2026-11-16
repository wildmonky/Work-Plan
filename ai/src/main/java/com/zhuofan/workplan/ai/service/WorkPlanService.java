package com.zhuofan.workplan.ai.service;

import com.google.gson.Gson;
import com.zhuofan.workplan.ai.dto.WorkPlanModelDTO;
import com.zhuofan.workplan.ai.dto.WorkPlanQdrantDTO;
import com.zhuofan.workplan.ai.tools.TestTools;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.*;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
public class WorkPlanService {

    @Resource
    private Gson gson;

    @Resource
    private ChatModel chatModel;

    @Resource
    private EmbeddingModel embeddingModel;

    @Resource
    private TestTools testTools;

    @Value("classpath:/ai/workplan_clean.md")
    private ClassPathResource systemPrompt;

    private ChatClient CHAT_CLIENT;

    @PostConstruct
    public void postConstruct() {
        CHAT_CLIENT = ChatClient.create(chatModel);
    }

    public WorkPlanQdrantDTO format(WorkPlanModelDTO workPlanModelDTO) {
        if (workPlanModelDTO == null) {
            throw new IllegalArgumentException("参数为空");
        }

        String userMessage = """
                作业任务: %s
                专业：%s
                风险等级：%s
                """.formatted(workPlanModelDTO.getPlan(),
                workPlanModelDTO.getProfession(),
                workPlanModelDTO.getRisk());

        List<Message> messages = Arrays.asList(
                SystemMessage.builder()
                        .text(systemPrompt)
                        .build(),
                UserMessage.builder()
                        .text(userMessage)
                        .build()
        );

        Prompt prompt = new Prompt(messages,
                OllamaChatOptions.builder()
                        .enableThinking()
                        .build()
        );

        String resContent = CHAT_CLIENT
                .prompt(prompt)
                .tools(testTools)
                .call()
                .content();

        return gson.fromJson(resContent, WorkPlanQdrantDTO.class);
    }

    /**
     * 批量获取文本对应的向量
     * @param texts 输入文本
     * @param embeddingModel 嵌入模型
     * @return 嵌入模型生成的向量
     */
    public List<List<Float>> embeddingBatch(List<String> texts, EmbeddingModel embeddingModel) {
        EmbeddingResponse embeddingResponse = embeddingModel.embedForResponse(texts);
        List<Embedding> results = embeddingResponse.getResults();
        return results.stream().map(e -> {
            float[] output = e.getOutput();
            return IntStream.range(0, output.length)
                    .mapToObj(i -> output[i])
                    .toList();
        }).toList();
    }

}
