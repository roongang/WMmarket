package com.around.wmmarket.controller.dto.notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class OutputMessage {
    private String from;
    private String text;
    private String time;
}
