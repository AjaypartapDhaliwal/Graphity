import React from 'react';
import '../CSS/ChoiceButton.css'

// creates buttons for chart suggestions
const SuggestionList = ({ names, clickedButton, setClickedButton }) => {

    if (!names) {
        return <div></div>
    }

    const handleClick = (name) => {
      setClickedButton(name);
    };
  
    const resetButtons = () => {
      setClickedButton(null);
    };
  
    return (
      <div className="button-list">
        <div className="button-container">
          {names.map((name, index) => (
            <button
              key={index}
              onClick={() => handleClick(name)}
              className={`list-button ${clickedButton === name ? 'active' : ''}`}
            >
              {name}
            </button>
          ))}
        </div>
        <button className="reset-button" onClick={resetButtons}>
          Reset
        </button>
      </div>
    );
  };
  
  export default SuggestionList;