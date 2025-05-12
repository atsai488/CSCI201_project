// src/pages/MessagingPage.jsx
import React, { useEffect, useRef, useState } from 'react';
import '../styles/MessagingPage.css';
import MessageBubble from '../components/MessageBubble';

export default function MessagingPage() {
  const YOUR_USER_ID = 1;

  const [messages, setMessages] = useState([]);
  const socketRef = useRef(null);
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
    socketRef.current = new WebSocket("ws://localhost:8080/chat");

    socketRef.current.onmessage = (event) => {
      setMessages(prev => [...prev, JSON.parse(event.data)]);
    };

    return () => socketRef.current.close();
  }, []);

  const sendMessage = () => {
    const newMsg = {
      text: messageText.trim(),
      senderId: YOUR_USER_ID,
      timestamp: Date.now()
    };
    socketRef.current.send(JSON.stringify(newMsg));
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
                disabled={!messageText.trim()}
              >
                Send
              </button>
            </div>
          </>
      </div>
    </div>
  );
};