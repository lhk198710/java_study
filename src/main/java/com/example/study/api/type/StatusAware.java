package com.example.study.api.type;

import java.time.LocalDateTime;

public interface StatusAware {

    int status();

    String updater();

    LocalDateTime updated();
}
