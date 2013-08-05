<div class="row">

  <div class="span3">
	  <div class="row">
	    <div class="panel panel-primary">
		  <div class="panel-heading"><h3 class="panel-title">Options</h3></div>
          <button class="btn">Filter</button>
          <button class="btn">Clear All</button>
          <button class="btn">Select all</button>
          <button class="btn">Save</button>
        </div>
	  </div>
      <div ng-repeat="field in fields">
		<div class="row">
		  <div class="filter-Selector"
             inputfield="field" 
             pushToParamsIn="pushToParams()" 
             pullFromParamsIn="pullFromParams(key)"
             clearFunction="">
          </div>
		</div>
      </div>
  </div>

  <div class="span8">
    <!-- Data Table-->
    <table class='table'>
      <thead>
        <tr>
          <th ng-repeat="header in fields">{{header.name}}</th>
        </tr>
      </thead>
      <tbody>
        <tr ng-repeat="row in tuples">
            <td ng-repeat="cell in fields">
              {{row[cell.reference]}}
            </td>
        </tr>
      </tbody>
    </table>
  </div>

</div>
