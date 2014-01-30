package edu.dfci.cccb.mev.presets.simple;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lombok.experimental.Accessors;
import edu.dfci.cccb.mev.presets.contract.Preset;
import edu.dfci.cccb.mev.presets.contract.PresetsBuilder;
import edu.dfci.cccb.mev.presets.contract.exceptions.PresetException;
import edu.dfci.cccb.mev.presets.contract.exceptions.PresetNotFoundException;
import edu.dfci.cccb.mev.presets.prototype.AbstractPresets;

@Accessors(fluent=true)
public class SimplePresests extends AbstractPresets {

  private final List<Preset> list;
  
  public SimplePresests(){
    list=new ArrayList<Preset>();
  }  
  public SimplePresests(URL metadataURL, PresetsBuilder builder) throws PresetException{    
    list=builder.getAll (metadataURL);
  }
  
  @Override
  public Preset get (String name) throws PresetNotFoundException{    
    int index = find(name);
    if(index>=0)
      return list.get (index);
    else
      throw new PresetNotFoundException ("Preset.name("+name+") not found");
  }

  @Override 
  public void put (Preset preset) {
    int index = find(preset.name ());
    if(index>=0)
      list.remove (index);
    list.add (preset);
  }

  @Override
  public void remove (String name) throws PresetNotFoundException{
    for(Preset preset : list)
      if(preset.name ().equalsIgnoreCase (name)){
        list.remove (preset);
        return;
      }
    throw new PresetNotFoundException ("Preset.id("+name+") not found");        
  }
  
  private int find(String name){    
    for(int i=0; i<list.size ();i++)      
      if(list.get (i).name ().equalsIgnoreCase (name))
        return i;          
    return -1;
  }
  @Override
  public List<Preset> getAll () {
    // TODO Auto-generated method stub
    return Collections.unmodifiableList (list);
  }
  @Override
  public List<String> list () {
    List<String> names = new ArrayList<String> ();
    for(Preset preset : list)
      names.add (preset.name ());
    return names;
  }
}
