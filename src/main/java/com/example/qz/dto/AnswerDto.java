package com.example.qz.dto;

public class AnswerDto {
    private Long answerId;
    private ApproveState approveState;

    public AnswerDto(Long answerId, ApproveState approved) {
        this.answerId = answerId;
        this.approveState = approved;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public ApproveState getApproveState() {
        return approveState;
    }

    @Override
    public String toString() {
        return "AnswerDto{" +
            "answerId=" + answerId +
            ", approved=" + approveState +
            '}';
    }
}
