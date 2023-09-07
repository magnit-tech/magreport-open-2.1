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

export function JobThreadsChart(props) {
	const classes = useStyles();

	const { title, dataThreads } = props;

	const data = {
		labels: [""],
		datasets: [
			{
				label: "Суммарное кол-во потоков",
				data: [dataThreads.totalThreads, dataThreads.totalThreads + 1],
				backgroundColor: ["rgba(0,0,0, 0.2)"],
				borderColor: ["rgba(0,0,0, 1)"],
				grouped: false,
				order: 1,
				categoryPercentage: 0.6,
			},
			{
				label: "Кол-во занятых потоков",
				data: [dataThreads.currentThreads],
				backgroundColor: ["rgba(255, 0, 0, 0.5)"],
				borderColor: ["rgba(255, 0, 0, 1)"],
				grouped: false,
				order: 2,
				categoryPercentage: 0.5,
			},
		],
	};

	const options = {
		indexAxis: "y",
		borderRadius: 6,
		responsive: true,
		maintainAspectRatio: false,
		scales: {
			y: {
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
				text: title,
			},
		},
	};

	return (
		<Box className={classes.jobThreadsChart}>
			<Bar options={options} data={data} />
		</Box>
	);
}


