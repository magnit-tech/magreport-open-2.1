import { makeStyles } from '@material-ui/core/styles';
import StyleConsts from '../../../StyleConsts';
import CatFingerPng from '../../../images/catFinger.png';
//import RosesPng from '../../../images/roses.png';

export const HeaderCSS = makeStyles(theme => ({
	appBar: {
		backgroundColor: theme.palette.primary.dark,
		height: StyleConsts.headerHeight,
		overflowX: 'hidden',
		transform: 'translateZ(0)',
		whiteSpace: 'nowrap',
		justifyContent: 'end',
	},
	appBarHeight: {
		height: StyleConsts.headerHeight,
	},
	appBarHeightHollyday: {
		height: StyleConsts.headerHeightHollyday,
	},
	iconIndent: {
		display: 'flex',
		justifyContent: 'space-between',
		paddingLeft: '12px',
	},
	logo: {
		display: 'flex',
		textDecoration: 'none',
	},
	logoText: {
		flexGrow: 1,
		textAlign: 'left',
		color: theme.palette.primary.contrastText,
		fontWeight: theme.typography.fontWeightMedium,
		fontSize: '1rem',
		margin: '10px',
	},
	iconButton: {
		color: theme.palette.primary.contrastText,
		zIndex: 1110,
	},
	userNameClass: {
		margin: theme.spacing(0, 1),
	},
	catFinger: {
		backgroundImage: 'url(' + CatFingerPng + ')',
		backgroundSize: 'contain',
		backgroundRepeat: 'no-repeat',
		backgroundPosition: 'center',
		//marginTop : '48px',
		height: '140px',
		width: '140px',
	},
/*	roses: {
		backgroundImage: 'url(' + RosesPng + ')',
		backgroundSize: 'contain',
		backgroundRepeat: 'round',
		backgroundPosition: 'center',
		//marginTop : '48px',
		height: '100%',
	}
*/
}));
