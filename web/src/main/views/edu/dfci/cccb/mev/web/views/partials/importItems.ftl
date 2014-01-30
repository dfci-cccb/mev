
<!-- Page Hacks -->
<br>
<br>
<br>
<!-- End Page Hacks -->




<div class="row-fluid">

  <div class="span3">
  
    <div class="row-fluid">
        <div class="span10 offset1">
            <div class="row-fluid">
                <p class="lead">Manage Datasets</p>
                <hr>
            </div>
            <div class="row-fluid">
                <div class='uploadDrag'></div>
            </div>
        </div>
    </div>
  
  </div>

  <div class="span6">
    <div class="row-fluid">
      <ul class="nav nav-tabs" id="importTabs">
        <li class="active"><a href="#current" data-toggle="tab" ng-click="loadUploads()">Files</a></li>
        <li><a href="#googleDrive" data-toggle="tab">Google Drive</a></li>
        <li><a href="#tcga" data-toggle="tab">TCGA</a></li>
      </ul>
 	  
      <div class="tab-content">
      
         <div class="tab-pane active" id="current">
         
            <div class="row-fluid">
            
              <div class="span12">
                <div uploads-Table uploads="userUploads"></div>
              </div>
              
            </div>
           
         </div>
        
        <div class="tab-pane" id="tcga">
		  <div preset-manager ng-controller="PresetManagerController"></div>
          <!-- <a href="" onclick="$.ajax({ type: 'POST',
                                       url: '/dataset?load=Ovarian Agilent G4502A 07 2 Level 3',
                                       contentType: 'application/json;charset=UTF-8',
                                       onSuccess: function() {
                                         //loadUploads();
                                         alert('Ovarian Agilent G4502A 07 2 Level 3 loaded');
                                       }
                                     });">Ovarian Agilent G4502A 07 2 Level 3</a>
           -->
        </div>
       
        
      </div>
      
    </div>
    
  </div>

</div>