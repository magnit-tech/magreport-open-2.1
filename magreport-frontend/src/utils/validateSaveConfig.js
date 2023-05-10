/**
 * Проверка на валидацию сохраненных полей конфигурации olapConfig с полями из Metadata
 * @param {object} responseConfigData - объект c значениями fieldsLists из сохраненной конфигурации
 * @param {array} allFields - объект c полями, загруженных из Metadata
 */

export default function validateSaveConfig(responseConfigData, allFields, derivedFields, callback) {
	const allFieldsWithDerived = [...derivedFields, ...allFields]
	let configsIdsArr = []
	let otherDerivedFields = []

	for (var key in responseConfigData) {
		if(key !== 'derivedFields' && responseConfigData[key].length !== 0 && Array.isArray(responseConfigData[key])) {
			responseConfigData[key].map( item => item.original ? configsIdsArr.push(item.id) : otherDerivedFields.push(item.id))
		}
	}

	const resultArray = configsIdsArr.map( (id) => {
		return allFieldsWithDerived.some( (fieldData) => {
			return fieldData.id === id
		})
	})

	return callback(!resultArray.includes(false), otherDerivedFields) // если ответ false значит в responseConfigData нет отличающих значений с allFieldsWithDerived
}