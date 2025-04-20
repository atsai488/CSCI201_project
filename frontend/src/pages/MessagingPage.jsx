// src/pages/MessagingPage.jsx
import React, { useState } from 'react';
import '../styles/MessagingPage.css';
import ConversationList from '../components/ConversationList';
import MessageBubble from '../components/MessageBubble';

const dummyConversations = [
    {
      id: 1,
      otherUserId: 2,
      otherUserName: "LebronFan42069",
      listingName: "Jordan 4 Black Cat",
      listingThumbnail: "https://i.ebayimg.com/images/g/QwIAAOSwANJnBROf/s-l1600.webp",
      lastMessage: "Cool, let me know when you're free!",
      unread: true
    },
    {
      id: 2,
      otherUserId: 3,
      otherUserName: "Deez",
      listingName: "Essential Calculus",
      listingThumbnail: "https://i.ebayimg.com/images/g/k8UAAOSw3PNmKU9T/s-l1600.webp",
      lastMessage: "Thanks for the info!",
      unread: false
    },
    {
      id: 3,
      otherUserId: 4,
      otherUserName: "Bot1",
      listingName: "Cactus Jack USC hoodie",
      listingThumbnail: "https://i.ebayimg.com/images/g/XKgAAOSwnq9n6I6n/s-l1600.webp",
      lastMessage: "When can we meet?",
      unread: true
    },
    {
      id: 4,
      otherUserId: 5,
      otherUserName: "Bot2",
      listingName: "Iphone 15",
      listingThumbnail: "https://i.ebayimg.com/images/g/sY4AAOSwe01oADTe/s-l500.webp",
      lastMessage: "Is it still available?",
      unread: false
    },
  ];

const dummyMessages = {
	1: [
	     {
	       text: "Hey, is this still available?",
	       senderId: 1,
	       timestamp: "10:30 AM",
	       senderProfilePic: "https://i.pravatar.cc/40?img=1"
	     },
	     {
	       text: "Yes, it is!",
	       senderId: 2,
	       timestamp: "10:31 AM",
	       senderProfilePic: "https://i.pravatar.cc/40?img=3"
	     },
	     {
	       text: "Cool, let me know when you're free!",
	       senderId: 1,
	       timestamp: "10:32 AM",
	       senderProfilePic: "https://i.pravatar.cc/40?img=1"
	     }
	   ],
	   2: [
	     {
	       text: "Appreciate the help!",
	       senderId: 3,
	       timestamp: "Yesterday",
	       senderProfilePic: "https://i.pravatar.cc/40?img=4"
	     },
	     {
	       text: "No problem, let me know if you have questions.",
	       senderId: 1,
	       timestamp: "Yesterday",
	       senderProfilePic: "https://i.pravatar.cc/40?img=1"
	     }
	   ],
	   3: [
	     {
	       text: "Hey! I'm interested in the listing.",
	       senderId: 4,
	       timestamp: "Today, 9:00 AM",
	       senderProfilePic: "https://i.pravatar.cc/40?img=5"
	     },
	     {
	       text: "?",
	       senderId: 4,
	       timestamp: "Today, 9:05 AM",
	       senderProfilePic: "https://i.pravatar.cc/40?img=5"
	     }
	   ],
	   4: [
	     {
	       text: "Is it still available?",
	       senderId: 5,
	       timestamp: "Today, 11:00 AM",
	       senderProfilePic: "https://i.pravatar.cc/40?img=6"
	     }
	   ]
};

export default function MessagingPage() {
  const YOUR_USER_ID = 1;
  const [selectedConversation, setSelectedConversation] = useState(null);
  const [messages, setMessages]             = useState(dummyMessages);
  const [messageText, setMessageText]       = useState("");

  const handleSend = () => {
      if (!messageText.trim() || !selectedConversation) return;

      const newMsg = {
        text: messageText.trim(),
        senderId: YOUR_USER_ID,
        timestamp: "Just now",
        senderProfilePic: "https://i.pravatar.cc/40?img=1"
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
       <div className="messaging-page">
         <div className="sidebar">
           <ConversationList
             conversations={dummyConversations.map(conv => ({
               ...conv,
               otherUserName: conv.listingName
             }))}
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
                 <h2>{selectedConversation.listingName}</h2>
               </div>

               <div className="chat-body-full">
                 {messages[selectedConversation.id]?.map((msg, idx) => (
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

