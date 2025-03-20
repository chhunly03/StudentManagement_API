package com.khrd.studentmanagement.controller;

import com.khrd.studentmanagement.model.entity.Message;
import com.khrd.studentmanagement.model.response.ApiResponse;
import com.khrd.studentmanagement.model.response.MessageDTO;
import com.khrd.studentmanagement.repository.MessageRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@AllArgsConstructor
@CrossOrigin
public class WebSocketController {

    private final MessageRepository messageRepository;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message handleMessage(MessageDTO messageDTO) {
        System.out.println("message dto: "+messageDTO);
        Message message = Message.builder()
                .sender(messageDTO.getSender())
                .receiver(messageDTO.getReceiver())
                .content(messageDTO.getContent())
                .type(messageDTO.getType())
                .timestamp(messageDTO.getTimestamp())
                .build();
            return messageRepository.save(message);
    }


    @GetMapping("/{senderId}/{receiverId}")
    public ResponseEntity<ApiResponse<List<Message>>> handleMessage(@PathVariable String senderId, @PathVariable String receiverId) {
        List<Message> messages = messageRepository.findBySenderAndReceiver(senderId, receiverId);
        if (!messages.isEmpty()) {
            ApiResponse<List<Message>>response = ApiResponse.<List<Message>>builder()
                    .message("Get data is successfully")
                    .statusCode(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .payload(messages)
                    .timestamp(new Timestamp(System.currentTimeMillis()))
                    .build();
            return ResponseEntity.ok(response);
        }else {
            ApiResponse<List<Message>> response = ApiResponse.<List<Message>>builder()
                    .message("Get message is not successfully")
                    .statusCode(HttpStatus.NO_CONTENT.value())
                    .httpStatus(HttpStatus.NO_CONTENT)
                    .payload(messages)
                    .timestamp(new Timestamp(System.currentTimeMillis()))
                    .build();
            return ResponseEntity.ok(response);
        }
    }

}
