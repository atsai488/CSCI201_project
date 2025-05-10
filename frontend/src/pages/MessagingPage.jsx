// src/pages/MessagingPage.jsx
import React, { useEffect, useState } from 'react';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import '../styles/MessagingPage.css';
import ConversationList from '../components/ConversationList';
import MessageBubble from '../components/MessageBubble';

export default function MessagingPage() {
  const stored = sessionStorage.getItem('userId') || localStorage.getItem('userId');
  const YOUR_USER_ID = stored ? parseInt(stored, 10) : 1;
  console.log("stored", stored);
   
  const [conversations, setConversations] = useState([]);
  const [messages, setMessages] = useState({});  // object keyed by otherUserId
  const [selectedConversation, setSelectedConversation] = useState(null);
  const [messageText, setMessageText] = useState("");
  const [stompClient, setStompClient] = useState(null);

  // 1️ Load all conversations on mount
  useEffect(() => {
    fetch(`http://localhost:8080/get-conversation-servlet?yourUserID=${YOUR_USER_ID}`)
      .then(res => res.json())
      .then(data => setConversations(data))
      .catch(err => console.error("Failed to fetch conversations:", err));
  }, []);

  // 2️ Load messages for selected conversation
  useEffect(() => {
    if (!selectedConversation) return;

    fetch(
      `http://localhost:8080/get-messages-servlet?yourUserID=${YOUR_USER_ID}&otherUserID=${selectedConversation.otherUserId}`
    )
      .then(res => res.json())
      .then(data => {
        if (data.messages) {
          setMessages(prev => ({
            ...prev,
            [selectedConversation.otherUserId]: data.messages
          }));
        }
      })
      .catch(err => console.error("Failed to fetch messages:", err));
  }, [selectedConversation]);

  // WebSocket setup for real-time updates
  useEffect(() => {
    const socket = new SockJS('http://localhost:8080/ws-chat');
    const client = new Client({
      webSocketFactory: () => socket,
      onConnect: () => {
        console.log('✅ STOMP connected');
        client.subscribe(`/topic/messages.${YOUR_USER_ID}`, ({ body }) => {
          const msg = JSON.parse(body);
          console.log('📥 Got via WS:', msg);
          const convId = msg.senderId === YOUR_USER_ID
            ? msg.receiverId
            : msg.senderId;
          setMessages(prev => ({
            ...prev,
            [convId]: [
              ...(prev[convId] || []),
              msg
            ]
          }));
        });
      }
    });
    client.activate();
    setStompClient(client);
    return () => client.deactivate();
  }, []);

  // 4️⃣ Send a new message
  const handleSend = () => {
    if (!messageText.trim() || !selectedConversation) return;

    fetch('http://localhost:8080/send-message-servlet', {
      method: 'POST',
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      body: new URLSearchParams({
        yourUserID: YOUR_USER_ID,
        otherUserID: selectedConversation.otherUserId,
        message: messageText.trim()
      })
    })
      .then(res => res.json())
      .then(data => {
        if (data.success) {
          const newMsg = {
            text: messageText.trim(),
            senderId: YOUR_USER_ID,
            receiverId: selectedConversation.otherUserId,
            timestamp: 'Just now'
          };
          // Persist locally for immediate UI update
          setMessages(prev => ({
            ...prev,
            [selectedConversation.otherUserId]: [
              ...(prev[selectedConversation.otherUserId] || []),
              newMsg
            ]
          }));
          // Publish over STOMP for real-time deliver
          if (stompClient) {
            stompClient.publish({
              destination: '/app/send',
              body: JSON.stringify(newMsg)
            });
          }
          setMessageText("");
        } else {
          console.error("Failed to send message:", data.error);
        }
      })
      .catch(err => console.error("Send message failed:", err));
  };

  return (
    <div className="messaging-page">
      <div className="sidebar">
        <ConversationList
          conversations={conversations}
          onSelect={setSelectedConversation}
        />
      </div>

      <div className="chat-area">
        {!selectedConversation ? (
          <div className="empty-state">Select a conversation</div>
        ) : (
          <>
            <div className="chat-header-full">
              <button onClick={() => setSelectedConversation(null)}>⬅</button>
              <h2>{selectedConversation.otherUserName}</h2>
            </div>

            <div className="chat-body-full">
              {/* Render messages for this conversation */}
              {messages[selectedConversation.otherUserId]?.map((msg, idx) => (
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
                onKeyDown={e => e.key === "Enter" && handleSend()}
              />
              <button
                className="send-message-button"
                onClick={handleSend}
                disabled={!messageText.trim()}
              >
                Send
              </button>
            </div>
          </>
        )}
      </div>
    </div>
  );
}
