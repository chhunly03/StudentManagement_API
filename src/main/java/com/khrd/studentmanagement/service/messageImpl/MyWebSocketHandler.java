package com.khrd.studentmanagement.service.messageImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.khrd.studentmanagement.model.entity.Message;
import com.khrd.studentmanagement.model.response.MessageDTO;
import com.khrd.studentmanagement.repository.MessageRepository;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.sql.Timestamp;
import java.util.concurrent.CopyOnWriteArraySet;

public class MyWebSocketHandler extends TextWebSocketHandler {

    private final MessageRepository messageRepository;
    private static final CopyOnWriteArraySet<WebSocketSession> sessions = new CopyOnWriteArraySet<>();
    private final ObjectMapper objectMapper;

    public MyWebSocketHandler(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessions.add(session);
        System.out.println("New connection: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received: " + payload);

        // Broadcast message to all connected clients
        broadcastMessage(payload);

        // Deserialize JSON to MessageDTO
        MessageDTO messageDTO = objectMapper.readValue(payload, MessageDTO.class);
        System.out.println("Message received: " + messageDTO);

        // Convert and save message
        Message savedMessage = new Message();
        savedMessage.setContent(messageDTO.getContent());
        savedMessage.setSender(messageDTO.getSender());
        savedMessage.setReceiver(messageDTO.getReceiver());
        savedMessage.setType(messageDTO.getType());

        // Use Timestamp directly
        savedMessage.setTimestamp((messageDTO.getTimestamp() != null ? messageDTO.getTimestamp() : new Timestamp(System.currentTimeMillis())));

        // Save to database
        messageRepository.save(savedMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessions.remove(session);
        System.out.println("Connection closed: " + session.getId());
    }

    private void broadcastMessage(String message) throws Exception {
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
            }
        }
    }
}
