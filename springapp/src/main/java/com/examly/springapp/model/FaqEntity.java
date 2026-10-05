package com.examly.springapp.model;

import jakarta.persistence.*;

@Entity
@Table(name = "faqs")
public class FaqEntity {

    @Id
    private Long id;

    private String category;

    @Column(length = 1000)
    private String question;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String answer;

    public FaqEntity() {
    }

    public FaqEntity(Long id, String category, String question, String answer) {
        this.id = id;
        this.category = category;
        this.question = question;
        this.answer = answer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
