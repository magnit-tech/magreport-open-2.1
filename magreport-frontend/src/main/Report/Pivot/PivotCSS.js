import { withStyles, makeStyles, alpha } from '@material-ui/core/styles';

const gridWidth = '100%';
const gridHeight = '100%';

export const FieldForDrag =  withStyles({

})

export const PivotCSS = makeStyles(theme => ({
    verticalList: {
        margin : '2px',
        border : '1px solid',
        borderColor: theme.palette.divider,
        borderRadius : '2px',
        padding: '2px'
    },
    verticalListTableCell: {
        verticalAlign : 'top',
        maxWidth: '100%', 
        display: 'grid', 
        //gridTemplateColumns: '1fr 1fr 48px'
    },
    horRel: {
        position: 'relative',
         minHeight: '10px', 
         maxHeight: '68px',
         height: '100px'
    },
    horizontalList: {
        margin : '2px',
        border : '1px solid',
        borderColor: theme.palette.divider,
        borderRadius : '2px',
        padding: '2px',
        overflow: 'auto',
        maxHeight: '80px'
    },
    listDraggingOver:{
        backgroundColor : theme.palette.action.hover
    },
    allFieldsBtn:{
        position: 'absolute', 
        top: '2px', 
        right: '16px', 
        zIndex: 1, 
        width: '8px', 
        height: '4px', 
        padding: '4px', 
        backgroundColor: theme.palette.background.paper
    },
    popover: {
        pointerEvents: 'none'
    },
    fieldTextHover: {
        height: 'fit-content',
        minHeight: '56px',
        textAlign: 'center',
        display: 'table-cell',
        verticalAlign: 'middle'
    },
    paper: {
        minWidth: '48px',
        padding : '2px',
        border : '1px solid',
        borderColor: theme.palette.divider,
        borderRadius : '8px',
        backgroundColor : theme.palette.drawerColor,
        maxWidth: '200px',
    },
    offPaper: {
        minWidth: '48px',
        padding : '2px',
        border : '1px solid',
        borderColor: theme.palette.divider,
        borderRadius : '8px',
        backgroundColor : theme.palette.pivotTable.grey ,//theme.palette.action.selected,
        maxWidth: '200px',
    },
    offField: {
        margin : '2px',
        padding : '2px',
        border : '1px solid',
        borderColor: theme.palette.divider,
        borderRadius : '8px',
        backgroundColor : theme.palette.pivotTable.grey, //theme.palette.action.selected,
        justifyContent : 'center',
        alignItems: 'center',
        width : 'fit-content',
        maxWidth: '200px',
    },
    field: {
        margin : '2px',
        padding : '2px',
        border : '1px solid',
        borderColor: theme.palette.divider,
        borderRadius : '8px',
        backgroundColor : theme.palette.drawerColor,
        justifyContent : 'center',
        alignItems: 'center',
        width : 'fit-content',
        maxWidth: '200px',
    },
    fieldText: {
        maxHeight: '40px',
        overflow: 'hidden',
        display: 'block',
        position: 'relative',
    },
    popoverDiv: {
        textAlign: 'center', 
        minHeight: '56px', 
        display: 'table'
    },
    listItemText: {
        minWidth: '40px', 
        textAlign: 'center'
    },
    listItemOffText: {
        minWidth: '40px', 
        textAlign: 'center',
        color: theme.palette.text.disabled
    },
    divFilterButton:{
        position: 'absolute', 
        top: '-2px', 
        right: '-12px', 
        borderRadius: '50%', 
        backgroundColor: 'cadetblue',
        height: '16px', 
        width: '16px'
    },
    divFilterOffButton:{
        position: 'absolute', 
        top: '14px', 
        right: '-12px', 
        borderRadius: '50%', 
        backgroundColor: 'cadetblue',
        height: '16px', 
        width: '16px'
    },
    filterButton: {
        borderRadius: '50%', 
        height: '14px', 
        width: '14px',
        padding: '1px',
        display: 'block'
    },
    draggingField: {
        borderWidth : '2px',
    },
    gragDropDiv: {
        display: 'flex', 
        flex: 1, 
        position: 'relative'
    },
    dragDropGridContainer:{
        display: 'grid', 
        gridTemplateColumns: 'auto 1fr', 
        gridTemplateRows: 'auto 1fr',
        position: 'absolute', 
        inset: 0
    },
    gridMeasure: {
        maxHeight: '100%', 
        maxWidth: '100%', 
        minHeight: '20px', 
        minWidth: '20px'
    },
    gridForFilters: {
        display: 'flex' , 
        maxWidth: '100%'
    },
    gridForTools: {
        maxWidth: '100%', 
        display: 'grid', 
        gridTemplateRows: '32px'
    },
    cell: {
        border: '1px solid black',
        width: '100px'
      //  borderStyle : 'solid',
      //  borderColor : "black"
    },
    leftTopCornerCell : {
    },
    tr: {
        "&:nth-child(odd)" :{
            backgroundColor: theme.palette.background.paper
        },
        "&:nth-child(even)":{
                backgroundColor: theme.palette.pivotTable.grey,
            }
    },
    tr1: {
        backgroundColor: theme.palette.background.paper
    },
    tr2: {
        backgroundColor:  theme.palette.pivotTable.grey
    },
    tr3: {
        backgroundColor: theme.palette.pivotTable.dimentionValue
    },
    blanc : {
    },
    dimensionValueCell: {
        align : 'center',
        cursor : 'pointer',
        width: '100px'
    },
    dimensionNameCell: {
        backgroundColor : theme.palette.drawerColor,
        textAlign: 'center',
        width: '100px'
    },
    metricNameCell: {
        backgroundColor : theme.palette.drawerColor,
        textAlign: 'center',
        width: '100px'
    },
    metricValueCell: {
        width: '100px',
        cursor : 'pointer',
        "&:hover" :{
            "& .metricValueCellArrowsWrapp" :{
                display: 'contents'
            },
        },
    },
    metricValueCellArrowsWrapp: {
        display: 'none',
        zIndex: 999,
    },
    metricValueCellArrow: {
        "& path" :{
            transition: 'all .3s ease',
            fill: `${alpha(theme.palette.active, 0.45)}!important`
        },
        "&:hover" :{
            "& path" :{
                fill: `${theme.palette.active}!important`
            },
        },
    },
    pivotTable: {
        width : gridWidth,
        height : gridHeight,
        overflow: 'auto'
    },
    modal: {
        position: 'absolute',
        backgroundColor: theme.palette.background.paper,
        border: '2px solid #000',
        boxShadow: theme.shadows[5],
        borderRadius: '4px',
        padding: '0px 8px'
    },
    dialog: {
        backgroundColor: theme.palette.background.default
    },
    panelNameVertical: {
        width: '10px', 
        wordWrap: 'break-word',
        margin: '8px auto',
        color: theme.palette.action.disabled
    },
    panelNameHorizontal: {
        color: theme.palette.action.disabled,
        margin: '0px auto',
        letterSpacing: '8px'
    },
    rangeMainHorizontal: {
        display: 'flex', 
        alignItems: 'center'
    },
    rangeMainVertical: {
        display: 'grid', 
        gridTemplateRows: '32px 20px 1fr 30px 32px', 
        justifyItems: 'center'
    },
    rangeP: {
        margin: 0
    },
    rangeBtn: {
        width: 'fit-content'
    },
    sliderRoot: {
        justifySelf: 'center'
    },
    thumb: {
        marginTop: '-4px',
        '&:focus, &:hover, &$active': {
            boxShadow: 'inherit',
        },
    },
    track: {
        height: 4,
        borderRadius: 4,
    }, 
    rail: {
        height: 4,
        borderRadius: 4,
    },
    valueLabel: {
      //  height: '24px',
        width: 'fit-content',
       // left: 'calc(-50% + 12px)',
        top: 0,
        backgroundColor: theme.palette.background.default,
        border: '2px solid',
        borderColor: theme.palette.primary.main,
        borderRadius: '25%', //'50%',
        padding: '0px 8px',
        '& *': {
          background: 'transparent',
          color: '#000',
        },
      },
    formControl: {
        margin: '0px 16px',
        width: '408px'
    },
    pivotFiltersRoot: {
        display: 'flex',
        flex: 1,
        flexDirection: 'column'
    },
    divLinkNo:{
        display: 'flex', padding: '8px 0px'
    },
    divFlex: {
        display: 'flex'
    },
    equalText: {
        width: '100%',
        padding: '8px 16px 8px 36px'
    },
    betweenText: {
        width: '100%',
        padding: '8px 16px'
    },
    betweenTitle: {
        padding: '8px 0px 0px'
    },
    opTypeAria: {
        margin: '8px'
    },
    invertResult: {
        color: theme.palette.secondary.main,
        fontWeight: 'bold'
    },
    notInvertResult:{
        color: theme.palette.action.disabled,
        textDecoration: 'line-through',
        fontWeight: 'bold'
    },
    btnsArea: {
        display: 'flex',
        justifyContent: 'end',
        margin: '0px 16px 8px'
    },
    cancelBtn: {
        marginLeft: '16px'
    },
    inListDialog: {
        height: '90vh'
    },
    //TransferList
    gridRoot: {
        display: 'flex',
        flex: 1,
        marginTop: '8px'
    },
    gridColumn: {
        display: 'flex', 
        flex: 1, 
        height: '100%',
        minWidth: '460px'
    },
    gridBtnColumn: {
        display: 'flex', 
        flex: 1,  
        height: '100%', 
        alignItems: 'center',
        maxWidth: '80px',
        minWidth: '80px'
    },
    transferHeader: {
        display: 'flex',
        flex: 1,
        flexDirection: 'column',
    },
    divPag: {
        display: 'flex', 
        flexWrap: 'wrap'
    },
    listRel: {
        display: 'flex', 
        flex: 1, 
        flexDirection: 'column', 
        position: 'relative'
    },
    divProgress: {
        height: '100%', 
        display: 'flex', 
        justifyContent: 'center', 
        alignItems: 'center'
    },
    rootTrList: {
        display: 'flex',
        flex: 1,
        flexDirection: 'column'
    },
    cardHeaderTrList: {
        padding: theme.spacing(1, 2),
        backgroundColor: theme.palette.primary.main
    },
    listTrList: {
        width: '100%',
      //  minHeight: 100,
        backgroundColor: theme.palette.background.paper,
        overflow: 'auto',
        display: 'flex',
        flexDirection: 'column',
        position: 'absolute',
        inset: 0

    },
    buttonTrList: {
        margin: theme.spacing(0.5, 0),
    },
    dialogTitle: {
        cursor: 'move'
    },
    // FormattingDialog
    FD_metrcisNameWrapper: {
        padding: '0px 24px 24px'
    },
    FD_metrcisName: {
        padding: '18px', 
        borderRadius: '8px', 
        backgroundColor: theme.palette.drawerColor,
    },
    FD_root: {
        flexGrow: 1,
        width: '100%',
        backgroundColor: theme.palette.background.paper,
    },
    FD_wrapperForActionSections: {
        display : 'flex',
        alignItems: 'center',
        marginTop: '20px',
    },
    // FD number
    FD_inputNumberWithArrows: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        marginLeft: '8px',
    },
    FD_inputForNumber: {
        width: '35px',
        textAlign: 'center'
    },
    FD_wrapperForPercent: {
        marginTop: '12px',
        display: 'flex',
        alignItems: 'center'
    },
    // FD font
    FD_inscriptionBtns: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        marginLeft: '12px',
    },
    FD_inscriptionBtn: {
        backgroundColor: theme.palette.pivotTable.dimentionValue
    },
    FD_clearInscriptionBtn: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        marginLeft: '18px',
        '& label': {
            marginRight: 0
        }
    },
    FD_fontSizeSelect: {
        marginLeft: '12px'
    },
    FD_fontColorSection: {
        display: 'flex', 
        alignItems: 'center',
    },
    FD_fontColorWrapper: {
        display: 'flex', 
        alignItems: 'center',
        marginLeft: '10px'
    },
    FD_fontColorCircle: {
        position: 'relative',
        flexShrink: 0,
        width: '40px',
        height: '40px',
        borderRadius: '50%',
        border: '1px solid black',
        marginRight: '15px',
        cursor: 'pointer'
    },
    FD_fontColorCircleNotAlowed: {
        position: 'relative',
        flexShrink: 0,
        width: '40px',
        height: '40px',
        borderRadius: '50%',
        border: '1px solid black',
        marginRight: '15px',
        cursor: 'not-allowed',
        opacity: '0.7'
    },
    FD_fontColorCircleSVG: {
        position: 'relative',
        flexShrink: 0,
        width: '40px',
        height: '40px',
        borderRadius: '50%',
        border: '1px solid black',
        marginRight: '15px',
        cursor: 'pointer',
    },
    FD_fontColorCircleSVGNotAlowed: {
        position: 'relative',
        flexShrink: 0,
        width: '40px',
        height: '40px',
        borderRadius: '50%',
        border: '1px solid black',
        marginRight: '15px',
        cursor: 'not-allowed',
        opacity: '0.7'
    },
    FD_autoFontColorSection: {
        marginLeft: '15px'
    },
    // ConditionalFormattingDialog
    CFD_wrapper: {
        display: 'flex'
    },
    CFD_item: {
        width: '100%',
        height: '30px', 
        cursor: 'pointer', 
        border: `5px solid ${theme.palette.pivotTable.grey}`,
        '&.active': {
            border: `5px solid ${theme.palette.primary.dark}`
        },
    },
    CFD_choiceBlock: {
        marginTop: '16px',
        display: 'flex',
        flexDirection: 'column'
    },
    CFD_errorChoiceBlock: {
        opacity: '0.5',
        cursor: 'default',
    },
    CFD_values: {
        marginTop: '10px',
        display: 'flex',
        justifyContent: 'space-around',
    },
    CFD_errorValues: {
        display: 'flex',
        flexDirection: 'column',
        '& span': {
            color: theme.palette.error.main
        },
    },
    CFD_fontColor: {
        marginTop: '16px'
    },
    CFD_actionBtns: {
        display: 'flex',
        justifyContent: 'flex-end'
    },
    CFD_deleteBtn: {
        marginRight: '8px',
        padding: '8px 12px',
        color: theme.palette.error.main,
        border: `1px solid ${theme.palette.error.main}`,
        borderRadius: '8px',
        background: 'inherit',
        cursor: 'pointer',
    },
    CFD_divideBtn: {
        padding: '8px 12px',
        border: `1px solid ${theme.palette.primary.dark}`,
        borderRadius: '8px',
        background: 'inherit',
        cursor: 'pointer',
    },
    // SortingDialog
    SD_root: {
        flexGrow: 1,
        width: '100%',
        backgroundColor: theme.palette.background.paper,
        padding: '18px'
    },
    SD_root_without_action: {
        flexGrow: 1,
        width: '400px',
        backgroundColor: theme.palette.background.paper,
        padding: '18px'
    },

    SD_deleteSortingBtn: {
        marginTop: '8px',
        display: 'flex'
    },
    SD_deleteSortingBtnLabel: {
        fontSize: '12px',
        marginLeft: '8px'
    },
    SD_inputText_without_action: {
        marginBottom: '30px'
    },
    // ConfigSaveDialog
    CSD_wrapperList: {
        backgroundColor: theme.palette.drawerColor,
        padding: '15px',
        borderRadius: '8px',
    },
    CSD_actionBtns: {
        backgroundColor:  theme.palette.pivotTable.grey,
        padding: '8px 12px',
        borderRadius: '8px',
        marginTop: '16px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
    },
    CSD_wrapperSaveFor:{
        display: 'flex',
        alignItems: 'center'
    },
    CSD_formControl: {
        width: '100%',
        margin: 0
    },
    CSD_nameField: {
        width: '100%',
        margin: 0
    },
    CSD_nameFieldExisting: {
        width: '100%',
        margin: '40px 0 0'
    },
    CSD_descriptionField: {
        width: '100%',
        margin: '24px 0 0'
    },
    CSD_saveFor: {
        marginRight: '8px'
    },
    //ConfigDialog
    CD_root: {
        flexGrow: 1,
        width: '100%',
        backgroundColor: theme.palette.background.paper,
        padding: '0px 24px'
    },
    CD_item: {
        marginBottom: '8px',
        padding: '15px',
        cursor: 'pointer'
    },
    CD_wrapper: {
        marginBottom: '15px'
    },
    CD_wrapperList: {
        backgroundColor: theme.palette.drawerColor,
        padding: '15px',
        borderRadius: '8px',
        textAlign: "center"
    },
    CD_subtitle: {
        padding: "12px",
        backgroundColor: theme.palette.drawerColor,
        borderRadius: "8px",
        marginBottom: "4px"
    },
    //CustomList
    CL_listItem: {
        marginBottom: '12px', 
        borderBottom: `1px solid ${theme.palette.divider}`
    },
    //ShareJob
    SJ_gridItem: {
        width: '370px'
    },
    SJ_titlebar: {
        backgroundColor: theme.palette.primary.main,
        color: theme.palette.primary.contrastText,
        padding: '8px 16px',
        height: '100px'
    },
    SJ_title: {
        display: 'none',
        fontSize: '1.2rem',
        marginBottom: '14px',
        [theme.breakpoints.up('sm')]: {
            display: 'block',
        },
        alignSelf: 'start'
    },
    SJ_titleSubButtons: {
        width: '100%',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
    SJ_search: {
        position: 'relative',
        borderRadius: theme.shape.borderRadius,
        color: theme.palette.primary.contrastText,
        backgroundColor: alpha(theme.palette.common.white, 0.15),
        '&:hover': {
          backgroundColor: alpha(theme.palette.common.white, 0.25),
        },
        marginLeft: 0,
        width: '100%',
        [theme.breakpoints.up('sm')]: {
          marginLeft: theme.spacing(1),
          width: 'auto',
        },
    },
    SJ_searchIcon: {
        width: theme.spacing(7),
        height: '100%',
        position: 'absolute',
        pointerEvents: 'none',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
    },
    SJ_inputRoot: {
        color: 'inherit',
    },
    SJ_inputInput: {
        padding: theme.spacing(1, 1, 1, 7),
        transition: theme.transitions.create('width'),
        width: '100%',
        [theme.breakpoints.up('sm')]: {
          width: theme.spacing(12), //96px,
          '&:focus': {
            width: 150,
          },
        },
        [theme.breakpoints.down('md')]: {
            width: theme.spacing(0), 
            '&:focus': {
              width: 100,
            },
          },
    },
    SJ_listItems: {
        height: '490px',
        overflow: 'auto',
        borderColor: theme.palette.divider,
        borderBottom: `1px solid ${theme.palette.divider}`,
    },
    SJ_bottomButtons:{
        margin: '14px',
        height: '27px'
    },
    SJ_iconButton: {
        color: 'white'
    }
}))
