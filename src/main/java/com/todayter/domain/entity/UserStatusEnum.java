package com.todayter.domain.entity;

public enum UserStatusEnum {

    ACTIVE(Status.ACTIVE),
    BLOCK(Status.BLOCK),
    WITHDRAW(Status.WITHDRAW);

    private final String status;

    UserStatusEnum(String status) {
        this.status = status;
    }

    public String getStatus() {

        return this.status;
    }

    public static class Status {
        public static final String ACTIVE = "ACTIVE";
        public static final String BLOCK = "BLOCK";
        public static final String WITHDRAW = "WITHDRAW";
    }

}
