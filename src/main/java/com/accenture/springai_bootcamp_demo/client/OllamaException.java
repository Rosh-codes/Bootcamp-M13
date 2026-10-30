package com.accenture.springai_bootcamp_demo.client;

/** Raised when the local Ollama model cannot provide a usable response. */
public class OllamaException extends RuntimeException {

    public OllamaException(String message) {
        super(message);
    }

    public OllamaException(String message, Throwable cause) {
        super(message, cause);
    }
}
