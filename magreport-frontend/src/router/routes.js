import React from 'react';

import { Navigate, Route, Routes } from "react-router-dom"

import { AuthProvider } from './AuthProvider';
import { RequireAuth } from './RequireAuth';

import LoginPage from '../login/LoginPage/LoginPage';
import Main from '../main/Main/Main';

import ReportsMenuView from '../main/Main/Reports/ReportsMenuView';
import FavoritesMenuView from '../main/Main/Favorites/FavoritesMenuView';
import JobsMenuView from '../main/Main/Jobs/JobsMenuView';

import RolesMenuView from '../main/Main/Administration/Roles/RolesMenuView';
import UsersMenuView from '../main/Main/Administration/Users/UsersMenuView';
import SecurityFiltersMenuView from '../main/Main/Administration/SecurityFilters/SecurityFiltersMenuView';
import UserJobsMenuView from '../main/Main/Administration/UsersJobs/UsersJobsMenuView';
import ASMAdministrationMenuView from '../main/Main/Administration/ASMAdministration/ASMAdministrationMenuView';
import LogsMenuView from '../main/Main/Administration/Logs/LogsMenuView';
import SettingsMenuView from '../main/Main/Administration/ServerSettings/SettingsMenuView';
import MailTemplatesMenuView from '../main/Main/Administration/ServerMailTemplate/ServerMailTemplateMenuView';
import EmailMenuView from '../main/Main/Administration/MailSender/EmailMenuView';
import CubesMenuView from '../main/Main/Administration/Cubes/CubesMenuView';
import ThemesMenuView from '../main/Main/Administration/Theme/ThemesMenuView';

import DatasourcesMenuView from '../main/Main/Development/DatasourcesMenu/DatasourcesMenuView';
import DatasetsMenuView from '../main/Main/Development/DatasetsMenu/DatasetsMenuView';
import FilterTemplatesMenuView from '../main/Main/Development/FilterTemplates/FilterTemplatesMenuView';
import FilterInstancesMenuView from '../main/Main/Development/FilterInstances/FilterInstancesMenuView';
import ReportsDevMenuView from '../main/Main/Development/ReportsDevMenu/ReportsDevMenuView';

// Расписание
import SchedulesMenuView from "../main/Main/Schedule/Schedules/SchedulesMenuView";
import ScheduleViewer from "../main/Main/Schedule/Schedules/ScheduleViewer";
import ScheduleDesigner from "../main/Main/Schedule/Schedules/ScheduleDesigner";
import ScheduleTasksMenuView from '../main/Main/Schedule/SchedulerReports/ScheduleTasksMenuView';
import ScheduleTasksViewer from '../main/Main/Schedule/SchedulerReports/ScheduleTasksViewer';
import ScheduleTasksDesigner from '../main/Main/Schedule/SchedulerReports/ScheduleTasksDesigner';

import ReportJob from '../main/Report/ReportJob';
import ReportStarter from '../main/Report/ReportStarter';




export default function AppRoutes(props) {
	return (
		<AuthProvider>
			<Routes>
				<Route path="/login" element={ <LoginPage version={props.version}/> }/>
				<Route
					path="*"
					element={<Navigate to="/reports" replace />}
				/>

				<Route element={ <RequireAuth><Main version={props.version}/></RequireAuth> }>

					<Route path="/reports" element={<ReportsMenuView/>} />
					<Route path="/reports/:id" element={<ReportsMenuView/>} />
					<Route path="/favorites" element={<FavoritesMenuView/>} />
					<Route path="/job" element={<JobsMenuView/>} />

					{/* Администрирование */}
					<Route path="/roles" element={<RolesMenuView/>} />
					<Route path="/users" element={<UsersMenuView/>} />
					<Route path="/securityFilters" element={<SecurityFiltersMenuView/>} />
					<Route path="/userJobs" element={<UserJobsMenuView/>} />
					<Route path="/asm" element={<ASMAdministrationMenuView/>} />
					<Route path="/logs" element={<LogsMenuView/>} />
					<Route path="/settings" element={<SettingsMenuView/>} />
					<Route path="/systemMailTemplates" element={<MailTemplatesMenuView/>} />
					<Route path="/mailSender" element={<EmailMenuView/>} />
					<Route path="/cubes" element={<CubesMenuView/>} />
					<Route path="/theme" element={<ThemesMenuView/>} /> 

					{/* Разработка */}
					<Route path="/datasource" element={<DatasourcesMenuView/>} />
					<Route path="/dataset" element={<DatasetsMenuView/>} />
					<Route path="/filterTemplate" element={<FilterTemplatesMenuView/>} />
					<Route path="/filterInstance" element={<FilterInstancesMenuView/>} />
					<Route path="/reportsDev" element={<ReportsDevMenuView/>} />

					{/* Расписание */}
					<Route path="/schedules" element={<SchedulesMenuView/>} />
					<Route path="/schedules/:id" element={<ScheduleViewer/>} />
					<Route path="/schedules/:id/edit" element={<ScheduleDesigner/>} />
					<Route path="/schedules/add" element={<ScheduleDesigner/>} />

					<Route path="/scheduleTasks" element={<ScheduleTasksMenuView/>} />
					<Route path="/scheduleTasks/:id" element={<ScheduleTasksViewer/>} />
					<Route path="/scheduleTasks/:id/edit" element={<ScheduleTasksDesigner/>} />
					<Route path="/scheduleTasks/add" element={<ScheduleTasksDesigner/>} />

					{/* Отчёт */}
					<Route path="/report/:id" element={<ReportJob/>} />
					<Route path="/report-starter/:id" element={<ReportStarter/>} />
					
				</Route>

			</Routes>	
		</AuthProvider>
	)
}