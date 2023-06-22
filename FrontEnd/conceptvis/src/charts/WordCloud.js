import * as am4core from "@amcharts/amcharts4/core";
import * as am4plugins_wordCloud from "@amcharts/amcharts4/plugins/wordCloud"; 
import { useEffect } from "react";
import '../CSS/Chart.css';

function WordCloud({ contents }) {
    useEffect(() => {

        // makes chart object
        let chart = am4core.create("chartdiv", am4plugins_wordCloud.WordCloud); 
        let series = chart.series.push(new am4plugins_wordCloud.WordCloudSeries());

        series.data = contents;

        // gets column names
        const keys = Object.keys(contents[0]);
        series.dataFields.word = [keys[0]];
        series.dataFields.value = [keys[1]];

        let title = chart.titles.create();
        title.text = keys[0] + " vs " + keys[1];
        title.fontSize = 15;
        title.paddingTop = 10;
        title.paddingLeft = 110;

        chart.logo.disabled = true;
        series.labels.template.tooltipText = "{word}:\n[bold]{value}[/]";

        return () => {
        // Cleanup on component unmount
            chart.dispose();
        };

    }, [contents]);

    return <div id="chartdiv" className='chart'></div>;
}

export default WordCloud;