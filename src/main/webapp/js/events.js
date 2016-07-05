/**
 *  Captures all the events in the application such as click, selected, mouse movements, etc.
 */

function relationSelected(e){
	toD3jRadialFormatSelectedRelation($('#termSearch').val(),e.currentTarget.value);
}