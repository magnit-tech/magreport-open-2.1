import React, {useState} from 'react'
import PropTypes from 'prop-types';

// components
import Menu from '@material-ui/core/Menu';
import MenuItem from '@material-ui/core/MenuItem';
import Tooltip from '@material-ui/core/Tooltip/Tooltip';
import IconButton from '@material-ui/core/IconButton';
import ArrowDropDownIcon from '@material-ui/icons/ArrowDropDown';
import StarRateIcon from '@material-ui/icons/StarRate';
import { Box } from '@material-ui/core';


export default function ReportTemplatesList({excelTemplates, isDisabled, onSelectTemplate}) {

    const [anchorEl, setAnchorEl] = useState(null);

    const handleSelectTemplate = id => {
        setAnchorEl(null)
        onSelectTemplate(id)
    }

    return (
        <span>
            <Menu
                id="folderMenu"
                anchorEl={anchorEl}
                keepMounted
                open={Boolean(anchorEl)}
                onClose={() => setAnchorEl(null)}
            >
                {
                    excelTemplates.map(i => 
                        <MenuItem key={i.excelTemplateId} onClick={() => handleSelectTemplate(i.excelTemplateId)}>
                            {i.default 
                            ? 
                                <Tooltip title="Шаблон по-умолчанию">
                                    <Box
                                        display="flex"
                                        alignItems="center"
                                    >
                                        <StarRateIcon fontSize="small" style={{color: "yellow"}} /> 
                                        <span style={{marginLeft:"8px"}}>
                                            {i.name}
                                        </span> 
                                    </Box>
                                </Tooltip>
                            :   <span style={{marginLeft: "25px"}}>{i.name}</span>
                            }
                        </MenuItem>)
                }
                
            </Menu>
            <Tooltip title="Дополнительные шаблоны экспорта">
                <span>
                    <IconButton
                        size="small"
                        aria-label="export"
                        onClick={event => setAnchorEl(event.currentTarget)}
                        disabled={isDisabled}
                    >
                        <ArrowDropDownIcon />
                    </IconButton>
                </span>
            </Tooltip>
        </span>
    )
}

ReportTemplatesList.propTypes = {
    excelTemplates: PropTypes.array.isRequired,
    isDisabled: PropTypes.bool.isRequired,
    onSelectTemplate: PropTypes.func.isRequired,
};