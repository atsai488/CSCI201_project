// src/App.js
import React from "react";
import { createBrowserRouter, RouterProvider } from "react-router-dom";
import AppLayout     from "./AppLayout";
import Home          from "./pages/Home";
import MessagingPage from "./pages/MessagingPage";

const router = createBrowserRouter([
  {
    element: <AppLayout />,
    children: [
      { path: "/",        element: <Home /> },
      { path: "messages", element: <MessagingPage /> },
    ],
  },
]);

export default function App() {
  return <RouterProvider router={router} />;
}

