import React from 'react';

import { FolderItemTypes } from '../../FolderContent/FolderItemTypes';

import FolderOpenIcon from '@material-ui/icons/FolderOpen';
import AlarmIcon from '@material-ui/icons/Alarm';
import BuildIcon from '@material-ui/icons/Build';
import CodeIcon from '@material-ui/icons/Code';
import StarsIcon from '@material-ui/icons/Stars';
import ScheduleIcon from '@material-ui/icons/Schedule';

const SidebarItems = {
    reports : {
        key : "REPORTS_ITEM",
        text : "Отчёты",
        icon : (<FolderOpenIcon/>),
        folderTree : true,
        folderItemType: FolderItemTypes.reports
    },
    favorites: {
        key: 'FAVORITES_ITEM',
        text: 'Избранное',
        icon: (<StarsIcon />),
        folderTree : false,
        folderItemType: FolderItemTypes.favorites
    },
    jobs : {
        key : "TASKS_ITEM",
        text : "Задания",
        icon : (<AlarmIcon/>),
        folderTree : false,
        folderItemType : FolderItemTypes.job
    },
    admin : {
        key : "ADMIN_ITEM",
        text : "Администрирование",
        icon : (<BuildIcon/>),
        permission : "ADMIN",
        subItems : {
            roles : {
                key : "ROLES_ITEM",
                text : "Роли",
                folderTree : true,
                folderItemType: FolderItemTypes.roles
            },
            users : {
                key : "USERS_ITEM",
                text : "Пользователи",
                folderItemType: FolderItemTypes.users
            },
            securityFilters : {
                key : "SECURITY_FILTERS_ITEM",
                text : "Фильтры безопасности",
                folderTree : true,
                folderItemType: FolderItemTypes.securityFilters
            },
            userJobs : {
                key : "USER_TASKS_ITEM",
                text : "Задания пользователей",
                folderTree : false,
                folderItemType : FolderItemTypes.userJobs,
            },
            ASMAdministration : {
                key : "ASM_ADMIN_ITEM",
                text : "Управление ASM",
                folderItemType: FolderItemTypes.asm
            },
            logs : {
                key : "LOGS_ITEM",
                text : "Логи",
                folderItemType: FolderItemTypes.logs
            },
            settings : {
                key : "ADMIN_SETTINGS",
                text : "Настройки",
                folderItemType: FolderItemTypes.settings
            },
            mailTexts : {
                key : "SYSTEM_MAIL_TEXT_ITEM",
                text : "Тексты системных писем",
                folderTree : true,
                folderItemType: FolderItemTypes.systemMailTemplates
            },
            mailSender : {
                key : "MAIL_SENDER",
                text : "Рассылка писем",
                folderItemType: FolderItemTypes.mailSender
            },
            cubes: {
                key: "CUBES",
                text: "Кубы",
                folderTree : false,
                folderItemType: FolderItemTypes.cubes
            },
            theme : {
                key : 'DESIGNER',
                text : 'Дизайн',
                folderTree : false,
                folderItemType: FolderItemTypes.theme
            },
            loadMonitoring: {
                key: "LOAD_MONITORING",
                text: "Мониторинг нагрузки",
                folderTree : true,
                folderItemType: FolderItemTypes.loadMonitoring
            },

        }
    },
    development : {
        key : "DEVELOPMENT_ITEM",
        text : "Разработка",
        icon : (<CodeIcon/>),
        permission : "DEVELOPER",
        subItems : {
            datasources : {
                key : "DATASOURCES_ITEM",
                text : "Источники данных",
                folderTree : true,
                folderItemType: FolderItemTypes.datasource
            },
            datasets : {
                key : "DATASETS_ITEM",
                text : "Наборы данных",
                folderTree : true,
                folderItemType: FolderItemTypes.dataset
            },
            filterTemplates : {
                key : "FILTERTEMPLATES_ITEM",
                text : "Шаблоны фильтров",
                folderTree : true,
                folderItemType: FolderItemTypes.filterTemplate
            },            
            filterInstances : {
                key : "FILTERINSTANCES_ITEM",
                text : "Экземпляры фильтров",
                folderTree : true,
                folderItemType: FolderItemTypes.filterInstance
            },
            reportsDev : {
                key : "REPORTSDEV_ITEM",
                text : "Разработка отчётов",
                folderTree : true,
                folderItemType: FolderItemTypes.reportsDev
            }                 
        }
    },
    schedule : {
        key : "SCHEDULE_ITEM",
        text : "Расписание",
        icon : (<ScheduleIcon />),
        permission : "ADMIN",
        subItems : {
            schedules: {
                key: "SCHEDULES_ITEM",
                text: "Расписания",
                folderTree: false,
                folderItemType: FolderItemTypes.schedules,
            },
            scheduleTasks : {
                key : "SCHEDULED_REPORTS",
                text : "Отчеты на расписании",
                folderTree : false,
                folderItemType: FolderItemTypes.scheduleTasks
            },             
        }
    }
};

export default SidebarItems;