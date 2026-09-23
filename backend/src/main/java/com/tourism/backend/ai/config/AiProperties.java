package com.tourism.backend.ai.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    private AiProvider provider = AiProvider.OPENAI;

    private OpenAi openai = new OpenAi();

    private Gemini gemini = new Gemini();

    private Ollama ollama = new Ollama();

    @Getter
    @Setter
    public static class OpenAi {

        private String apiKey;

        private String model = "gpt-5.6-mini";
    }

    @Getter
    @Setter
    public static class Gemini {

        private String apiKey;

        private String model = "gemini-2.5-flash";
    }

    @Getter
    @Setter
    public static class Ollama {

        private String baseUrl = "http://localhost:11434";

        private String model = "qwen3:8b";
    }
}