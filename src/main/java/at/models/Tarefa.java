package at.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter; // Para formatar a data ISO-8601

public class Tarefa {
    private int id;
    private String titulo;
    private String descricao;
    private boolean concluida;
    private String dataCriacao; // formato ISO-8601

    // Construtor padrão
    public Tarefa() {
        this.dataCriacao = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }

    // Construtor para criar uma tarefa com título e descrição
    public Tarefa(String titulo, String descricao) {
        this();
        this.titulo = titulo;
        this.descricao = descricao;
        this.concluida = false;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isConcluida() {
        return concluida;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setConcluida(boolean concluida) {
        this.concluida = concluida;
    }

    // Setter para dataCriacao
    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    @Override
    public String toString() {
        return "Tarefa{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", descricao='" + descricao + '\'' +
                ", concluida=" + concluida +
                ", dataCriacao='" + dataCriacao + '\'' +
                '}';
    }
}