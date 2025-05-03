// src/components/ConversationList.jsx

import React from "react";
import "../styles/ConversationList.css";

function ConversationList({ conversations, onSelect }) {
  return (
    <div className="conversation-list">
      {conversations.length === 0 && (
        <p style={{ padding: "1rem", color: "#777" }}>No conversations found.</p>
      )}

      {conversations.map((conv, index) => {
        console.log("Rendering conversation:", conv); // Debug log
        return (
          <div
            key={index}
            className="conversation-item"
            style={{
              border: "1px solid black",         // Debug border
              padding: "8px",
              margin: "4px 0",
              backgroundColor: "white",
              cursor: "pointer"
            }}
            onClick={() => onSelect(conv)}
          >
            <div className="conv-info">
              <div>{conv.otherUserName || `User ${conv.otherUserId}`}</div>
            </div>
          </div>
        );
      })}
    </div>
  );
}

export default ConversationList;
