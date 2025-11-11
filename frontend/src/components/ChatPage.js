import React, { useState, useEffect, useRef } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "../styles/ChatPage.css";

function ChatPage() {
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState("");
  const chatMessagesRef = useRef(null);
  const navigate = useNavigate();

  const email = localStorage.getItem("email");
  const token = localStorage.getItem("token");

  // Load previous chat history
  useEffect(() => {
    if (!email) return;

    axios
      .get(`http://localhost:8080/api/chat/history?email=${email}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        const previousMessages = res.data.map((item) => [
          { role: "user", content: item.userMessage },
          { role: "assistant", content: item.aiResponse },
        ]).flat();
        setMessages(previousMessages);
      })
      .catch((err) => {
        console.error("Failed to load chat history:", err);
      });
  }, [email,token]);

  // Send message
  const handleSend = async () => {
    if (!input.trim()) return;

    const newMessage = { role: "user", content: input };
    setMessages((prev) => [...prev, newMessage]);
    setInput("");

    try {
      const response = await axios.post(
        "http://localhost:8080/api/chat/message",
        { userId: email, message: input },
        { headers: { Authorization: `Bearer ${token}` } }
      );

      const aiReply = {
        role: "assistant",
        content: response.data.response || "(No response received)",
      };

      setMessages((prev) => [...prev, aiReply]);
    } catch (error) {
      console.error("Chat error:", error);
      setMessages((prev) => [
        ...prev,
        { role: "assistant", content: "Something went wrong. Try again." },
      ]);
    }
  };

  // Auto-scroll to bottom
  useEffect(() => {
    if (chatMessagesRef.current) {
      chatMessagesRef.current.scrollTop = chatMessagesRef.current.scrollHeight;
    }
  }, [messages]);

  // Logout function
  const handleLogout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("email");
    navigate("/login");
  };

  // Clear chat history
  const handleClearChat = async () => {
    if (!window.confirm("Are you sure you want to clear your chat history?")) return;
    try {
      await axios.delete(`http://localhost:8080/api/chat/clear/${email}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setMessages([]);
      alert("Chat history cleared successfully!");
    } catch (error) {
      console.error("Error clearing chat:", error);
      alert("Failed to clear chat history. Please try again.");
    }
  };

  return (
    <div className="chat-wrapper">
      <div className="chat-container">
        <div className="chat-header">
          <span>Netflix AI Chatbot</span>
          <div className="chat-header-buttons">
            <button className="header-btn clear-btn" onClick={handleClearChat}>
              Clear Chat
            </button>
            <button className="header-btn logout-btn" onClick={handleLogout}>
              Logout
            </button>
          </div>
        </div>

        <div className="chat-messages" ref={chatMessagesRef}>
          {messages.map((msg, index) => (
            <div
              key={index}
              className={`chat-message ${
                msg.role === "user" ? "user-msg" : "ai-msg"
              }`}
            >
              <div className="message-bubble">
                <strong>{msg.role === "user" ? "You" : "AI"}:</strong>{" "}
                {msg.content}
              </div>
            </div>
          ))}
        </div>

        <div className="input-area">
          <input
            type="text"
            placeholder="Ask about movies..."
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSend()}
          />
          <button onClick={handleSend}>Send</button>
        </div>
      </div>
    </div>
  );
}

export default ChatPage;
