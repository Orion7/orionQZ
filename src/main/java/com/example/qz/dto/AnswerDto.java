package com.example.qz.dto;

public class AnswerDto {
    private Long answerId;
    private Boolean approved;

    public AnswerDto(Long answerId, Boolean approved) {
        this.answerId = answerId;
        this.approved = approved;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public Boolean getApproved() {
        return approved;
    }

    @Override
    public String toString() {
        return "AnswerDto{" +
            "answerId=" + answerId +
            ", approved=" + approved +
            '}';
    }
}
