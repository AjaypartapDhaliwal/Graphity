import React, { useState } from 'react';
import '../CSS/Login.css';

// Creates login screen
function Login({ onLogin }) {
  const [url, setUrl] = useState('');
  const [database, setDatabase] = useState('');
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const handleSubmit = (e) => {
    e.preventDefault();

    // Call the onLogin callback with the login details
    onLogin({ url, database, username, password });

    // Clear the input fields
    setUrl('');
    setDatabase('');
    setUsername('');
    setPassword('');
  };

  return (
    <div class="wrapper">
      <div className="login-container">
        <form className="login-form" onSubmit={handleSubmit}>
        <h2 className='title'>Connect</h2>
          <div>
            <label htmlFor="url">URL:</label>
            <input
              type="text"
              id="url"
              value={url}
              onChange={(e) => setUrl(e.target.value)}
              required
            />
          </div>
          <div>
            <label htmlFor="database">Database:</label>
            <input
              type="text"
              id="database"
              value={database}
              onChange={(e) => setDatabase(e.target.value)}
              required
            />
          </div>
          <div>
            <label htmlFor="username">Username:</label>
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              required
            />
          </div>
          <div>
            <label htmlFor="password">Password:</label>
            <input
              type="password"
              id="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              required
            />
          </div>
          <button type="submit">Connect</button>
        </form>
      </div>

      <ul class="bg-bubbles">
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
        <li></li>
      </ul>
    </div>
  );
}

export default Login;
