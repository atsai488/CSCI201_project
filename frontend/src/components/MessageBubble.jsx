import React from "react";
import "../styles/MessageBubble.css";

function MessageBubble({ message, isOwnMessage }) {
  const displayTimestamp = message.formattedTimestamp || message.timestamp;
  return (
    <div className={`message-bubble ${isOwnMessage ? "own" : "other"}`}>
      <div className="message-content">
        <p className="message-text">{message.text}</p>
        <span className={`timestamp ${isOwnMessage ? "own-timestamp" : "other-timestamp"}`}>{displayTimestamp}</span>
      </div>
    </div>
  );
}

export default MessageBubble;
