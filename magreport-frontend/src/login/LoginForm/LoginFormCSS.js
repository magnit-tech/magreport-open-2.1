import { makeStyles } from '@material-ui/core/styles';


export const LoginFormCSS = makeStyles(theme => ({
    paper: {
      padding: theme.spacing(8, 4),
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center'
    },
    avatar: {
      margin: theme.spacing(1),
      backgroundColor: theme.palette.secondary.main,
    },
    form: {
      width: '100%', // Fix IE 11 issue.
      marginTop: theme.spacing(1),
    },
    submit: {
      marginTop: '10px !important',
    },
    failLoggin: {
      display: 'flex',
      justifyContent: 'center',
      marginTop: '10px'
    },
    circularProgress: {
        display: 'flex',
        justifyContent: 'center',
    }
}));