import React from 'react';

// components
import ListItem from "@material-ui/core/ListItem";

// local
import ReportFieldItem from './ReportFieldItemViewer';

/**
 * Компонент просмотра полей отчета
 * @param {Object} props - свойства компонента
 * @param {Array} props.fields - поля отчета
 * @param {Object} props.dataSet - набор данных
 * @return {JSX.Element}
 * @constructor
 */

export default function ReportFieldsViewer(props) {
    const sortedArr = () => {
        let arr = []

        props.fields.forEach((_, index) => {             
            for (let item of props.fields) {
                if (item.ordinal === index) {
                    arr.push(item)
                }
            }
        })

        return arr
    }

    return (
        <div>
            {sortedArr().map((item, index) =>
                <ListItem key={item.id}>
                    <ReportFieldItem
                        key={item.id}
                        name={item.name}
                        visible={item.visible}
                        dataSetFieldId={item.dataSetFieldId}
                        valid={item.valid}
                        description={item.description}
                        open={item.open}
                        index={index}
                        dataSetFields={props.dataSet.fields}
                    />
                </ListItem>
            )
            }
        </div>
    )
}
