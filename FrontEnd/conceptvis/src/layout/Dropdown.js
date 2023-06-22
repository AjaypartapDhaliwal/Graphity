import React, { useState } from 'react';
import "../CSS/Dropdown.css"

// creates dropdown button for filters
const Dropdown = ({ options, setFilterValues, filterInputBoxValues, setFilterInputBoxValues}) => {

  const [isOpen, setIsOpen] = useState(false);

  const toggleDropdown = () => {
    setIsOpen(!isOpen);
  };

  // handles input values for filters
  const handleInputChange = (e, option) => {
    const { value } = e.target;
    setFilterInputBoxValues((prevInputValues) => {
      let updatedInputValues = [...prevInputValues];

      if (value === '') {
        updatedInputValues = updatedInputValues.filter((input) => input.option !== option);
      } else {
        const existingInput = updatedInputValues.find((input) => input.option === option);

        if (existingInput) {
          existingInput.value = value;
        } else {
          updatedInputValues.push({ option, value });
        }
      }

      return updatedInputValues;
    });
    setFilterValues(filterInputBoxValues);
  };

  const handleReset = () => {
    setFilterInputBoxValues([]);
    setFilterValues([]);
  };

  // generates input boxes for filters
  return (
    <div className="dropdown">
      <button className="dropdown-button" onClick={toggleDropdown}>
        Filters
      </button>
      {isOpen && (
        <div className="dropdown-content">
          {options.map((option, index) => {
            const inputValue = (filterInputBoxValues.find((input) => input.option === option) || {}).value || '';

            return (
              <div key={index} className="filter-row">
                <label className="filter-label">{option}</label>
                <div className="filter-inputs">
                  <input
                    type="text"
                    className="filter-input"
                    value={inputValue}
                    onChange={(e) => handleInputChange(e, option)}
                  />
                </div>
              </div>
            );
          })}
          <div className="button-row">
            <button className="filter-button" onClick={handleReset}>Reset</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Dropdown;