// src/main/java/com/example/demo/controller/ChatController.java
package com.example.demo.controller;

import com.example.demo.dto.MessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
  private final SimpMessagingTemplate broker;

  public ChatController(SimpMessagingTemplate broker) {
    this.broker = broker;
  }

  @MessageMapping("/send")
  public void receive(MessageDTO msg) {
    // 1) Persist to DB if you want
    //    e.g. messageService.save(msg);

    // 2) Broadcast to topic for the recipient
    broker.convertAndSend(
      "/topic/messages." + msg.getReceiverId(),
      msg
    );
  }
}
