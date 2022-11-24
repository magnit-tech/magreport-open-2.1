import React, {createContext, useState} from "react";

import { useNavigate } from 'react-router-dom';

import dataHub from 'ajax/DataHub'

import { useDispatch } from 'react-redux';
import { hideLoader } from '../redux/actions/actionLoader'
import { showAlert, hideAlert } from '../redux/actions/actionsAlert'
// import { APP_LOGGED_IN } from '../redux/reduxTypes'


export const AuthContext = createContext(null)

export const AuthProvider = ({children}) => {
    const dispatch = useDispatch();
    const navigate = useNavigate()

	const [user, setUser] = useState(null)

	// Авторизация
	const signin = (userName, domainName, passWord, cb) => {
		try {        
            dataHub.login(userName, domainName, passWord, handleLogin)

            function handleLogin (magrepResponse){
                if (magrepResponse.ok){
                    dataHub.userController.whoAmI(handleWhoAmI)                 
                }
                else {
                    switch(magrepResponse.data.status) {
                        case 401 : handleFail("Неверный логин / пароль");
                        break;
                        case 403 : handleFail("Учетная запись заблокирована");
                        break;
                        default : handleFail("Произошла неизвестная ошибка");
                    }
                }
            }
            
            function handleWhoAmI (magrepResponse){
                if (magrepResponse.ok){
                    dispatch(hideLoader())
                    // dispatch({type: APP_LOGGED_IN, userName})
					setUser(magrepResponse.data)
					cb()
                }
                else {
                    handleFail("Не удалось получить информацию о пользователе")
                }
            }

            function handleFail(errorText){
                dispatch(hideLoader())
                const callback = () => {dispatch(hideAlert())}
                const buttons = [{'text':'OK','onClick': callback}]
                dispatch(showAlert("Ошибка", errorText, buttons, callback))
                // props.appLoginFail()
            }
        }
        catch (e) {
            console.error(e)
            
        }
	}

	// Выход из аккаунта
	const signout = () => {
		setUser(null)
		navigate('/login')
	}

	const value = {user, signin, signout}

	return <AuthContext.Provider value={value}>
		{children}
	</AuthContext.Provider>
}