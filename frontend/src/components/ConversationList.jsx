// src/components/ConversationList.jsx

import React from "react";
import "../styles/ConversationList.css";

function ConversationList({ conversations, onSelect})
{
	return (
		<div className="conversation-list">
			{conversations.map((conv, index) => (
				<div
		          key={index}
		          className={`conversation-item ${conv.unread ? "unread" : ""}`}
		          onClick={() => onSelect(conv)}
		        >
					<img
			            src={conv.listingThumbnail}
			            alt="thumbnail"
			            className="conv-thumbnail"
			         />
					 <div className="conv-info">
					 	<div className="">{conv.listingName}</div>
						<div className="conv-last-message">{conv.lastMessage}</div>
					 </div>
				</div>
			))}
		</div>
	);
}

export default ConversationList;