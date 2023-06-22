import React from "react";
import "../CSS/Axis.css";

// creates input box for user to input columns
export default function Inputs({ inputValues, setInputValues, activeTables, setActiveTables, types, setTypes}) {
    
    return (
      <div className="axis-container">
        <div className="axis-button-flex">
          <input className="input-box"
            type="text"
            id="xaxis"
            name="xaxis"
            value={inputValues}
            onKeyDown={(e) => e.preventDefault()}
            disabled
          />
          <button className="axis-button" onClick={() => {
              setInputValues(inputValues.slice(0, -1));
              setActiveTables(activeTables.slice(0, -1));
              setTypes(types.slice(0, -1));
            }}>remove</button>
          <button className="axis-button clear-button" onClick={() => {
              setInputValues([]);
              setActiveTables([]);
              setTypes([]);
            }}>clear</button>
        </div>
      </div>
    );
  }