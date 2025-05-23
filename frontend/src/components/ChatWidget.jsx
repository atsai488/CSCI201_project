// src/components/ChatWidget.jsx

import React, { useState } from "react";
import "../styles/ChatWidget.css";
import ConversationList from "./ConversationList";
import MessageBubble from "./MessageBubble";


const dummyConversations = [
    {
      id: 1,
      otherUserId: 2,
      otherUserName: "LebronFan42069",
      lastMessage: "Cool, let me know when you're free!",
    },
    {
      id: 2,
      otherUserId: 3,
      otherUserName: "Deez",
      lastMessage: "Thanks for the info!",
      
    },
    {
      id: 3,
      otherUserId: 4,
      otherUserName: "Bot1",
      lastMessage: "When can we meet?",
    },
    {
      id: 4,
      otherUserId: 5,
      otherUserName: "Bot2",
      lastMessage: "Is it still available?",
    },
  ];

  const dummyMessages = {
    1: [
      {
        text: "Hey, is this still available?",
        senderId: 1,
        timestamp: "10:30 AM",
      },
      {
        text: "Yes, it is!",
        senderId: 2,
        timestamp: "10:31 AM",
      },
      {
        text: "Cool, let me know when you're free!",
        senderId: 1,
        timestamp: "10:32 AM",
      }
    ],
    2: [
      {
        text: "Appreciate the help!",
        senderId: 3,
        timestamp: "Yesterday",
      },
      {
        text: "No problem, let me know if you have questions.",
        senderId: 1,
        timestamp: "Yesterday",
      }
    ],
    3: [
      {
        text: "Hey! I'm interested in the listing.",
        senderId: 4,
        timestamp: "Today, 9:00 AM",
      },
      {
        text: "?",
        senderId: 4,
        timestamp: "Today, 9:05 AM",
      }
    ],
    4: [
      {
        text: "Is it still available?",
        senderId: 5,
        timestamp: "Today, 11:00 AM",
      }
    ]
  };
  
function ChatWidget() {
	
	
  const [isOpen, setIsOpen] = useState(false);
  const [selectedConversation, setSelectedConversation] = useState(null);
  const [messageText, setMessageText] = useState("");
  const [messages, setMessages] = useState(dummyMessages);
  const YOUR_USER_ID = 1;
  
  
  const handleSend = () => {
        if (!messageText.trim() || !selectedConversation) return;

        const newMsg = {
          text: messageText.trim(),
          senderId: YOUR_USER_ID,
          timestamp: "Just now"
        };
		
        setMessages(prev => ({
          ...prev,
          [selectedConversation.id]: [
            ...(prev[selectedConversation.id] || []),
            newMsg
          ]
        }));
        setMessageText("");
     };
  	

  return (
      <div className="chat-widget">
        <button className="chat-toggle" onClick={() => setIsOpen(!isOpen)}>
          💬
        </button>

        {isOpen && (
          <div className="chat-window">
            <div className="chat-header">
              {!selectedConversation ? (
                <span>Messages</span>
              ) : (
                <>
                  <button onClick={() => setSelectedConversation(null)}>⬅ Back</button>
                  <span>{selectedConversation.listingName}</span>
                </>
              )}
              <button onClick={() => setIsOpen(false)}>X</button>
            </div>

            <div className="chat-body">
              {!selectedConversation ? (
                <ConversationList
                  conversations={dummyConversations}
                  onSelect={(convo) => setSelectedConversation(convo)}
                />
              ) : (
				messages[selectedConversation.id]?.map((msg, idx) => (
				    <MessageBubble
				      key={idx}
				      message={msg}
				      isOwnMessage={msg.senderId === YOUR_USER_ID}
				    />
				  ))
              )}
            </div>

            {selectedConversation && (
              <div className="chat-input">
                <input
                  type="text"
                  placeholder="Type your message…"
                  value={messageText}
                  onChange={(e) => setMessageText(e.target.value)}
                  onKeyDown={(e) => e.key === "Enter" && handleSend()}
                />
                <button className="send-message-button" onClick={handleSend} disabled={!messageText.trim()}>
                  Send
                </button>
              </div>
            )}
          </div>
        )}
      </div>
    );
}

export default ChatWidget;

