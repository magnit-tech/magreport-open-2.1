import React from 'react'
import clsx from 'clsx';
import { PivotCSS } from '../../../PivotCSS';

export default function DerivedFieldDialogModal(){
    const classes = PivotCSS();

	return (
		<div className={classes.DFD_modal}>
			<div className={clsx(classes.DFD_modalBox, 'MuiPaper-root')}>
				<p className={classes.DFD_modalText}>Выберите доступное поле или создайте новое</p>
			</div>
		</div>
	)
}