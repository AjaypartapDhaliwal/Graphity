import React, { useState } from "react";
import "../CSS/ShowMe.css";

export default function ShowMe({ inputs, setData, tables, filterValues, setClickedButton, setCharts, setFilterInputBoxValues}) {

  // Tracks state of error message
  const [notification, setNotification] = useState(null);
  const [showNotification, setShowNotification] = useState(false);

  // handles clicking of show me button
  const handleClick = async () => {

    setClickedButton(null);
    setData([]);

    // Check if only one table has been used
    if ((new Set(tables).size > 1)) {
      setNotification("Please select columns from the same table");
      setShowNotification(true);
      return;
    }

    try {
      // Remove error message
      setNotification("")
      setShowNotification(false);

      // Send chosen columns and table to backend
      const response = await fetch("http://localhost:8080/api/v1/table/mapping", {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ inputs: inputs, table: [...new Set(tables)], filters: filterValues })
      });

      // Get table data based on chosen columns
      const data = await response.json();
      setData(data);
      setCharts(data.chart);
      setFilterInputBoxValues([]);

    } catch (error) {
      console.error(error);
    }
  };

  // Closes the error message
  const closeNotification = () => {
    setShowNotification(false);
  };

  const isDisabled = !inputs;

  // Displays show me button and error message
  return (
    <div className="show-me-container">
      <button className="show-me" onClick={handleClick} disabled={isDisabled}>
        Show Me
      </button>
      {showNotification && (
        <div className="speech-bubble-container">
          <div className="speech-bubble">
            {notification}
            <button className="close-button" onClick={closeNotification}>
              x
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
