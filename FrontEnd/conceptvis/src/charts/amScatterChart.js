import React, { useEffect } from 'react';
import * as am4core from '@amcharts/amcharts4/core';
import * as am4charts from '@amcharts/amcharts4/charts';
import am4themes_animated from '@amcharts/amcharts4/themes/animated';
import '../CSS/Chart.css';
am4core.useTheme(am4themes_animated);

function AmScatter({ contents }) {

    useEffect(() => {

        // gets column names
        const keys = Object.keys(contents[0]);

        // makes chart object
        let chart = am4core.create("chartdiv", am4charts.XYChart);
        chart.data = contents;

        // sets axis
        let valueAxisX = chart.xAxes.push(new am4charts.ValueAxis());
        valueAxisX.title.text = keys[1];
        let minxValue = Math.min(...contents.map(item => item[keys[1]]));
        valueAxisX.min = minxValue;

        let valueAxisY = chart.yAxes.push(new am4charts.ValueAxis());
        valueAxisY.title.text = keys[2];
        let minyValue = Math.min(...contents.map(item => item[keys[2]]));
        valueAxisY.min = minyValue;

        // allows axis to be viewed on chart
        let series = chart.series.push(new am4charts.LineSeries());
        series.name = keys[1] + " by " + keys[2];
        series.strokeOpacity = 0;
        series.dataFields.valueX = keys[1];
        series.dataFields.valueY = keys[2];

        // makes plots for each point
        let bullet = series.bullets.push(new am4core.Circle());
        bullet.fill = am4core.color("rgba(8, 95, 177, 1)");
        bullet.propertyFields.fill = "rgba(8, 95, 177, 1)";
        bullet.strokeOpacity = 0;
        bullet.radius = 5;
        bullet.strokeWidth = 2;
        bullet.fillOpacity = 0.7;
        bullet.stroke = am4core.color("rgba(8, 95, 177, 1)");

        bullet.tooltipText = keys[0] + `: {${keys[0]}}\n` + keys[1] + ": {valueX}\n" + keys[2] + ": {valueY}";

        let hoverState = bullet.states.create("hover");
        hoverState.properties.fillOpacity = 1;
        hoverState.properties.strokeOpacity = 1;

        chart.logo.disabled = true;
        chart.legend = new am4charts.Legend();
        chart.legend.position = "top";

        // adds scrollbars to chart
        chart.scrollbarX = new am4core.Scrollbar();
        chart.scrollbarY = new am4core.Scrollbar();

        chart.cursor = new am4charts.XYCursor();    
        chart.cursor.behavior = "zoomXY";

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

export default AmScatter;