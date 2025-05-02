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
		          className={`conversation-item`}
		          onClick={() => onSelect(conv)}
		        >
					 <div className="conv-info">
						<div className="">{conv.otherUserName}</div>
					 </div>
				</div>
			))}
		</div>
	);
}

export default ConversationList;