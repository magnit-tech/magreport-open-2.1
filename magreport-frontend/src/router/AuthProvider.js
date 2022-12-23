import React, {createContext, useRef} from "react";

import { useNavigate } from 'react-router-dom';

import dataHub from 'ajax/DataHub'

import { useDispatch } from 'react-redux';
import { hideLoader } from '../redux/actions/UI/actionLoader'
import { showAlert, hideAlert } from '../redux/actions/UI/actionsAlert'


export const AuthContext = createContext(null)

export const AuthProvider = ({children}) => {
    const dispatch = useDispatch();
    const navigate = useNavigate()

	const user = useRef(null)

	// Авторизация
	const signin = (userName, domainName, passWord, cb) => {
		try {        
            dataHub.login(userName, domainName, passWord, handleLogin)

            function handleLogin (magrepResponse){
                if (magrepResponse.ok){
                    user.current = {authtoken: magrepResponse.data.authtoken.replace('Bearer ', '')}
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
					user.current = {
                        ...user.current, 
                        name: magrepResponse.data.name,
                        isAdmin: magrepResponse.data.isAdmin,
                        isDeveloper: magrepResponse.data.isDeveloper,
                        domain: magrepResponse.data.domain
                    }
                    localStorage.setItem('userData', JSON.stringify(user.current))
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
            }
        }
        catch (e) {
            console.error(e)
            
        }
	}

	// Выход из аккаунта
	const signout = () => {
        user.current = null
        localStorage.removeItem('userData')
		navigate('/ui/login')
	}

    const setUserData = (userData) => {
        user.current = userData
    }

	const value = {user, signin, signout, setUserData}

	return <AuthContext.Provider value={value}>
		{children}
	</AuthContext.Provider>
}