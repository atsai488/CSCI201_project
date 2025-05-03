// src/pages/MessagingPage.jsx
import React, { useEffect, useState } from 'react';
import '../styles/MessagingPage.css';
import ConversationList from '../components/ConversationList';
import MessageBubble from '../components/MessageBubble';

export default function MessagingPage() {
  const YOUR_USER_ID = 1;

  const [conversations, setConversations] = useState([]);
  const [messages, setMessages] = useState({}); // Changed to object
  const [selectedConversation, setSelectedConversation] = useState(null);
  const [messageText, setMessageText] = useState("");

  // Load all conversations on mount
  useEffect(() => {
    fetch(`http://localhost:8080/get-conversation-servlet?yourUserID=${YOUR_USER_ID}`)
      .then(res => res.json())
      .then(data => setConversations(data))
      .catch(err => console.error("Failed to fetch conversations:", err));
  }, []);

  // Load messages for selected conversation
  useEffect(() => {
    if (!selectedConversation) return;

    fetch(`/get-messages-servlet?yourUserID=${YOUR_USER_ID}&otherUserID=${selectedConversation.otherUserId}`)
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

  // Send message to backend
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
          timestamp: "Just now"
        };

        setMessages(prev => ({
          ...prev,
          [selectedConversation.otherUserId]: [
            ...(prev[selectedConversation.otherUserId] || []),
            newMsg
          ]
        }));
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
