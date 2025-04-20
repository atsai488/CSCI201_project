// src/AppLayout.jsx
import React from "react";
import { Outlet, useLocation } from "react-router-dom";
import ChatWidget from "./components/ChatWidget";

export default function AppLayout() {
  const location = useLocation();

  return (
    <>
      {/* only show on routes other than /messages */}
      {location.pathname !== "/messages" && <ChatWidget />}

      <main>
        <Outlet />
      </main>
    </>
  );
}
