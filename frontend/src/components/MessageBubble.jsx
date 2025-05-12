import React from "react";
import "../styles/MessageBubble.css";

function MessageBubble({ message, isOwnMessage }) {
  return (
    <div className={`message-bubble ${isOwnMessage ? "own" : "other"}`}>
      <div className="message-content">
        <p className="message-text">{message.text}</p>
        <span className={`"timestamp" ${isOwnMessage ? "own-timestamp" : "other-timestamp"}`}>{message.timestamp}</span>
      </div>
    </div>
  );
}

export default MessageBubble;
