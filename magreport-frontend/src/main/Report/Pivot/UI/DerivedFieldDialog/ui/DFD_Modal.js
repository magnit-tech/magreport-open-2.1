import React from 'react'
import { PivotCSS } from '../../../PivotCSS';

export default function DerivedFieldDialogModal(){
    const classes = PivotCSS();

	return (
		<div className={classes.DFD_modal}>
			<div className={classes.DFD_modalBox}>
				<p className={classes.DFD_modalText}>Выберите доступное поле или создайте новое</p>
			</div>
		</div>
	)
}