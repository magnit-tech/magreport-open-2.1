import {alpha, makeStyles, withStyles} from '@material-ui/core/styles';
import StyleConsts from '../../../../StyleConsts.js';
import TextField from '@material-ui/core/TextField';

export const DomainSelect = withStyles((theme) => ({
  root: {
      backgroundColor: alpha(theme.palette.primary.light, 0.15),
      border: '0px solid',
      borderColor: alpha(theme.palette.primary.light, 0.15),
      '&:hover': {
          backgroundColor: alpha(theme.palette.primary.light, 0.25),
      },
      '& .MuiInput-underline:after': {
        borderBottomColor: alpha(theme.palette.primary.light, 0.15),
      },
      '& .MuiOutlinedInput-root': {
          //color: theme.palette.primary.contrastText,
              '& fieldset': {
              borderColor: alpha(theme.palette.primary.light, 0.15),
          },
          '&:hover fieldset': {
              borderColor: alpha(theme.palette.primary.light, 0.15),
          },
          '&.Mui-focused fieldset': {
              borderColor: alpha(theme.palette.primary.light, 0.15),
          },
      },
      '& .MuiOutlinedInput-input': {
      //    padding: '8px 32px 8px 8px'
      }
  }
}))(TextField);


export const DomainGroupCSS = makeStyles(theme => ({
/*    titlebar: {
        backgroundColor: theme.palette.primary.main,
        color: theme.palette.primary.contrastText,
    },*/
    menuList: {
      //backgroundColor: alpha(theme.palette.primary.light, 0.25),
      fontWeight: 500,
      color: theme.palette.primary.main
    /*  '&:hover': {
        backgroundColor: alpha(theme.palette.primary.light, 0.45),
      },
      '& .MuiListItem-root': {
        '&.Mui-selected': {
          backgroundColor: alpha(theme.palette.primary.light, 0.55),
        }, 
        '&:hover': {
          backgroundColor: alpha(theme.palette.primary.light, 0.75),
        },
      }*/
    },
    domainTooltip: {
      cursor: 'pointer'
    },
    userAddPanel: {
      display: 'flex',
      justifyContent: 'space-between',
      flexWrap: 'wrap',
      padding: '16px 40px 0px 16px'
    },
    roleAutocompleteDiv: {
      display: 'flex',
      maxWidth: '50%',
      flexGrow: 1,
     // margin: '16px 16px 0px'
    },
    selectDiv: {
      display: 'flex', 
      justifyContent: 'end', 
     // margin: '16px 0px 0px 16px'
    },
    addButton: {
      minWidth: '120px'
    },
    grow: {
        flexGrow: 1,
    },
    domainAutocomplete:{
      display: 'flex',
      flexGrow: 1,
      marginRight: '16px',
      minWidth: '200px'
    },
    title: {
        flexGrow: 1,
        display: 'none',
        [theme.breakpoints.up('sm')]: {
            display: 'block',
        },
    },
    domainGroupList: {
      //margin: theme.spacing(1),
      //height: `calc(100vh - ${StyleConsts.headerHeight} - 30px - 174px - ${StyleConsts.breadHeight})`,
      minWidth: StyleConsts.paperRoleUserWidth,
      padding: 0,
      display: 'flex',
      flexDirection: 'column',
      flex: 1,
      minHeight: '48px'
    },
    domainGroupListBox: {
      //overflow: 'auto',
      //height: `calc(100vh - ${StyleConsts.headerHeight} * 3 - 32px - 124px - ${StyleConsts.breadHeight})`
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      display: 'flex',
      overflow: 'auto',
      position: 'absolute',
      flexDirection: 'column'
  },
    search: {
        position: 'relative',
        borderRadius: theme.shape.borderRadius,
       // color: theme.palette.primary.contrastText,
        backgroundColor: alpha(theme.palette.primary.light, 0.15),
        '&:hover': {
          backgroundColor: alpha(theme.palette.primary.light, 0.25),
        },
        marginLeft: '8px',
        width: '100%',
        [theme.breakpoints.up('sm')]: {
          marginLeft: theme.spacing(1),
          width: 'auto',
        },
    },
    searchIcon: {
        width: theme.spacing(7),
        height: '100%',
        position: 'absolute',
        pointerEvents: 'none',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
    },
    inputRoot: {
        color: 'inherit',
    },
    inputInput: {
        padding: theme.spacing(1, 1, 1, 7),
        transition: theme.transitions.create('width'),
        width: '100%',
        [theme.breakpoints.up('sm')]: {
          width: theme.spacing(12), //96px,
          '&:focus': {
            width: 200,
          },
        },
        [theme.breakpoints.down('md')]: {
            width: theme.spacing(0), //96px,
            '&:focus': {
              width: 200,
            },
          },
    },
}));
