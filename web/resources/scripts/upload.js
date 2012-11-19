<!--
// @require prototype.js

// global object, no good...TODO: move all functions to class
var updater;

// main function for upload monitoring
function submitPostUsingAjax(id)
{
	try
	{
		var isFirstCall = true;
		// get upload status from Prototype Ajax object
		updater = new Ajax.PeriodicalUpdater(
			'uploadStatusDiv',
			'${facesContext.externalContext.requestContextPath}/upload?query=3',
			{
				asynchronous:true,
				frequency:1,
				method:'get',
				onSuccess:function(request) {
					if (isFirstCall)
					{
						isFirstCall=false;
						var div = document.createElement('div')
						div.id = 'uploadStatusDiv_1';
						div.style.width = '0px';
						div.style.hright = '0px';
						divstyle.visibility = 'hidden';
/*
			<div id="uploadStatusDiv_1" style="width:0px; height:0px; visibility:hidden;"/>

			<div style="width:33%; border:1px; background-color:white;">
				<table id="progressBar_1" width="0px" class="progressBar" style="visibility:hidden; ">
				<tr>
					<td>
						<div id="statusText_1" class="statusText" style="height:16px; overflow:hidden;"/>
					</td>
				</tr>
				</table>
			</div>
*/

						$('progressBar_' + id).style.visibility = 'visible';
						$('progressBar_' + id).style.width = '1%';
					} else {
						if (request.responseText.length > 1)
						{
							$('progressBar_' + id).style.width = request.responseText + '%';
							$('statusText_' + id).update(request.responseText + '% uploaded');
						}
					}
				}
			}
		);
	} catch(e) {
		alert('submitPostUsingAjax() failed, reason: ' + e);
	} finally {
		// empty
	}

	return false;
}

// executes function after page loaded
function addLoadEvent(func)
{
	var oldonload = window.onload;
	if (typeof window.onload != 'function')
	{
		window.onload = func;
	} else {
		window.onload = function() {
			oldonload();
			func();
		}
	}
}

// update uploaded file list manually
function updateUploadedFileList()
{

	// stop updater manually
	if (typeof updater != 'undefined')
	{
		updater.stop();
		updater = null;
	}

	// Hide Modal Dialog
	Richfaces.hideModalPanel('uploadDlg');

	// update uploaded attachments
	var attForm = $('attachmentsFormID').value;
	$(attForm + ':btnUpdateFileList').click();
	$('statusText_' + id).update('Upload finished!');
	$('progressBar_' + id).style.width = '100%';

	// remove all input tags except first
	$('attUploadForm').getInputs('file').each(
		function(i, index) {
			i.remove();
		}
	);

	// remove all <br> tags except first
	$$('br.formBR').each(
		function(i, index) {
			i.remove();
		}
	);

	// add one file input
	appendInput();

	// reset submit form
	$('attUploadForm').reset();
}

// observe IFrame object for "load" event to update uploaded file list
function observeFormSubmit()
{
	// add event to observe upload target
	Event.observe('uploadTargetID', 'load', updateUploadedFileList);
}

// add event handler only after page loaded
addLoadEvent(observeFormSubmit);

// appends new input to form
function appendInput()
{
	try
	{
		// test inputs values, if even one of them empty, do not add new input
		var testInputs = true;
		$('attUploadForm').getInputs('file').each(
			function(i) {
				var str = i.value;
				str = str.replace(' ','');
				str = str.replace('\t','');
				if (str.length < 1)
				{
					testInputs = false;
				}
			}
		);
		if (testInputs==false)
		{
			return;
		}

		// add new input to form
		new Insertion.Before(
			'submitID1',
			'<input type=\"file\" name="txtFile" id="txtFile" class=\"rsInput\" onchange=\"appendInput()\"/><br class=\"formBR\"/>'
		);
	} catch (e) {
		alert('appendInput() failed, reason: ' + e);
	}
}
//-->
