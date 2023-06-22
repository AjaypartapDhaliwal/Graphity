import React, { useEffect } from 'react';
import * as am4core from '@amcharts/amcharts4/core';
import * as am4charts from '@amcharts/amcharts4/charts';
import am4themes_animated from '@amcharts/amcharts4/themes/animated';
import '../CSS/Chart.css';

am4core.useTheme(am4themes_animated);

function ChordDiagram({contents}) {
 
  useEffect(() => {
    // Create chart instance
    let chart = am4core.create('chartdiv', am4charts.ChordDiagram);
    const keys = Object.keys(contents[0]);

    // Set data
    chart.data = contents;

    // Set data fields
    chart.dataFields.fromName = [keys[0]];
    chart.dataFields.toName = [keys[1]];
    chart.dataFields.value = [keys[2]];

    chart.sortBy = [keys[0]];

    // makes each chord
    let slice = chart.nodes.template.slice;
    slice.stroke = am4core.color("#000");
    slice.strokeOpacity = `1`;
    slice.strokeWidth = 0;
    slice.cornerRadius = 50;
    slice.innerCornerRadius = 0;

    let link = chart.links.template;
    link.fillOpacity = 0.8;

    chart.legend = new am4charts.Legend();
    chart.legend.position = "left";
    chart.legend.scrollable = true;
    chart.legend.useDefaultMarker = true;
    let marker = chart.legend.markers.template.children.getIndex(0);
    marker.cornerRadius(12, 12, 12, 12);

    let title = chart.titles.create();
    title.text = keys[0] + " vs " + keys[1] + " based on " + keys[2];
    title.fontSize = 15;
    title.fontWeight = "bold";
    title.paddingTop = 15;
    title.paddingLeft = 110;
    title.marginBottom = -10;

    chart.logo.disabled = true;

    return () => {
      // Cleanup on component unmount
      chart.dispose();
    };
  }, [contents]);

  return <div id="chartdiv" className='chart' style={{ width: '100%', height: '90%' }}></div>;
}

export default ChordDiagram;
