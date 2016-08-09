/**
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.dfci.cccb.mev.dataset.rest.controllers;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.refine.ProjectMetadata;
import com.google.refine.io.FileProjectManager;
import com.google.refine.model.Project;
import edu.dfci.cccb.mev.dataset.domain.contract.*;
import edu.dfci.cccb.mev.dataset.domain.fs.DatasetBuilderFlatFile;
import edu.dfci.cccb.mev.dataset.domain.fs.IFlatFileValues;
import edu.dfci.cccb.mev.dataset.domain.zip.DatasetBuilderZip;
import edu.dfci.cccb.mev.dataset.domain.zip.OpenRefineAnnotation;
import edu.dfci.cccb.mev.dataset.rest.assembly.binary.MultipartBinaryInput;
import edu.dfci.cccb.mev.io.implementation.TemporaryFile;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j;

import org.apache.commons.io.IOUtils;
import org.apache.tools.tar.TarOutputStream;
import org.springframework.aop.scope.ScopedObject;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import edu.dfci.cccb.mev.dataset.rest.assembly.tsv.MultipartTsvInput;
import org.springframework.web.multipart.MultipartHttpServletRequest;

/**
 * @author levk
 * 
 */
@RestController
@Log4j
@ToString (exclude = { "workspace", "builder" })
public class WorkspaceController {

  private @Getter @Setter @Inject Workspace workspace;
  private @Getter @Setter @Inject DatasetBuilder builder;
  private @Inject @Named("RestJsonObjectMapper") ObjectMapper mapper;
  private @Inject FileProjectManager projectManager;
  @RequestMapping (value = "/dataset", method = GET)
  public List<String> list () {
    return workspace.list ();
  }

  @RequestMapping (value = "/dataset", method = POST, consumes = "multipart/form-data")
  @ResponseStatus (OK)
  public void upload (@RequestParam("name") String name, @RequestParam ("upload") MultipartFile upload) throws DatasetBuilderException,
                                                                    InvalidDatasetNameException,
                                                                    InvalidDimensionTypeException {
    Dataset dataset = builder.build (new MultipartTsvInput (upload));
    if (log.isDebugEnabled ())
      log.debug ("Uploaded " + dataset);
    workspace.put (dataset);
  }

    private void importAnnotations(File file) throws IOException {
        try(FileInputStream fin = new FileInputStream(file)){
            long projectId = Project.generateID();
            projectManager.save(true);
            projectManager.importProject(projectId, fin, true);
            projectManager.loadProjectMetadata(projectId);
            ProjectMetadata pm = projectManager.getProjectMetadata(projectId);
            pm.updateModified();
            projectManager.save(true);
        }
    }

  @RequestMapping (value = "/import/binary", method = POST, consumes = "multipart/form-data")
  @ResponseStatus (OK)
  public void upload (
          @RequestParam("name") String name,
          @RequestParam("rows") List<String> rows,
          @RequestParam("columns") List<String> columns,
          @RequestParam("rowSelections") String jsonRowSelections,
          @RequestParam("columnSelections") String jsonColumnSelections,
          MultipartHttpServletRequest req) throws DatasetException,
          IOException {
    log.debug (String.format("Offline %s, %s, %s", name, rows, columns));
    MultipartFile upload = req.getFile("upload");
    RawInput input = new MultipartBinaryInput(upload);
    Selection[] rowSelections = mapper.readValue(jsonRowSelections, Selection[].class);
    Selection[] columnSelections = mapper.readValue(jsonColumnSelections, Selection[].class);

    DatasetBuilderFlatFile builder = new DatasetBuilderFlatFile();
    Dataset dataset = builder.build(input, name, columns, rows,
            Arrays.asList(columnSelections),
            Arrays.asList(rowSelections));

    workspace.put(dataset);
  }

    @RequestMapping (value = "/import/zip", method = POST, consumes = "multipart/form-data")
    @ResponseStatus (OK)
    public void uploadZip (
            @RequestParam("name") String name,
            @RequestParam ("upload") MultipartFile upload) throws IOException, DatasetException {

        RawInput input = new MultipartBinaryInput(upload);
//        File zipfile = new TemporaryFile();
//        OutputStream zipos = new FileOutputStream(zipfile);
//        IOUtils.copy(input.input(), zipos);
//        zipos.close();
        DatasetBuilderZip builder = new DatasetBuilderZip().setMapper(mapper);
        Dataset dataset = builder.build(input);

        Annotation columnAnnotation = dataset.dimension(Dimension.Type.COLUMN).annotation();
        if(columnAnnotation instanceof OpenRefineAnnotation)
            importAnnotations(
                    ((OpenRefineAnnotation)columnAnnotation).file()
            );

        Annotation rowAnnotation = dataset.dimension(Dimension.Type.ROW).annotation();
        if(rowAnnotation instanceof OpenRefineAnnotation)
            importAnnotations(
                    ((OpenRefineAnnotation)rowAnnotation).file()
            );

        if(workspace.list().contains(dataset.name()))
            workspace.remove(dataset.name());
        workspace.put(dataset);
    }

