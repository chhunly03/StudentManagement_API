package com.khrd.studentmanagement.service;

import com.khrd.studentmanagement.model.entity.ChatMessage;
import com.khrd.studentmanagement.model.entity.Message;
import com.khrd.studentmanagement.repository.MessageRepository;
import org.springframework.stereotype.Service;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void saveMessage(ChatMessage chatMessage) {
        Message message = new Message(null, chatMessage.getSender(), chatMessage.getReceiver(),
                                      chatMessage.getContent(),chatMessage.getType(), chatMessage.getTimestamp());
        messageRepository.save(message);
    }
}
