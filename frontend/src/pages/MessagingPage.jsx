import React, { useEffect, useRef, useState } from 'react';
import '../styles/MessagingPage.css';
import MessageBubble from '../components/MessageBubble';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

export default function MessagingPage() {
  const YOUR_USER_ID = 1;

  const [messages, setMessages] = useState([]);
  const stompClientRef = useRef(null);
  const [messageText, setMessageText] = useState("");

  useEffect(() => {
    fetch("http://localhost:8080/get-messages-servlet")
      .then(res => res.json())
      .then(data => {
        if (data.messages) {
          setMessages(data.messages);
        }
      })
      .catch(err => console.error("Failed to fetch messages:", err));
  }, []);

  useEffect(() => {
    const socket = new SockJS('http://localhost:8080/chat');

    const stompClient = new Client({
      webSocketFactory: () => socket,
      debug: (str) => {
        console.log('STOMP: ' + str);
      },
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
    });

    stompClient.onConnect = (frame) => {
      console.log('Connected: ' + frame);

      stompClient.subscribe('/topic/publicmessages', (message) => {
        try {
          const receivedMessage = JSON.parse(message.body);
          setMessages(prev => [...prev, receivedMessage]);
        } catch (error) {
          console.error("Failed to parse received message:", error, "Message body:", message.body);
        }
      });
    };

    stompClient.onError = (err) => {
      console.error('STOMP error', err);
    };

    stompClient.activate();

    stompClientRef.current = stompClient;

    return () => {
      if (stompClientRef.current) {
        stompClientRef.current.deactivate();
        console.log("STOMP client disconnected");
      }
    };
  }, []);

  const sendMessage = () => {
    if (!stompClientRef.current || !stompClientRef.current.connected) {
      console.warn("STOMP client is not connected.");
      return;
    }

    const messageContent = messageText.trim();
    if (!messageContent) {
      return;
    }

    const newMsg = {
      text: messageContent,
      senderId: YOUR_USER_ID,
      timestamp: Date.now()
    };

    stompClientRef.current.send(
      "/app/chat.sendMessage",
      {},
      JSON.stringify(newMsg)
    );

    setMessageText("");
  };

  return (
    <div className="messaging-page">
      <div className="chat-area">
          <>
            <div className="chat-header-full">
              <h2>Message the community!</h2>
            </div>

            <div className="chat-body-full">
              {messages?.map((msg, idx) => (
                <MessageBubble
                  key={idx}
                  message={msg}
                  isOwnMessage={msg.senderId === YOUR_USER_ID}
                />
              ))}
            </div>

            <div className="chat-input">
              <input
                type="text"
                placeholder="Type your message…"
                value={messageText}
                onChange={e => setMessageText(e.target.value)}
                onKeyDown={e => e.key === "Enter" && sendMessage()}
              />
              <button
                className="send-message-button"
                onClick={sendMessage}
                disabled={!messageText.trim() || !stompClientRef.current?.connected}
              >
                Send
              </button>
            </div>
          </>
      </div>
    </div>
  );
};