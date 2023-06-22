import React, { useState, useEffect } from 'react';
import Tables from './layout/Tables';
import Header from './layout/Header';
import Login from './layout/Login';
import LoadingScreen from './layout/LoadingScreen';
import "./CSS/App.css";

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [data, setData] = useState([]);

  // if login succesful fetch data
  useEffect(() => {
    if (isLoggedIn) {
      fetchData();
    }
  }, [isLoggedIn]);

  const handleLogin = async (loginDetails) => {
    // Make a POST request to the login endpoint
    try {
      const response = await fetch('http://localhost:8080/api/v1/table/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(loginDetails),
      });

      if (response.ok) {
        setIsLoggedIn(true);
      } else {
        // Handle login error
        console.error('Login failed');
      }
    } catch (error) {
      // Handle network error
      console.error('Network error:', error);
    }
  };

  const fetchData = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/v1/table/names_and_columns');
      const json = await response.json();
      setData(json);
    } catch (error) {
      // Handle fetch error
      console.error('Error fetching data:', error);
    }
  };

  // if not logged in keep home screen as login screen
  if (!isLoggedIn) {
    return (
      <div className="container">
        <Header isLoggedIn={isLoggedIn}/>
        <Login onLogin={handleLogin} />
      </div>
    );
  }

  // display loading screen while fetching data
  if (data.length === 0) {
    return <LoadingScreen></LoadingScreen>;
  }

  /* 
    stores data on tables names, column names, and column data such as type
    and purpose.
  */
  const tableNamesAndColumns = [];
  data.map((table) => {
    const tableName = Object.keys(table)[0];
    const columns = table[tableName];
    const columnsList = []
    Object.keys(columns).forEach((key) => {
      const column = columns[key];
      const name = column.name;
      const type = column.type;
      const purpose = column.purpose;
      const columnList = [name,type,purpose];
      columnsList.push(columnList);
    });
    tableNamesAndColumns.push([tableName, columnsList]);
  });

  return (
    <div className="container">
        <Header isLoggedIn={isLoggedIn} />
        <Tables names_and_columns={tableNamesAndColumns} />
    </div>
  );
}

export default App;