import React from 'react';
import {
	Chart as ChartJS,
	CategoryScale,
	LinearScale,
	BarElement,
	Title,
	Tooltip,
	Legend,
} from 'chart.js';
import { Bar } from 'react-chartjs-2';

import { LoadMonitoringCSS as useStyles } from "../LoadMonitoringCSS";
import { Box } from '@material-ui/core';

ChartJS.register(
	CategoryScale,
	LinearScale,
	BarElement,
	Title,
	Tooltip,
	Legend
);

export function DataSourceChart({ dataSourceData }) {
	const { dataSourceName, connectPoolSize, activeConnectSize, queueConnectSize } = dataSourceData;

	const classes = useStyles();

	const data = {
		labels: [""],
		datasets: [
			{
				label: "Размер пула коннектов",
				data: [connectPoolSize, connectPoolSize + 1],
				backgroundColor: ["rgba(0,0,0, 0.2)"],
				borderColor: ["rgba(0,0,0, 1)"],
				grouped: false,
				order: 1,
				categoryPercentage: 0.4,
			},
			{
				label: "Кол-во выполняющихся заданий",
				data: [activeConnectSize],
				backgroundColor: ["rgba(0, 0, 255, 0.5)"],
				borderColor: ["rgba(0, 0, 255, 1)"],
				grouped: true,
				order: 2,
				categoryPercentage: 0.2,
			},
			{
				label: "Кол-во ожидающих заданий",
				data: [queueConnectSize],
				backgroundColor: ["rgba(255, 0, 0, 0.5)"],
				borderColor: ["rgba(255, 0, 0, 1)"],
				grouped: true,
				order: 3,
				categoryPercentage: 0.2,
			},
		],
	};

	const options = {
		indexAxis: "x",
		borderRadius: 6,
		responsive: true,
		maintainAspectRatio: false,
		scales: {
			x: {
				beginAtZero: true,
			},
		},
		elements: {
			bar: {
				borderWidth: .5,
			},
		},
		plugins: {
			legend: {
				position: 'bottom',
			},
			title: {
				display: true,
				text: dataSourceName,
			},
		},
	};

	return (
		<Box className={classes.dataSourceChart}>
			<Bar options={options} data={data} />
		</Box>
	);
}


