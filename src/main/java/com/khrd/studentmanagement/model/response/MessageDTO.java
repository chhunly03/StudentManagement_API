package com.khrd.studentmanagement.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
@AllArgsConstructor
@NoArgsConstructor
@Data
public class MessageDTO {
    private String sender;
    private String receiver;
    private String content;
    private String type;
    private Timestamp timestamp;
}
