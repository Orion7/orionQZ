package com.example.qz.dto;

public class AnswerDto {
    private Long answerId;
    private ApproveState approveState;
    private Integer cost;

    public AnswerDto(Long answerId, ApproveState approved, Integer cost) {
        this.answerId = answerId;
        this.approveState = approved;
        this.cost = cost;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public ApproveState getApproveState() {
        return approveState;
    }

    public Integer getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return "AnswerDto{" +
            "answerId=" + answerId +
            ", approved=" + approveState +
            '}';
    }
}
