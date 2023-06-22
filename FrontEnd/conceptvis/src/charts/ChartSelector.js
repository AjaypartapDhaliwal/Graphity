import React from "react";
import TreeMap from "./TreeMap";
import ChordDiagram from "./ChordDiagram";
import SankeyDiagram from "./SankeyDiagram";
import WordCloud from "./WordCloud";
import AmBarChart from "./amBarchart";
import AmScatter from "./amScatterChart";
import AmBubble from "./amBubbleChart";
import AmLineChart from "./amLine";
import '../CSS/Chart.css';

export default function ChartSelector({chart,contents, size}) {

    if (!chart) {
        return (
          <div>
          </div>
        );
    }

    console.log(size);

    if (size === null || size < 1 || isNaN(size)) {
      contents=contents;
    } else {
      if (chart !== 'line') {
        contents=contents.slice(0,size);
      }
    }

    if (!contents) {
      return (
        <div>
          No data
        </div>
      );
    }

    switch(chart){
        case "bar":
            return <AmBarChart contents={contents}/>
        case "line":
            return <AmLineChart data={contents} numberOfSeries={size}/>
        case "bubble":
            return <AmBubble contents={contents}/>;
        case "treemap":
          return <TreeMap contents={contents}/>;
        case "scatter":
            return <AmScatter contents={contents}/>;
        case "chord":
          return <ChordDiagram contents={contents}/>;
        case "sankey":
          return <SankeyDiagram contents={contents}/>;
        case "word":
          return <WordCloud contents={contents}/>;
        default:
          return <div>No Visualisation possible</div>
    }

}