package com.khrd.studentmanagement.model.entity;

import lombok.*;

import java.sql.Timestamp;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessage {
    private String sender;
    private String receiver;
    private String content;
    private String type;
    private Timestamp timestamp;
}
