import React, { useEffect } from 'react';
import * as am4core from '@amcharts/amcharts4/core';
import * as am4charts from '@amcharts/amcharts4/charts';
import am4themes_animated from '@amcharts/amcharts4/themes/animated';
import '../CSS/Chart.css';

am4core.useTheme(am4themes_animated);

function ChordDiagram({contents}) {
  useEffect(() => {
    // Create chart instance
    let chart = am4core.create('chartdiv', am4charts.SankeyDiagram);
    const keys = Object.keys(contents[0]);

    // Set data
    chart.data = contents;

    // Set data fields
    chart.dataFields.fromName = [keys[0]];
    chart.dataFields.toName = [keys[1]];
    chart.dataFields.value = [keys[2]];
    chart.logo.disabled = true;

    let title = chart.titles.create();
    title.text = keys[0] + " vs " + keys[1] + " based on " + keys[2];
    title.fontSize = 15;
    title.paddingTop = 10;
    title.paddingLeft = 110;

    // chart.legend = new am4charts.Legend();
    // chart.legend.position = "left";
    // chart.legend.scrollable = true;
    // chart.legend.useDefaultMarker = true;
    // let marker = chart.legend.markers.template.children.getIndex(0);
    // marker.cornerRadius(12, 12, 12, 12);

    return () => {
      // Cleanup on component unmount
      chart.dispose();
    };
  }, [contents]);

  return <div id="chartdiv" className='chart' style={{ width: '95%', height: '85%' }}></div>;
}

export default ChordDiagram;
