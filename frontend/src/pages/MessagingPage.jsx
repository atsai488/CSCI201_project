import React, { useEffect, useRef, useState } from 'react';
import '../styles/MessagingPage.css';
import MessageBubble from '../components/MessageBubble';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

export default function MessagingPage() {
  const [messages, setMessages] = useState([]);
  const stompClientRef = useRef(null);
  const [messageText, setMessageText] = useState("");
  const [YOUR_USER_ID, setYOUR_USER_ID] = useState(0);
  function formatTimestamp(millis) {
      const date = new Date(millis);
      return date.toLocaleString('en-US', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit',
        hour12: false
      });
    }
  const getUserID = async () => {
    try {
      const email = localStorage.getItem('email');
      const response = await fetch(`/get-user-id-servlet?email=${encodeURIComponent(email)}`, {
        method: 'GET',
        credentials: 'include'
      });
      const data = await response.json();

      if (data.userId !== undefined) {
        setYOUR_USER_ID(data.userId);
      } else {
        throw new Error("No userId in response");
      }
    } catch (error) {
      setYOUR_USER_ID(0);
    }
  };


  useEffect(() => {
    getUserID();
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
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  stompClient.onConnect = (frame) => {
    
    stompClientRef.current = stompClient;

    stompClient.subscribe('/topic/publicmessages', (message) => {
      try {
        const receivedMessage = JSON.parse(message.body);
        receivedMessage.formattedTimestamp = formatTimestamp(receivedMessage.timestamp);
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

  return () => {
    if (stompClientRef.current) {
      stompClientRef.current.deactivate();
    }
  };
}, []);

  const sendMessage = () => {
  const messageContent = messageText.trim();
  if (!messageContent) {
    return;
  }

  const newMsg = {
    text: messageContent,
    senderId: YOUR_USER_ID,
    timestamp: Date.now()
  };

  stompClientRef.current.publish({
    destination: "/app/chat.sendMessage",
    body: JSON.stringify(newMsg)
  });


  setMessageText("");
};

  return (
    <div className="messaging-page">
      <div className="chat-area">
          <>
            <div className="chat-header-full">
              <h2>Message the community!</h2>
              <div id="home">
                <a href="/home">
                  <button className="home-button">Home</button>
                </a>
              </div>
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