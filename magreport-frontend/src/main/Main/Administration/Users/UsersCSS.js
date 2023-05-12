import { alpha, makeStyles } from '@material-ui/core/styles';
import StyleConsts from '../../../../StyleConsts.js';

export const UsersCSS = makeStyles(theme => ({
	domainSelectLight: {
		backgroundColor: alpha(theme.palette.common.white, 0.15),
		border: '0px solid',
		borderColor: alpha(theme.palette.common.white, 0.15),
		'&:hover': {
			backgroundColor: alpha(theme.palette.common.white, 0.25),
		},
		'& .MuiInput-underline:after': {
			borderBottomColor: alpha(theme.palette.common.white, 0.15),
		},
		'& .MuiOutlinedInput-root': {
			color: theme.palette.primary.contrastText,
			'& fieldset': {
				borderColor: alpha(theme.palette.common.white, 0.15),
			},
			'&:hover fieldset': {
				borderColor: alpha(theme.palette.common.white, 0.15),
			},
			'&.Mui-focused fieldset': {
				borderColor: alpha(theme.palette.common.white, 0.15),
			},
		},
		'& .MuiOutlinedInput-input': {
			padding: '8px 32px 8px 4px',
		},
	},
	domainSelectDark: {
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
	},
	menuList: {
		fontWeight: 500,
		color: theme.palette.primary.main,
	},
	domainTooltip: {
		cursor: 'pointer',
	},
	userAddPanel: {
		display: 'flex',
		justifyContent: 'space-between',
		flexWrap: 'wrap',
		padding: '16px 40px 0px 16px',
	},
	domainLabel: {
		color: theme.palette.primary.contrastText,
	},
	rel: {
		position: 'relative',
		display: 'flex',
		flex: 1,
	},
	abs: {
		display: 'flex',
		flexDirection: 'column',
		flex: 1,
		width: '100%',
		position: 'absolute',
		inset: 0,
		overflow: 'auto',
	},
	userDesignerGrid: {
		minWidth: `calc(${StyleConsts.paperRoleUserWidth} * 2 + 16px)`,
		display: 'flex',
		flex: 1,
		padding: '0px 8px 8px',
	},
	userDesignerGridItem: {
		display: 'flex',
		margin: '8px',
	},
	userListCard: {
		minWidth: StyleConsts.paperRoleUserWidth,
		padding: 0,
		display: 'flex',
		flexDirection: 'column',
		flex: 1,
		minHeight: '48px',
	},

	/*

    */
	userListRelative: {
		display: 'flex',
		flex: 1,
		flexDirection: 'column',
		position: 'relative',
		borderColor: theme.palette.divider,
		borderBottom: `1px solid ${theme.palette.divider}`,
	},
	userListBox: {
		overflow: 'auto',
		display: 'flex',
		flexDirection: 'column',
		position: 'absolute',
		top: 0,
		bottom: 0,
		left: 0,
		right: 0,
	},
	bottomButtons: {
		display: 'flex',
		alignItems: 'center',
		margin: '4px 16px',
	},
	bottomPageCounter: {
		padding: '8px 12px',
		background: `${theme.palette.divider}`,
		borderRadius: '8px',
		marginLeft: '12px',
	},
	titlebar: {
		backgroundColor: theme.palette.primary.main,
		color: theme.palette.primary.contrastText,
	},
	title: {
		flexGrow: 1,
		display: 'none',
		[theme.breakpoints.up('sm')]: {
			display: 'block',
		},
	},
	userAutocompleteDiv: {
		display: 'flex',
		maxWidth: '50%',
		flexGrow: 1,
	},
	roleAutocompleteDiv: {
		flexGrow: 1,
		marginRight: theme.spacing(1),
	},
	domainAutocomplete: {
		display: 'flex',
		flexGrow: 1,
		marginRight: '8px',
		minWidth: '200px',
	},
	roleHideSelect: {
		display: 'flex',
		margin: theme.spacing(1, 3, 0, 1),
		justifyContent: 'end',
	},
	userIcon: {
		marginRight: '18px',
	},
	filterTitle: {
		backgroundColor: theme.palette.primary.light,
		overflow: 'hidden',
		height: StyleConsts.headerHeight,
	},
	search: {
		position: 'relative',
		borderRadius: theme.shape.borderRadius,
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
	searchLight: {
		color: theme.palette.primary.contrastText,
		backgroundColor: alpha(theme.palette.common.white, 0.15),
		'&:hover': {
			backgroundColor: alpha(theme.palette.common.white, 0.25),
		},
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
			width: theme.spacing(0),
			'&:focus': {
				width: 200,
			},
		},
	},
	selectInput: {
		color: '#fff',
	},
	roleList: {
		padding: 0,
		flexGrow: 1,
	},
	/* addButton: {
        width: theme.spacing(20),
        height: theme.spacing(7),
        justifyContent: 'center'
    },*/
    selectDiv: {
        display: 'flex', 
        justifyContent: 'end', 
      //  margin: '16px 0px 0px 16px'
      },
    addButton: {
        minWidth: '120px'
      },
    spanBtn: {
        display: 'flex',
        minWidth: '400px',
        alignItems: 'center',
        paddingLeft: theme.spacing(2),
       height: '64px'
    },
    usersBtn:{
        margin: theme.spacing(0, 1),
    },
    btnText:{
        fontSize: 'unset',
        fontWeight: 'unset'
    },
    pagination: {
        display: 'flex',
        flexShrink: 0,
        marginLeft: theme.spacing(2),
       // width:'500px',
    },
    iconButton: {
        marginLeft: theme.spacing(1),
    },
    pageNumber: {
        width: '60px',
        height: '29.6px',
        backgroundColor: theme.palette.action.hover,
        marginLeft: theme.spacing(0.5),
        fontSize: '0.9rem'
    },
}));
