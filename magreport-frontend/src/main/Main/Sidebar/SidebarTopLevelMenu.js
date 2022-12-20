import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';

import { useLocation, useNavigate} from 'react-router-dom';

import { routesName } from 'router/routes';

// material-ui
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import Paper from '@material-ui/core/Paper';
import Collapse from '@material-ui/core/Collapse';

//actions
import { foldersTreeToggle } from '../../../redux/actions/sidebar/actionFolderTree';

// local
import FolderTree from './FolderTree/FolderTree';
import SidebarSubMenu from './SidebarSubMenu'

// css
import { SidebarCSS } from './SidebarCSS';
import { useRef } from 'react';

/**
 * 
 * @param {*} props.sidebarItem - объект пункта меню верхнего уровня в SidebarItems
 */
function SidebarTopLevelMenu(props){

    const classes = SidebarCSS();

    const location = useLocation()
    const navigate = useNavigate()
    const locationName = useRef(null)

    const [menuExpanded, setMenuExpanded] = useState(false);

    useEffect(() => {
        const arr = props.sidebarItem.subItems ? Object.values(props.sidebarItem.subItems) : []

        routesName.forEach(name => {
            if (location.pathname.indexOf(name) !== -1) {
                locationName.current = name
            }
        })
        if (props.sidebarItem.folderItemType === locationName.current) {
            return setMenuExpanded(true)
        } else {
            arr.forEach(item => {
                if (item.folderItemType === locationName.current) {
                    return setMenuExpanded(true)
                }
            })
        }
        // if (props.sidebarItem.folderItemType === props.path) {
        //     console.log(props.sidebarItem.folderItemType);
        //     console.log(props.path);
        //     return setMenuExpanded(true)
        // } else {
        //     arr.forEach(item => {
        //         if (item.folderItemType === locationName.current) {
        //             return setMenuExpanded(true)
        //         }
        //     })
        // }

        // return setMenuExpanded(false)

    }, [props.sidebarItem, props.sidebarItem.subItems, location.pathname])

    function handleClick(){
        if(!props.sidebarItem.subItems && locationName.current !== props.sidebarItem.folderItemType){
            navigate(props.sidebarItem.folderItemType)
        }
    }

    function handleSubitemClick(item){
        // console.log(item);
        // props.actionSetSidebarItem(item);
    }

    function handleExpand(){
        if(props.sidebarItem.folderTree){
            foldersTreeToggle(props.sidebarItem.key, []);
        }
        if (props.sidebarItem.folderTree || props.sidebarItem.subItems){
            setMenuExpanded(!menuExpanded);
        }
        
        handleClick();
    }

    return (
        <React.Fragment>       
			<List className={classes.listClassMain}>
				<Paper key={props.sidebarItem.key} className={classes.paperRoot} elevation={5}>
					<ListItem 
						// className={classes.listItem + ' ' + (location.pathname.indexOf(locationName.current) !== -1 ? classes.folderListItemActive : null)} 
						className={classes.listItem + ' '} 
                        // className={classes.listItem + ' ' + (menuExpanded ? classes.folderListItemActive : null)}
						onClick={(props.drawerOpen ? handleExpand : handleClick)} button key={props.sidebarItem.key}
					>
						{props.sidebarItem.icon && <ListItemIcon className={classes.listIconClass} >{props.sidebarItem.icon}</ListItemIcon>}
						<ListItemText primary={props.drawerOpen ? props.sidebarItem.text : ''} />
					</ListItem>
				</Paper>
			</List>
            <div>
				<Collapse in={props.drawerOpen && menuExpanded} timeout="auto" unmountOnExit>
					{props.sidebarItem.folderTree &&
						<FolderTree entity={props.sidebarItem} menuExpanded={menuExpanded} folderItemType={props.sidebarItem.folderItemType}/>
					}
					{props.sidebarItem.subItems && 
						<List>
							{
							Object.values(props.sidebarItem.subItems).map( item => (
								<SidebarSubMenu 
									key={item.key} 
									drawerOpen={props.drawerOpen} 
                                    item={item}
                                    focus={menuExpanded}
									onClick={handleSubitemClick}/>
							))}
						</List>
					}
				
				</Collapse>
			</div>
        </React.Fragment>
    );
}

const mapStateToProps = state => (
    {
        drawerOpen : state.sidebar.drawerOpen,
        currentSidebarItemKey : state.sidebar.currentSidebarItemKey,
        state: state,
    }
)

const mapDispatchToProps = {
    foldersTreeToggle,
}

export default connect(mapStateToProps, mapDispatchToProps)(SidebarTopLevelMenu);
