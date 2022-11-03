import { createTheme /*, ThemeProvider*/ } from '@material-ui/core/styles';
// import { light } from '@material-ui/core/styles/createPalette';
import { orange } from '@material-ui/core/colors';

const defaultDarkTheme = createTheme({
    palette: {
        primary: 
        {
            main: '#7377a9', //'#55588a', //'#464775', //'#7986cb'
        }, 
        secondary:
        {
            main: '#ff4081'
        },
        active: '#8C989D', //активный лист дерева
        folderIcon: orange[100],
        breadcrumbsLabel: '#bcc2e5',
        drawerColor: '#434550',
        pivotTable: {
            grey: '#616161',
            dimentionValue: '#757575'
        },
        type: 'dark',
    },
    typography: {
        htmlFontSize: 16,
        fontFamily: [
              //'Pattaya',
              'Roboto',
              'Helvetica',
              'Arial',
              'sans-serif',
              '"Segoe UI"' ,
              '-apple-system',
              'BlinkMacSystemFont',
              '"Apple Color Emoji"',
              '"Segoe UI Emoji"',
              '"Segoe UI Symbol"'
            ].join(',')  ,
        fontSize: 14,   
    
            
    }
  });

export default defaultDarkTheme