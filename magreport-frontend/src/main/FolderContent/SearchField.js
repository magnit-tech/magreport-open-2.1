import React, {useState, useEffect} from 'react';

// components
import SearchIcon from '@material-ui/icons/Search';
import FormControl from '@material-ui/core/FormControl';
import InputAdornment from '@material-ui/core/InputAdornment';
import IconButton from '@material-ui/core/IconButton';
import Paper from '@material-ui/core/Paper';
import ClearIcon from '@material-ui/icons/Clear';
//import RepeatIcon from '@material-ui/icons/Repeat';
import KeyboardArrowUpIcon from '@material-ui/icons/KeyboardArrowUp';
import InputBase from '@material-ui/core/InputBase';
import Divider from '@material-ui/core/Divider';
import Tooltip from '@material-ui/core/Tooltip';
import Slide from '@material-ui/core/Slide';
//import TuneOutlinedIcon from '@material-ui/icons/TuneOutlined';

// styles 
import { SearchFieldCSS } from "./FolderContentCSS";

export default function SearchField(props){
    const classes = SearchFieldCSS();

    const [searchParams, setSearchParams] = useState({})

    const handleClickSearch = () => {
        if (searchParams.searchString && searchParams.searchString.trim().length > 0){
            setSearchParams(searchParams);
            props.onSearchClick(searchParams)
        }
    };

    function handleClickClearSearch(){
        props.onSearchClick({
            searchString: "",
            isRecursive: false
        })
        setSearchParams({...searchParams, searchString: "", isRecursive: false })
    }
        

    useEffect(() => {
        if(props.searchParams) {
            setSearchParams(props.searchParams)
        }
    },[props.searchParams])

    const handleChange = (name, value) => {
        setSearchParams({...searchParams, [name]: value})
    }

    return (
        <div className={classes.searchDiv}>
        {
            !props.searchOpen 
            ?
            <div/>
            : 
            <Slide direction="down" in={props.searchOpen} mountOnEnter unmountOnExit>
                <Paper elevation={3} className={classes.searchRoot}>
                    <FormControl
                        className={classes.searchControl}
                    >
                        <InputBase
                            id="searchfield"
                            placeholder = 'Поиск'
                            value={searchParams.searchString || ''}
                            classes={{
                                root: classes.inputRoot,
                                input: classes.inputInput,
                            }}
                            onChange={event => handleChange("searchString", event.target.value)}
                            onKeyDown={(event) => {
                                if (event.keyCode === 13) {
                                    handleClickSearch()
                                }
                            }}
                            endAdornment={
                                <InputAdornment position="end">
                                    <Tooltip title = "Найти" placement="top" disabled={searchParams.searchString === undefined || searchParams.searchString === null || searchParams.searchString.trim() === ""}>
                                        <IconButton
                                            size="small"
                                            aria-label="search"
                                            onClick={() => {
                                                handleClickSearch()
                                            }}
                                        >
                                            <SearchIcon color={props.searchParams && props.searchParams.searchString ? "secondary" : "inherit"}/>
                                        </IconButton>
                                    </Tooltip>
                                    <Divider className={classes.divider} />
                                    <Tooltip title = "Очистить" placement="top">
                                        <IconButton
                                            size="small"
                                            aria-label="clear"
                                            onClick={() => {handleClickClearSearch(true)}} 
                                        >
                                            <ClearIcon />
                                        </IconButton>
                                    </Tooltip>
                                    <Divider className={classes.divider} />
                                    <Tooltip title = "Спрятать" placement="top">
                                        <IconButton
                                            size="small"
                                            aria-label="hide"
                                            onClick={props.setSearchOpen}
                                        >
                                            <KeyboardArrowUpIcon/>
                                        </IconButton>
                                    </Tooltip>
                                </InputAdornment>
                            }
                        />
                    </FormControl>
                </Paper>
            </Slide>
        }
    </div>
    )
}

