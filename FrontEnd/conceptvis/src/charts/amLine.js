import React, { useEffect } from 'react';
import * as am4core from '@amcharts/amcharts4/core';
import * as am4charts from '@amcharts/amcharts4/charts';
import am4themes_animated from '@amcharts/amcharts4/themes/animated';

am4core.useTheme(am4themes_animated);

function AmLineChart({ data, numberOfSeries }) {
  useEffect(() => {
    // Create chart instance
    let chart = am4core.create('chartdiv', am4charts.XYChart);
    const keys = Object.keys(data[0]);

    //Order data
    chart.data = data;
    data.sort((a, b) => a.year - b.year);

    data.forEach(item => {
        item.year = item.year.toString();
      });

    // Create axes
    let categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
    categoryAxis.dataFields.category = keys[1];
    categoryAxis.title.text = keys[1];

    let valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
    valueAxis.title.text = keys[2];

    // Create series for each country
    let series = {};

    data.forEach(countryData => {
      let country = countryData[keys[0]];
      if (!series[country]) {
        series[country] = new am4charts.LineSeries();
        series[country].name = country;
        series[country].dataFields.valueY = keys[2];
        series[country].dataFields.categoryX = keys[1];
        series[country].tooltipText = '{name}: [bold]{valueY}[/]';
      }
      series[country].data.push(countryData);
    });

    // Push desired number of series onto the chart
    let seriesCount = 0;
    for (const country in series) {
      if (seriesCount >= 10) {
        break;
      }
      chart.series.push(series[country]);
      seriesCount++;
    }

    // Add chart cursor
    chart.cursor = new am4charts.XYCursor();

    // Add scrollbar to x axis
    chart.scrollbarX = new am4core.Scrollbar();

    // Add scrollbar to y axis
    chart.scrollbarY = new am4core.Scrollbar();

    chart.logo.disabled = true;
    chart.legend = new am4charts.Legend();
    chart.legend.position = "top";

    // Destroy chart on component unmount
    return () => {
      chart.dispose();
    };
  }, [data, numberOfSeries]);

  return <div id="chartdiv" style={{ width: '100%', height: '80%' }}></div>;
};

export default AmLineChart;
