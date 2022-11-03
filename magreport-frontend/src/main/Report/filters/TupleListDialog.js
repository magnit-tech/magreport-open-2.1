import React, {useState, useRef, useEffect} from 'react';
import Button from '@material-ui/core/Button';
import Dialog from '@material-ui/core/Dialog';
import DialogActions from '@material-ui/core/DialogActions';
import DialogContent from '@material-ui/core/DialogContent';
import DialogTitle from '@material-ui/core/DialogTitle';
import Typography from '@material-ui/core/Typography';
import { DataGrid } from '@material-ui/data-grid';
import Paper from '@material-ui/core/Paper';
import Draggable from 'react-draggable';
import IconButton from '@material-ui/core/IconButton';
import Tooltip from '@material-ui/core/Tooltip';
import EditIcon from '@material-ui/icons/Edit';
import DeleteIcon from '@material-ui/icons/Delete';
//styles
import { TupleListDialogCSS } from './FiltersCSS'

function PaperComponent(props) {
    return (
      <Draggable handle="#form-tuple-list" cancel={'[class*="MuiDialogContent-root"]'}>
        <Paper {...props}/>
      </Draggable>
    );
  }

function TupleListDialog(props){
    let columns = props.columns.filter(i=>i.type ==='CODE_FIELD');
    let [reload, setReload] = useState(false);
    const [selectionModel, setSelectionModel] = useState([]);
    const rows = useRef(props.rows ?? []);

    useEffect(() => {
        rows.current = props.rows ?? [];
    }, [props.rows])

    const classes = TupleListDialogCSS(); 

    function handleClose(){
        props.onClose();
    }

    function hadleCellEditCommit(params, event){      
         
        rows.current[params.id].values[parseInt(params.field)].value = params.value;
    }

    function handleSave(){
        props.onSave(rows.current);
    }

    function handleDelRows(){
        function compareNumeric(a, b) {
            if (a > b) return -1;
            if (a === b) return 0;
            if (a < b) return 1;
          }

        for (let r of selectionModel.sort(compareNumeric)){
            rows.current.splice(r, 1);
        }
        setReload(!reload);
    }

    function handleAddRow(){
        rows.current.push({values: columns.map(i => {return {fieldId: i.id, value: ''}})});
        setReload(!reload);
    }
    
    return(
        <Dialog 
            open={props.isOpen} 
            onClose={handleClose} 
            PaperComponent={PaperComponent}
            aria-labelledby="form-tuple-list" 
            PaperProps={{ classes: {root: classes.root }}}
        >
            <DialogTitle  style={{ cursor: 'move' }} id="form-tuple-list">
                <Typography>Таблица значений </Typography>
            </DialogTitle>

            <DialogContent style={{display: 'flex', flex: 1, flexDirection: 'column'}}>
                <div>
					<Tooltip title="Добавить строку" placement="top">
						<IconButton 
							aria-label="edit"
							size='small'
							onClick={handleAddRow}
						>
							<EditIcon fontSize='small' />
						</IconButton>
					</Tooltip>
					<Tooltip title="Удалить строки" placement="top">
						<IconButton 
							aria-label="delete"
							size='small'
							onClick={handleDelRows}
						>
							<DeleteIcon fontSize='small' />
						</IconButton>
					</Tooltip>
                </div>
               
                <DataGrid
                    rows={rows.current.map((v,i)=>v.values.reduce((res, item, index)=> { return { ...res, [index.toString()]: item.value}}, {id: i} ))}
                    columns={columns.map((v, i)=> {return ({
                            field: i.toString(),
                            headerName: v.name,
                            description: v.description,
                            width: 150,
                            editable: true,
                        }  
                    )})}
                    checkboxSelection
                    onSelectionModelChange={(newSelectionModel) => {
                        setSelectionModel(newSelectionModel);
                      }}
                    selectionModel={selectionModel}
                    disableSelectionOnClick
                    onCellEditCommit = {(p, e) => hadleCellEditCommit(p, e)}
                />          
            </DialogContent>
            <DialogActions className={classes.indent}> 
                <Button                     
                    type="submit"
                    variant="contained"
                    color="primary"
                    size="small"
                    onClick={handleSave}>Сохранить
                </Button>
                <Button
                    type="submit"
                    variant="contained"
                    color="primary"
                    size="small"
                    onClick={handleClose}>Отменить
                </Button>
            </DialogActions>
        </Dialog>
    );
}

export default TupleListDialog;