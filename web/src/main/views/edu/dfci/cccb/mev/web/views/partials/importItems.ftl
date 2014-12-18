
<mev-main-menu></mev-main-menu>

<a class="brand" href="/"></a>
     		
<div class="container-fluid imports-container">

	<div class="row">
	
	  <div class="col-md-3">	
	  
	    <div class="row">
	        <div class="col-md-10 col-md-offset-1"  id="userUploadsPanel" >
	        	
		            <div class="row" >
		                <div class='uploadDrag'></div>
		            </div>
		            <div class="row">
		            	<div uploads-Table uploads="userUploads"></div>
		            </div>
	            
	        </div>
	    </div>
	  
	  </div>
	
	  <div class="col-md-8">
	  	
	    <div class="row">
	      <ul class="nav nav-tabs" id="importTabs">
<!-- 	        <li class="active"><a href="#current" data-toggle="tab" ng-click="loadUploads()" target="_self">Files</a></li> -->
	        <li id="tcga_tab"><a href="#tcga" data-toggle="tab" target="_self" >Import TCGA Datasets</a></li>
	        <li id="google_tab"><a href="#google" data-toggle="tab" target="_self" >Google Drive</a></li>
	        <li id="geods_tab"><a href="#geods" data-toggle="tab" target="_self" >Import GEO Datasets</a></li>
	      </ul>
	 	  
	      <div class="tab-content" id="import">
	         
	        <div class="tab-pane" id="tcga">
			  <div id="presetMgr" preset-manager ng-controller="PresetManagerController">
			  </div>
	        </div>
	        
	        <div class="tab-pane" id="geods">
			  <div id="geodsImportMgr" mev-geods-import-directive ng-controller="MevGeodsImportCtrl">
			  </div>
	        </div>
	        <div class="tab-pane" id="google">
	        
				 <div ng-hide="googleDrive.signedIn">
					<div class="row-fluid">
						<form action="signin/google" method="POST">
						    <button id="ds-drive-signin" type="submit" class="btn btn-primary btn-block">Sign in with Google</button>
						    <input type="hidden" name="scope" value="https://www.googleapis.com/auth/plus.login https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo#email https://www.googleapis.com/auth/plus.me https://www.googleapis.com/auth/tasks https://www.googleapis.com/auth/drive https://www.googleapis.com/auth/latitude.all.best" />
						    <input type="hidden" name="request_visible_actions" value="http://schemas.google.com/AddActivity http://schemas.google.com/BuyActivity http://schemas.google.com/CheckInActivity http://schemas.google.com/CommentActivity http://schemas.google.com/CreateActivity http://schemas.google.com/DiscoverActivity http://schemas.google.com/ListenActivity http://schemas.google.com/ReserveActivity http://schemas.google.com/ReviewActivity http://schemas.google.com/WantActivity"/>
						    <input type="hidden" name="access_type" value="offline"/>
						</form>
					</div>
				</div>
				
				<div class="geo_import_container" ng-show="googleDrive.signedIn">
					<table class="mev-table-tight table table-striped" style="font-size:14px;">
						<thead>
							<tr>
								<th>File Name</th>
								<th>Import</th>
							</tr>
						</thead>
						<tbody>
							<tr ng-repeat="file in googleDrive.files">
								<td>{{file.name}}</td>
								<td>
									<a class="btn btn-small btn-success" href="" ng-click="postDriveFile(file.id)">Import</a>
								</td>
							</tr>
						</tbody>
					</table>
					
				</div>
				
	        </div>
	        
	      </div>
	      
	    </div>
	    
	  </div>
	
	</div>
</div>