    private void zipAnnotations(String name, String dimension, ZipOutputStream zout) throws DatasetNotFoundException, IOException {
        Dataset dataset = workspace.get(name);
        long projectId = projectManager.getProjectID (dataset.name () + dimension);
        if(projectId > -1){
            File annotations = new TemporaryFile();
            GZIPOutputStream gos = new GZIPOutputStream(new FileOutputStream(annotations));
            TarOutputStream tos = new TarOutputStream(gos);
            projectManager.exportProject(projectId, tos);
            tos.flush();
            tos.close();
            zout.putNextEntry(new ZipEntry(String.format("annotations_%s.tar.gz", dimension)));
            IOUtils.copy(new FileInputStream(annotations), zout);
            zout.closeEntry();
        }
    }


  @RequestMapping (value = "/export/zip", method = POST, consumes = "multipart/form-data")
  @ResponseStatus (OK)
  public byte[] export (
          @RequestParam("name") String name,
          @RequestParam("rows") List<String> rows,
          @RequestParam("columns") List<String> columns,
          @RequestParam("rowSelections") String jsonRowSelections,
          @RequestParam("columnSelections") String jsonColumnSelections,
          @RequestParam("analyses") String[] analyses,
          HttpServletResponse response) throws DatasetException,
          IOException {
          log.info (String.format("Offline %s, %s, %s, %s", name, rows, columns, analyses));

          //creating byteArray stream, make it bufforable and passing this buffor to ZipOutputStream
          ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(byteArrayOutputStream);
          ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream);

          //nw zip entry for dataset
          Dataset dataset = workspace.get(name);
          zipOutputStream.putNextEntry(new ZipEntry("dataset.json"));
          IOUtils.copy(new ByteArrayInputStream(mapper.writeValueAsBytes(dataset)), zipOutputStream);
          zipOutputStream.closeEntry();

          zipAnnotations(name, "row", zipOutputStream);
          zipAnnotations(name, "column", zipOutputStream);

          if(dataset.values() instanceof IFlatFileValues){
              IFlatFileValues values = (IFlatFileValues)dataset.values();
              zipOutputStream.putNextEntry(new ZipEntry("values.bin"));
              InputStream valuesIs = values.asInputStream();
              IOUtils.copy(valuesIs, zipOutputStream);
              zipOutputStream.closeEntry();
              zipOutputStream.flush();
              valuesIs.close();
          }

          //new zip entry and copying inputstream with file to zipOutputStream, after all closing streams
          int i=0;
          for(String analysis : analyses){
            InputStream analysisOs = new ByteArrayInputStream(analysis.getBytes(StandardCharsets.UTF_8));
            zipOutputStream.putNextEntry(new ZipEntry(String.format("analysis_%d.json", i)));
            JsonNode analysisJson = mapper.readTree(analysis);
            IOUtils.copy(analysisOs, zipOutputStream);
            zipOutputStream.closeEntry();
            zipOutputStream.flush();
            analysisOs.close();
            i++;
          }     
          zipOutputStream.flush();
          zipOutputStream.close();

          byte[] ret = byteArrayOutputStream.toByteArray();
          IOUtils.closeQuietly(bufferedOutputStream);
          IOUtils.closeQuietly(byteArrayOutputStream);

          response.setContentLength(ret.length);
          response.setContentType("application/zip"); //or something more generic...
          response.setHeader("Accept-Ranges", "bytes");
          response.setStatus(HttpServletResponse.SC_OK);
          response.addHeader("Content-Disposition", String.format("attachment; filename=\"%s.zip\"", name));
          return ret;
  }

  @RequestMapping (method = RequestMethod.POST, value = "/import/google/{id}/load")
  public void load (@PathVariable ("id") String id) throws DatasetBuilderException,
                                                   InvalidDatasetNameException,
                                                   InvalidDimensionTypeException,
                                                   IOException {
    log.debug ("Loading id "
               + id + " into workspace " + workspace.getClass () + " implementing "
               + Arrays.asList (workspace.getClass ().getInterfaces ()));
    Workspace workspace =
                          this.workspace instanceof ScopedObject
                                                                ? (Workspace) ((ScopedObject) this.workspace).getTargetObject ()
                                                                : this.workspace;
  }

  @RequestMapping(method = GET, value="/session/close")
  @ResponseStatus(OK)
  public void closeSession(){
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    HttpSession session = attributes.getRequest().getSession(true);
    session.invalidate();
  }
}
