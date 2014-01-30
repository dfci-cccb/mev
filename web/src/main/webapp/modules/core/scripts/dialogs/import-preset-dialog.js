function ImportPresetDialog() { 	
    this._createDialog();
    
}

ImportPresetDialog.prototype._lastItem=null;


ImportPresetDialog.prototype._createDialog = function() {
    var self = this;
    var dialog = $(DOM.loadHTML("core", "scripts/dialogs/import-preset-dialog.html"));
    this._elmts = DOM.bind(dialog);
    this._name="";
    
    this._dimension="";
    if(theProject.metadata.customMetadata){
    	if(theProject.metadata.customMetadata.selectionName)
    		this._name=theProject.metadata.customMetadata.selectionName;
    	if(theProject.metadata.customMetadata.dimension)
    		this._dimension=theProject.metadata.customMetadata.dimension;
    	
    }
    this._elmts.setName[0].value=this._name;
    
    //this._elmts.controls.find("textarea").bind("keyup change input",function() { self._scheduleUpdate(); });
    
    //this._elmts.resetButton.html($.i18n._('core-buttons')["reset-template"]);
    this._elmts.exportSetButton.html("Save");
    this._elmts.exportSetAndExitButton.html("Save and Close");
    this._elmts.cancelSetButton.html($.i18n._('core-buttons')["cancel"]);
    
    this._elmts.exportSetButton.click(function() { if(self._validate()){self._exportAjax(); self._dismiss(); }});
    this._elmts.exportSetAndExitButton.click(function() { if(self._validate()){self._exportAjax(); self._dismiss(); Refine._close(); }});
    this._elmts.cancelSetButton.click(function() { self._dismiss(); });
    /*
    this._elmts.resetButton.click(function() {
        self._fillInTemplate(self._createDefaultTemplate());
        self._updatePreview();
    });
    */
    this._level = DialogSystem.showDialog(dialog);
};

ImportPresetDialog.prototype._dismiss = function() {
    DialogSystem.dismissUntil(this._level - 1);
};

ImportPresetDialog.prototype._validate = function()
{
	  //var name = window.prompt("Esport set name", "open-refine-exported-set");
	  var name = this._elmts.setName[0].value.trim();	  
	  if (!name) {
		this._elmts.errorMessage.html("Name is required");
		this._elmts.setName[0].focus();
	    return false;
	  }

	  this._name=name; 	  

	  return true;
};

ImportPresetDialog.prototype._exportAjax = function(){
	var postRequest = {
		    type: "POST",
		    url: "command/core/import-preset-dataset",
		    data: { 
		    	"project" : theProject.id, 
		    	//"import-preset" : theProject.metadata.name,
		    	"import-preset" : $.url().param('import-preset'),
		    	"newDatasetName" : this._name,		    	
		    	"engine" : JSON.stringify(ui.browsingEngine.getJSON())
		    	},
		    dataType: "json",
		    success: function (data) {
		      if (data && typeof data.code != 'undefined' && data.code == "ok") {
		        //alert("Set saved succesfully");		        
		        //parent.OpenRefineBridge.addSelectionSet(Refine._lastItem);
		        var currentUrl = window.location.href;
		        console.log("currentUrl:"+currentUrl);
		        var newUrl = "/#/dataset/"+Refine._lastItem.name+"/";
		        console.log("newUrl:"+newUrl);
		        window.location.replace(newUrl);
		      } else {
		        alert($.i18n._('core-index')["error-rename"]+" " + data.message);
		      }
		    }
		  };
		 
		Refine._lastItem={
			name: this._name,
			dimension: this._dimension,
		};		
		$.ajax(postRequest);
};

ImportPresetDialog.prototype._export = function() {  

	  var form = document.createElement("form");	  
	  $(form)
	  .css("display", "block")
	  .attr("method", "post")
	  .attr("action", "command/core/export-set/");	  	  
	  
	  $('<input />')
	  .attr("name", "project")
	  .attr("value", theProject.id)
	  .appendTo(form);
	  
	  $('<input />')
	  .attr("name", "selectionName")
	  .attr("value", this._name)
	  .appendTo(form);
	  	  
	  $('<input />')
	  .attr("name", "selectioncolor")
	  .attr("value", this._color)
	  .appendTo(form);
	  
	  $('<input />')
	  .attr("name", "engine")
	  .attr("value", JSON.stringify(ui.browsingEngine.getJSON()))
	  .appendTo(form);
	  
	  
	  document.body.appendChild(form);
	  form.submit();
	  document.body.removeChild(form);
	  
}