import React from 'react';

import { Route, Routes } from "react-router-dom"

import { AuthProvider } from './AuthProvider';

import LoginPage from '../login/LoginPage/LoginPage';
import Main from '../main/Main/Main';

import ReportsMenuView from '../main/Main/Reports/ReportsMenuView';
import JobsMenuView from '../main/Main/Jobs/JobsMenuView';
import RolesMenuView from '../main/Main/Administration/Roles/RolesMenuView';
import UsersMenuView from '../main/Main/Administration/Users/UsersMenuView';
import UserJobsMenuView from '../main/Main/Administration/UsersJobs/UsersJobsMenuView';
import SecurityFiltersMenuView from '../main/Main/Administration/SecurityFilters/SecurityFiltersMenuView';
import ASMAdministrationMenuView from '../main/Main/Administration/ASMAdministration/ASMAdministrationMenuView';
import LogsMenuView from '../main/Main/Administration/Logs/LogsMenuView';
import SettingsMenuView from '../main/Main/Administration/ServerSettings/SettingsMenuView';
import DatasourcesMenuView from '../main/Main/Development/DatasourcesMenu/DatasourcesMenuView';
import DatasetsMenuView from '../main/Main/Development/DatasetsMenu/DatasetsMenuView';
import FilterTemplatesMenuView from '../main/Main/Development/FilterTemplates/FilterTemplatesMenuView';
import FilterInstancesMenuView from '../main/Main/Development/FilterInstances/FilterInstancesMenuView';
import ReportsDevMenuView from '../main/Main/Development/ReportsDevMenu/ReportsDevMenuView';
import FavoritesMenuView from '../main/Main/Favorites/FavoritesMenuView';
import SchedulesMenuView from "../main/Main/Schedule/Schedules/SchedulesMenuView";
import ScheduleTasksMenuView from '../main/Main/Schedule/SchedulerReports/ScheduleTasksMenuView';
import MailTemplatesMenuView from '../main/Main/Administration/ServerMailTemplate/ServerMailTemplateMenuView';
import EmailMenuView from '../main/Main/Administration/MailSender/EmailMenuView';
import ThemesMenuView from '../main/Main/Administration/Theme/ThemesMenuView';
import CubesMenuView from '../main/Main/Administration/Cubes/CubesMenuView';
import { RequireAuth } from './RequireAuth';

export default function AppRoutes() {
	return (
		<AuthProvider>
			<Routes>
				<Route path="/login" element={<LoginPage />} />
				

				<Route element={<RequireAuth><Main /></RequireAuth>} >
						<Route path="/" element={<ReportsMenuView/>} />
						<Route path="/favorites" element={<FavoritesMenuView/>} />
						<Route path="/jobs" element={<JobsMenuView/>} />
						<Route path="/jobs" element={<JobsMenuView/>} />

						{/* Администрирование */}
						<Route path="/jobs" element={<RolesMenuView/>} />
						<Route path="/jobs" element={<UsersMenuView/>} />
						<Route path="/jobs" element={<UserJobsMenuView/>} />
						<Route path="/jobs" element={<SecurityFiltersMenuView/>} />
						<Route path="/jobs" element={<ASMAdministrationMenuView/>} />
						<Route path="/jobs" element={<LogsMenuView/>} />
						<Route path="/jobs" element={<SettingsMenuView/>} />
						<Route path="/jobs" element={<MailTemplatesMenuView/>} />
						<Route path="/jobs" element={<EmailMenuView/>} />
						<Route path="/jobs" element={<CubesMenuView/>} />
						<Route path="/jobs" element={<ThemesMenuView/>} /> 

						{/* Разработка */}
						<Route path="/jobs" element={<DatasourcesMenuView/>} />
						<Route path="/jobs" element={<DatasetsMenuView/>} />
						<Route path="/jobs" element={<FilterTemplatesMenuView/>} />
						<Route path="/jobs" element={<FilterInstancesMenuView/>} />
						<Route path="/jobs" element={<ReportsDevMenuView/>} />
						<Route path="/jobs" element={<SchedulesMenuView/>} />
						<Route path="/jobs" element={<ScheduleTasksMenuView/>} />
				</Route>

			</Routes>	
		</AuthProvider>
	)
}