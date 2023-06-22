import React, { useState } from "react";
import ChartSelector from "../charts/ChartSelector";
import ShowMe from "./ShowMe";
import AxisInputs from "./AxisInputs";
import Dropdown from "./Dropdown";
import SuggestionList from '../layout/ChoiceButtons';
import "../CSS/Tables.css";

export default function Tables({ names_and_columns }) {

  const [inputValues, setInputValues] = useState([]);
  const [data, setData] = useState([]);
  const [activeTables, setActiveTables] = useState([]);
  const [types, setTypes] = useState([]);
  const [filterValues, setFilterValues] = useState([]);
  const [filterInputBoxValues, setFilterInputBoxValues] = useState([]);
  const [n, setN] = useState(20);
  const [clickedButton, setClickedButton] = useState(null);
  const [charts, setCharts] = useState([]);

  const handleChange = (event) => {
    const value = parseInt(event.target.value);
    setN(value);
  };

  // stores all tables which have names and columns to be displayed
  const tables = [];

  for (let i = 0; i < names_and_columns.length; i++) {
    const columns = [];
    for (let j = 0; j < names_and_columns[i][1].length; j++) {
      columns.push(
        // creates button for each column in each tables
        <li className="button-item" key={j}>
          <button
            onClick={() => {
              const value = names_and_columns[i][1][j][0];
              const table = names_and_columns[i][0];
              const type = names_and_columns[i][1][j][1];

              if (inputValues.length !==  new Set([...inputValues, value])) {
                setTypes([...types, type]);
              }
              setInputValues([...new Set([...inputValues, value])]);
              setActiveTables([...activeTables, table]);

            }}
            className="column-button"
          >
          <span>
            {names_and_columns[i][1][j][0]}
          </span>
          </button>
        </li>
      );
    }

    tables.push(
      <div className="table-contents" key={i}>
        <h2 className="table-names">{names_and_columns[i][0]}</h2>
        <ul>{columns}</ul>
      </div>
    );
  }
  
  // creates scrollable which lets you access buttons for all columns
  return (
    <div className="tables-container">
      <div className="tables-flexbox">


        {/* section that shows all tables and columns */}
        <div>
          <div className="sub-header-t sub-header">
            tables
          </div>
          <div className="table">
            {tables}
          </div>
        </div>

        {/* section shows all chart suggestions */}
        <div>
          <div className="sub-header-cc sub-header">
            chart choices
          </div>
          <div className="chart-choices">
            <SuggestionList 
              names={charts}
              clickedButton={clickedButton}
              setClickedButton={setClickedButton}
            />
          </div>
        </div>

        {/* shows section to inputs/filters and charts*/}
        <div className="entry">
          <div className="sub-header-sc sub-header">
            show chart
          </div>
            <div className="sc-flex">
              <AxisInputs
                inputValues={inputValues}
                setInputValues={setInputValues}
                activeTables={activeTables}
                setActiveTables={setActiveTables}
                types={types}
                setTypes={setTypes}
              />
              <ShowMe
                inputs={inputValues}
                setData={setData}
                tables={activeTables}
                filterValues={filterValues}
                setClickedButton={setClickedButton}
                setCharts={setCharts}
                setFilterInputBoxValues={setFilterInputBoxValues}
              />
            </div>
            <div className="dropdown-container">
              <Dropdown
                    options={inputValues}
                    setFilterValues={setFilterValues}
                    filterInputBoxValues={filterInputBoxValues}
                    setFilterInputBoxValues={setFilterInputBoxValues}
                  />
              <label className="item-counter-container">Number of items</label>
              <input className="item-counter-input" type="number" value={n} onChange={handleChange} />
            </div>
            <ChartSelector className="chart-container"
              chart={clickedButton}
              contents={data.contents}
              size={n}
            />
        </div>
        </div>
    </div>
  );
}
  