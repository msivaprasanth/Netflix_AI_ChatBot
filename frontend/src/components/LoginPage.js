import React, { useState } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "../styles/Auth.css";

function LoginPage() {
  const navigate = useNavigate();
  const [credentials, setCredentials] = useState({ email: "", password: "" });
  const [forgotMode, setForgotMode] = useState(false);
  const [emailForReset, setEmailForReset] = useState("");
  const [loading, setLoading] = useState(false);


  const handleChange = (e) => {
    setCredentials({ ...credentials, [e.target.name]: e.target.value });
  };

  // Handle Login Submit
  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try {
      const response = await axios.post("http://localhost:8080/api/auth/login", credentials);
      localStorage.setItem("token", response.data.token);
      localStorage.setItem("email", credentials.email);
      alert("Login successful!");
      navigate("/chat");
    } catch (error) {
      if (error.response?.data?.error?.includes("not registered")) {
        alert("User not registered. Please sign up first.");
      } else if (error.response?.data?.error?.includes("Invalid")) {
        alert("Invalid email or password. Try again.");
      } else {
        alert("Server error. Please try again later.");
      }
    }
  };

  // Forgot Password Handler
  const handleForgotPassword = async () => {
    if (!emailForReset.trim()) {
      alert("Please enter your registered email.");
      return;
    }

    try {
      const res = await axios.post("http://localhost:8080/api/auth/forgot-password", {
        email: emailForReset,
      });
      alert(res.data.message);
      setForgotMode(false);
    } catch (error) {
      alert(error.response?.data?.error || "⚠️ Something went wrong.");
    }
  };

  return (
    <div className="auth-container">
      <h2>{forgotMode ? "Forgot Password" : "Login"}</h2>

      {!forgotMode ? (
        <form onSubmit={handleSubmit}>
          <input
            name="email"
            type="email"
            placeholder="Email"
            value={credentials.email}
            onChange={handleChange}
            required
          />
          <input
            name="password"
            type="password"
            placeholder="Password"
            value={credentials.password}
            onChange={handleChange}
            required
          />
          <button type="submit" disabled={loading}>
            {loading ? "Logging in..." : "Login"}
          </button>


          <p
            className="switch-link"
            style={{ marginTop: "10px", cursor: "pointer" }}
            onClick={() => setForgotMode(true)}
          >
            Forgot Password?
          </p>
        </form>
      ) : (
        <>
          <input
            type="email"
            placeholder="Enter your registered email"
            value={emailForReset}
            onChange={(e) => setEmailForReset(e.target.value)}
          />
          <button onClick={handleForgotPassword}>Send Reset Link</button>
          <p
            className="switch-link"
            style={{ marginTop: "15px", cursor: "pointer" }}
            onClick={() => setForgotMode(false)}
          >
            ← Back to Login
          </p>
        </>
      )}

      <p className="switch-text">
        {forgotMode ? "" : (
          <>
            Don’t have an account?{" "}
            <span
              className="switch-link"
              onClick={() => navigate("/register")}
            >
              Register here
            </span>
          </>
        )}
      </p>
    </div>
  );
}

export default LoginPage;
