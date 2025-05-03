// src/pages/MessagingPage.jsx
import React, { useEffect, useState } from 'react';
import '../styles/MessagingPage.css';
import ConversationList from '../components/ConversationList';
import MessageBubble from '../components/MessageBubble';

export default function MessagingPage() {
  const YOUR_USER_ID = 1;

  const [conversations, setConversations] = useState([]);
  const [messages, setMessages] = useState({});
  const [selectedConversation, setSelectedConversation] = useState(null);
  const [messageText, setMessageText] = useState("");

  useEffect(() => {
    fetch(`/get-conversation-servlet?yourUserID=${YOUR_USER_ID}`)
      .then(res => res.json())
      .then(data => setConversations(data))
      .catch(err => console.error("Failed to fetch conversations:", err));
  }, []);
  
  const handleSend = () => {
      if (!messageText.trim() || !selectedConversation) return;

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

               {/* ← add this input bar */}
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

