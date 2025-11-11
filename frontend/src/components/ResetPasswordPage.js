import React, { useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import axios from "axios";
import "../styles/Auth.css";

function ResetPasswordPage() {
  const location = useLocation();
  const navigate = useNavigate();
  const queryParams = new URLSearchParams(location.search);
  const email = queryParams.get("email");

  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const handleReset = async () => {
    if (!password || !confirmPassword) {
      alert("Please fill out both fields.");
      return;
    }
    if (password !== confirmPassword) {
      alert("Passwords do not match. Try again.");
      return;
    }

    setLoading(true);
    try {
      const res = await axios.post("http://localhost:8080/api/auth/reset-password", {
        email,
        newPassword: password,
      });
      alert(res.data.message);
      navigate("/login");
    } catch (error) {
      alert(error.response?.data?.error || "Failed to reset password. Try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <h2>Reset Password</h2>
      <p style={{ textAlign: "center", color: "#555", marginBottom: "20px" }}>
        for <strong>{email}</strong>
      </p>

      <input
        type="password"
        placeholder="Enter new password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />
      <input
        type="password"
        placeholder="Confirm new password"
        value={confirmPassword}
        onChange={(e) => setConfirmPassword(e.target.value)}
      />

      <button onClick={handleReset} disabled={loading}>
        {loading ? "Updating..." : "Confirm Reset"}
      </button>

      <p className="switch-text">
        <span
          className="switch-link"
          onClick={() => navigate("/login")}
        >
          ← Back to Login
        </span>
      </p>
    </div>
  );
}

export default ResetPasswordPage;
