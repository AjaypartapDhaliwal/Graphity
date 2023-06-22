import React, { useEffect, useRef } from "react";
import Chart from "chart.js/auto";
import { registerables } from "chart.js";
import { TreemapController, TreemapElement } from "chartjs-chart-treemap";
import "../CSS/Chart.css";

Chart.register(...registerables, TreemapController, TreemapElement);

export default function TreeMap({contents}) {
  
  const canvasRef = useRef(null);

  // gets column names
  const keys = Object.keys(contents[0]);
  const fks = [];
  for (let i = 0; i < contents.length; i++) {
    fks.push(contents[i][keys[1]]);
  }

  // gets unique parent keys
  const numUniqueKeys = ([...new Set(fks)]).length;


  useEffect(() => {
    if (canvasRef.current) {
      const ctx = canvasRef.current.getContext("2d");

      // makes chart
      const chart = new Chart(ctx, {
        type: "treemap",
        data: {
          datasets: [
            {
              label: "chart",
              tree: contents.slice(0,50),
              backgroundColor: (ctx) => colorFromRaw(ctx,numUniqueKeys,keys),
              spacing: 1,
              labels: {
                display: true,
                align: "center",
                position: 'top',
              },
              key: keys[2],
              groups: [keys[1],keys[0]],
              captions: {
                display: true,
                color: 'black'
              },
            },
          ],
        },
      });

      return () => {
        chart.destroy();
      };
    }
  }, [contents]);

  return (
    <div className="chart">
      <canvas ref={canvasRef}></canvas>
    </div>
  );
}

// generates random color for each parent and child node
function colorFromRaw(ctx, n, keys) {
  const colorArray = generateRandomRGB(n);
  const companyArray = [];

  if (ctx.type !== "data") {
    return "transparent";
  }

  ctx.chart.data.datasets[0].tree.forEach((datastructure) => {
    companyArray.push(datastructure[keys[1]]);
  });

  const uniqueCompanyArray = [...new Set(companyArray)];
  const indexCompany = uniqueCompanyArray.indexOf(ctx.raw._data[keys[1]]);

  const value = ctx.raw.v;
  let alpha = Math.log(value) / 20;
  return `rgba(${colorArray[indexCompany]}, ${alpha})`;
}

// generates random color in rgb
function generateRandomRGB(n) {
  const colors = [];

  // Generate n random RGB values
  for (let i = 0; i < n; i++) {
    let rgb = generateRandomColor();
    colors.push(rgb);
  }

  return colors;
}

function generateRandomColor() {
  const r = Math.floor(Math.random() * 256);
  const g = Math.floor(Math.random() * 256);
  const b = Math.floor(Math.random() * 256);
  return `${r}, ${g}, ${b}`;
}
