import React from 'react';

import { Navigate, Route, Routes } from "react-router-dom"

import { AuthProvider } from './AuthProvider';
import { RequireAuth } from './RequireAuth';

import LoginPage from '../login/LoginPage/LoginPage';
import Main from '../main/Main/Main';

import ReportsMenuView from '../main/Main/Reports/ReportsMenuView';
import PublishReportDesigner from '../main/Main/Reports/PublishReportDesigner';

import FavoritesMenuView from '../main/Main/Favorites/FavoritesMenuView';
import JobsMenuView from '../main/Main/Jobs/JobsMenuView';

// Администрирование
import RolesMenuView from '../main/Main/Administration/Roles/RolesMenuView';
import RoleViewer from '../main/Main/Administration/Roles/RoleViewer';
import RoleDesigner from '../main/Main/Administration/Roles/RoleDesigner';
import UserDesigner from '../main/Main/Administration/Users/UserDesigner';
import SecurityFiltersMenuView from '../main/Main/Administration/SecurityFilters/SecurityFiltersMenuView';
import SecurityFilterViewer from '../main/Main/Administration/SecurityFilters/SecurityFilterViewer';
import SecurityFilterDesigner from '../main/Main/Administration/SecurityFilters/SecurityFilterDesigner';
import UserJobsMenuView from '../main/Main/Administration/UsersJobs/UsersJobsMenuView';

import ASMAdministrationMenuView from '../main/Main/Administration/ASMAdministration/ASMAdministrationMenuView';
import ASMViewer from '../main/Main/Administration/ASMAdministration/ASMViewer';
import ASMDesigner from '../main/Main/Administration/ASMAdministration/ASMDesigner';

import LogsMenuView from '../main/Main/Administration/Logs/LogsMenuView';
import SettingsMenuView from '../main/Main/Administration/ServerSettings/SettingsMenuView';

import ServerMailTemplateMenuView from '../main/Main/Administration/ServerMailTemplate/ServerMailTemplateMenuView';
import ServerMailTemplateView from '../main/Main/Administration/ServerMailTemplate/ServerMailTemplateView';
import ServerMailTemplateDesigner from '../main/Main/Administration/ServerMailTemplate/ServerMailTemplateDesigner';

import EmailMenuView from '../main/Main/Administration/MailSender/EmailMenuView';
import CubesMenuView from '../main/Main/Administration/Cubes/CubesMenuView';
import ThemesMenuView from '../main/Main/Administration/Theme/ThemesMenuView';

// Разработка
import DatasourcesMenuView from '../main/Main/Development/DatasourcesMenu/DatasourcesMenuView';
import DatasourceViewer from '../main/Main/Development/DatasourcesMenu/DatasourceViewer';
import DatasourceDesigner from '../main/Main/Development/DatasourcesMenu/DatasourceDesigner';

import DatasetsMenuView from '../main/Main/Development/DatasetsMenu/DatasetsMenuView';
import DatasetViewer from '../main/Main/Development/DatasetsMenu/DatasetViewer';
import DatasetDesigner from '../main/Main/Development/DatasetsMenu/DatasetDesigner';

import FilterTemplatesMenuView from '../main/Main/Development/FilterTemplates/FilterTemplatesMenuView';
import FilterTemplatesViewer from '../main/Main/Development/FilterTemplates/FilterTemplatesViewer';

import FilterInstancesMenuView from '../main/Main/Development/FilterInstances/FilterInstancesMenuView';
import FilterInstanceViewer from '../main/Main/Development/FilterInstances/FilterInstanceViewer';
import FilterInstanceDesigner from '../main/Main/Development/FilterInstances/FilterInstanceDesigner';

import ReportsDevMenuView from '../main/Main/Development/ReportsDevMenu/ReportsDevMenuView';
import ReportDevViewer from '../main/Main/Development/ReportsDevMenu/ReportDevViewer';
import ReportDevDesigner from '../main/Main/Development/ReportsDevMenu/ReportDevDesigner';

// Расписание
import SchedulesMenuView from "../main/Main/Schedule/Schedules/SchedulesMenuView";
import ScheduleViewer from "../main/Main/Schedule/Schedules/ScheduleViewer";
import ScheduleDesigner from "../main/Main/Schedule/Schedules/ScheduleDesigner";
import ScheduleTasksMenuView from '../main/Main/Schedule/SchedulerTasks/ScheduleTasksMenuView';
import ScheduleTasksViewer from '../main/Main/Schedule/SchedulerTasks/ScheduleTasksViewer';
import ScheduleTasksDesigner from '../main/Main/Schedule/SchedulerTasks/ScheduleTasksDesigner';

import ReportJob from '../main/Report/ReportJob';
import ReportStarter from '../main/Report/ReportStarter';


export const routesName = ['reports', 'favorites', 'job', 'roles', 'users', 'securityFilters', 'userJobs', 'asm', 'logs', 'settings', 
					'systemMailTemplates', 'mailSender', 'cubes', 'theme', 'datasource', 'dataset', 'filterTemplate', 'filterInstance', 
					'reportsDev', 'schedules', 'scheduleTasks', 'report']

