package com.example.study.api.type;

import org.springframework.util.Assert;

public class Requester {
    private final String id;
    private final Position position;

    private Requester(String id, Position position) {
        this.id = id;
        this.position = position;
    }

    public enum Position {
        ADMIN("admin"),
        SELLER("seller"),
        DOCENT("docent"),
        USER("");

        private final String postfix;

        Position(String postfix) {
            this.postfix = postfix;
        }

        public static Position parse(String s) {
            for (Position position : values()) {
                if (position.postfix.equals(s)) {
                    return position;
                }
            }

            throw new IllegalArgumentException("requester의 position을 확인해 주세요.");
        }
    }

    private static final String DELIMITER = "@";

    public static Requester of(String id, Position position) {
        Assert.notNull(position, "requester의 position이 없습니다.");
        return new Requester(id, position);
    }

    public static Requester parse(String s) {
        Assert.hasLength(s, "requester 토큰을 확인해 주세요.");

        String[] split = s.split(DELIMITER);

        if (split.length == 1) {
            return new Requester(split[0], Position.USER);
        }

        if (split.length == 2) {
            return new Requester(split[0], Position.parse(split[1]));
        }

        throw new IllegalArgumentException("requester 토큰의 형식이 맞지 않습니다.");
    }

    @Override
    public String toString() {
        return id + DELIMITER + position.postfix;
    }
}
