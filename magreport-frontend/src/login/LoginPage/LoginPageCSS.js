
import { makeStyles} from '@material-ui/core/styles';
import LoginCatPng from '../../images/LoginCat.png';
import NewYearCatPng from '../../images/NewYearCat.png'
import NewYearCatPng1 from '../../images/NewYearCat1.png'
import NewYearCatPng2 from '../../images/NewYearCat2.png'


export const LoginPageCSS = makeStyles(theme => ({
    main: {
        padding: theme.spacing(0),
        margin: theme.spacing(0)
    },
    snow: {
       // background: 'linear-gradient(90deg, #efd5ff 0%, #515ada 100%)'
        backgroundColor: theme.palette.action.disabledBackground
    },
    catBox: {
        backgroundColor: theme.palette.primary.main
    },
    welcomeText: {
        textAlign: 'center',
    },
    loginBox: {
        paddingTop: '32px'
    },
    loginCat : {
        backgroundImage : 'url(' + LoginCatPng + ')',
        backgroundSize: 'contain',
        backgroundRepeat: 'no-repeat',
        backgroundPosition: 'center',
        marginTop : '48px',
        height: '400px'
    },
    newYearLoginCat: {
        backgroundImage : 'url(' + NewYearCatPng + ')',
        backgroundSize: 'contain',
        backgroundRepeat: 'no-repeat',
        backgroundPosition: 'center',
        marginTop : '48px',
        height: '400px'
    },
    newYearLoginCat1: {
        backgroundImage : 'url(' + NewYearCatPng1 + ')',
        backgroundSize: 'contain',
        backgroundRepeat: 'no-repeat',
        backgroundPosition: 'center',
        marginTop : '48px',
        height: '400px'
    },
    newYearLoginCat2: {
        backgroundImage : 'url(' + NewYearCatPng2 + ')',
        backgroundSize: 'contain',
        backgroundRepeat: 'no-repeat',
        backgroundPosition: 'center',
        marginTop : '48px',
        height: '400px'
    },
    errorPaper: {
        padding: '16px', 
        marginTop: '8px', 
        marginRight: '4px',
        border: '2px solid', 
        borderColor: theme.palette.error.dark,
        display: 'flex'
    }

    
}));