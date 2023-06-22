import React, { useEffect } from 'react';
import * as am4core from '@amcharts/amcharts4/core';
import * as am4charts from '@amcharts/amcharts4/charts';
import am4themes_animated from '@amcharts/amcharts4/themes/animated';
import '../CSS/Chart.css';
am4core.useTheme(am4themes_animated);

function AmBarChart({ contents }) {

    useEffect(() => {

        // gets column names
        const keys = Object.keys(contents[0]);

        // makes chart object
        let chart = am4core.create("chartdiv", am4charts.XYChart);
        chart.hiddenState.properties.opacity = 0;
        
        chart.data = contents;

        // sets axis
        let categoryAxis = chart.xAxes.push(new am4charts.CategoryAxis());
        categoryAxis.dataFields.category = keys[0];
        categoryAxis.title.text = keys[0];

        let valueAxis = chart.yAxes.push(new am4charts.ValueAxis());
        valueAxis.title.text = keys[1];
        let minValue = Math.min(...contents.map(item => item[keys[1]]));
        valueAxis.min = minValue;
        console.log(minValue);

        // allows axis to be viewed on chart
        let series = chart.series.push(new am4charts.ColumnSeries());
        series.name = keys[0] + " by " + keys[1];
        series.columns.template.tooltipText = keys[0] + ": {categoryX}\n" + keys[1] + ": {valueY}";
        series.columns.template.fill = am4core.color("rgba(8, 95, 177, 1)");
        series.columns.template.stroke = am4core.color('rgba(8, 95, 177, 1)');
        series.dataFields.valueY = keys[1];
        series.dataFields.categoryX = keys[0];    

        chart.logo.disabled = true;
        chart.legend = new am4charts.Legend();
        chart.legend.position = "top";

        // adds scrollbars to chart
        chart.scrollbarX = new am4core.Scrollbar();
        chart.scrollbarY = new am4core.Scrollbar();

        return () => {
        // Cleanup on component unmount
        chart.dispose();
        };
    }, [contents]);

    return (
        <div id="chartdiv" className='chart' style={{ width: '100%', height: '85%' }}>
        </div>
    );

}

export default AmBarChart;