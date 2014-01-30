package edu.dfci.cccb.mev.presets.dal;

import java.net.URL;
import java.util.List;

public interface TsvReader {

  public abstract TsvReader init (URL data);

  public abstract int getColumnCount ();

  public abstract String[] getColumnNames ();

  public abstract List<Object[]> readAll ();

}