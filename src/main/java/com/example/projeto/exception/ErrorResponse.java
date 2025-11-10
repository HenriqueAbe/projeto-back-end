package com.example.projeto.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private String codigo;
    private String mensagem;
    private int status;
    private LocalDateTime timestamp;

    public ErrorResponse(String codigo, String mensagem, int status) {
        this.codigo = codigo;
        this.mensagem = mensagem;
        this.status = status;
        this.timestamp = LocalDateTime.now();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public int getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