export default function AppRoutes(props) {

	return (
		<AuthProvider>
			<Routes>
				<Route path="/ui/login" element={ <LoginPage version={props.version}/> }/>
				<Route
					path="*"
					element={<Navigate to="/ui/reports" replace />}
				/>

				<Route element = { 
					<RequireAuth>
						<Main version={props.version}/>
					</RequireAuth> 
				}>
					<Route path="/ui/reports" element={<ReportsMenuView/>} />
					<Route path="/ui/reports/:id" element={<ReportsMenuView/>} />
					<Route path="/ui/reports/:folderId/add" element={<PublishReportDesigner/>} />
					<Route path="/ui/favorites" element={<FavoritesMenuView/>} />
					<Route path="/ui/job" element={<JobsMenuView/>} />

					{/********** Администрирование **********/}
					<Route path="/ui/roles" element={<RolesMenuView/>} />
					<Route path="/ui/roles/:id" element={<RolesMenuView/>} />
					<Route path="/ui/roles/:folderId/view/:id" element={<RoleViewer/>} />
					<Route path="/ui/roles/:folderId/edit/:id" element={<RoleDesigner/>} />
					<Route path="/ui/roles/:folderId/add" element={<RoleDesigner/>} />

					<Route path="/ui/users" element={<UserDesigner/>} />

					<Route path="/ui/securityFilters" element={<SecurityFiltersMenuView/>} />
					<Route path="/ui/securityFilters/:id" element={<SecurityFiltersMenuView/>} />
					<Route path="/ui/securityFilters/:folderId/view/:id" element={<SecurityFilterViewer/>} />
					<Route path="/ui/securityFilters/:folderId/edit/:id" element={<SecurityFilterDesigner/>} />
					<Route path="/ui/securityFilters/:folderId/add" element={<SecurityFilterDesigner/>} />

					<Route path="/ui/userJobs" element={<UserJobsMenuView/>} />

					<Route path="/ui/asm" element={<ASMAdministrationMenuView/>} />
					<Route path="/ui/asm/view/:id" element={<ASMViewer/>} />
					<Route path="/ui/asm/edit/:id" element={<ASMDesigner/>} />
					<Route path="/ui/asm/add" element={<ASMDesigner/>} />

					<Route path="/ui/logs" element={<LogsMenuView/>} />
					<Route path="/ui/settings" element={<SettingsMenuView/>} /> {/* !!!!!!!! */}

					<Route path="/ui/systemMailTemplates" element={<ServerMailTemplateMenuView/>} />
					<Route path="/ui/systemMailTemplates/:id" element={<ServerMailTemplateMenuView/>} />
					<Route path="/ui/systemMailTemplates/:folderId/view/:id" element={<ServerMailTemplateView/>} />
					<Route path="/ui/systemMailTemplates/:folderId/edit/:id" element={<ServerMailTemplateDesigner/>} />

					<Route path="/ui/mailSender" element={<EmailMenuView/>} />
					<Route path="/ui/cubes" element={<CubesMenuView/>} />
					<Route path="/ui/theme" element={<ThemesMenuView/>} />  {/* !!!!!!!! */}

					{/********** Разработка **********/}
					<Route path="/ui/datasource" element={<DatasourcesMenuView />} />
					<Route path="/ui/datasource/:id" element={<DatasourcesMenuView/>} />
					<Route path="/ui/datasource/:folderId/view/:id" element={<DatasourceViewer/>} />
					<Route path="/ui/datasource/:folderId/edit/:id" element={<DatasourceDesigner/>} />
					<Route path="/ui/datasource/:folderId/add" element={<DatasourceDesigner/>} />

					<Route path="/ui/dataset" element={<DatasetsMenuView/>} />
					<Route path="/ui/dataset/:id" element={<DatasetsMenuView/>} />
					<Route path="/ui/dataset/:folderId/view/:id" element={<DatasetViewer/>} />
					<Route path="/ui/dataset/:folderId/edit/:id" element={<DatasetDesigner/>} />
					<Route path="/ui/dataset/:folderId/add" element={<DatasetDesigner/>} />

					<Route path="/ui/filterTemplate" element={<FilterTemplatesMenuView/>} />
					<Route path="/ui/filterTemplate/:id" element={<FilterTemplatesMenuView/>} />
					<Route path="/ui/filterTemplate/:folderId/view/:id" element={<FilterTemplatesViewer/>} />

					<Route path="/ui/filterInstance" element={<FilterInstancesMenuView/>} />
					<Route path="/ui/filterInstance/:id" element={<FilterInstancesMenuView/>} />
					<Route path="/ui/filterInstance/:folderId/view/:id" element={<FilterInstanceViewer/>} />
					<Route path="/ui/filterInstance/:folderId/edit/:id" element={<FilterInstanceDesigner/>} />
					<Route path="/ui/filterInstance/:folderId/add" element={<FilterInstanceDesigner/>} />

					<Route path="/ui/reportsDev" element={<ReportsDevMenuView/>} />
					<Route path="/ui/reportsDev/:id" element={<ReportsDevMenuView/>} />
					<Route path="/ui/reportsDev/:folderId/view/:id" element={<ReportDevViewer/>} />
					<Route path="/ui/reportsDev/:folderId/edit/:id" element={<ReportDevDesigner/>} />
					<Route path="/ui/reportsDev/:folderId/add" element={<ReportDevDesigner/>} />

					{/********** Расписание **********/}
					<Route path="/ui/schedules" element={<SchedulesMenuView/>} />
					<Route path="/ui/schedules/view/:id" element={<ScheduleViewer/>} />
					<Route path="/ui/schedules/edit/:id" element={<ScheduleDesigner/>} />
					<Route path="/ui/schedules/add" element={<ScheduleDesigner/>} />

					<Route path="/ui/scheduleTasks" element={<ScheduleTasksMenuView/>} />
					<Route path="/ui/scheduleTasks/view/:id" element={<ScheduleTasksViewer/>} />
					<Route path="/ui/scheduleTasks/edit/:id" element={<ScheduleTasksDesigner/>} />
					<Route path="/ui/scheduleTasks/add" element={<ScheduleTasksDesigner/>} />

					{/********** Отчёт **********/}
					<Route path="/ui/report/:id" element={<ReportJob/>} />
					<Route path="/ui/report/starter/:id" element={<ReportStarter/>} />
					
				</Route>

			</Routes>	
		</AuthProvider>
	)
}