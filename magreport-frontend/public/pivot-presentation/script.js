/*
	Регистрация события "Просмотр презентации сводной таблицы"
*/
const PROTOCOL = window.location.protocol;
const HOST = window.location.hostname;
const PORT = window.location.port;
const API_BASE_URL = '/api/v1';
const EVENT_REGISTER = '/event/register'

const BASE_URL = PROTOCOL + "//" + HOST + (PORT ? ":" + PORT : "") + API_BASE_URL + EVENT_REGISTER;


const payload = {
    typeEvent: "PRESENTATION-READ",
    additionally: ''
};

const data = JSON.stringify(payload);

const xhr = new XMLHttpRequest();

xhr.open("POST", BASE_URL);
xhr.setRequestHeader("Content-Type", "application/json");

xhr.send(data);