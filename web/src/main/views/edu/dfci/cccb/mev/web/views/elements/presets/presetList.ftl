<div>
<table class="table table-striped" style="font-size:14px;">
<thead>
<tr>
<th>Dataset</th>
<th>Disease Name</th>
<th>Platform Name</th>
</tr>
</thead>
<tbody>
<tr ng-repeat="preset in presets">
<td><a href="/annotations/import-dataset/command/core/view-preset-annotations?import-preset={{preset.name}}">{{preset.name}}</a></td>
<td>{{preset.diseaseName}}</td>
<td>{{preset.platformName}}</td>
</tr>
</tbody>
</table>
</div>