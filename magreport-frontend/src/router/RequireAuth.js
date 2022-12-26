import React from 'react';
import { useLocation, Navigate } from 'react-router-dom'
import { useAuth } from './useAuth';

const RequireAuth = ({children}) => {
	const location = useLocation()
	const { user, setUserData } = useAuth()

	if (!user.current) {
		let userData = localStorage.getItem('userData') ? JSON.parse(localStorage.getItem('userData')) : ''

		if (userData && userData.authtoken && userData.name) {
			setUserData(userData)
		} else {
			return <Navigate to="/ui/login" state={{from: location}}/>
		}
	}
	
	return children
}

export {RequireAuth}