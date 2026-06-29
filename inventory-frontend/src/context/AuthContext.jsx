/* eslint-disable react-refresh/only-export-components */
import { createContext, useContext } from 'react';

const AuthContext = createContext(null);

export const useAuth = () => useContext(AuthContext);

export default AuthContext;
