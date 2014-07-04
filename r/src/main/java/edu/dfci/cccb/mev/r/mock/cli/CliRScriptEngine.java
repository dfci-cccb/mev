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
package edu.dfci.cccb.mev.r.mock.cli;

import static java.io.File.createTempFile;
import static java.lang.Integer.parseInt;
import static java.lang.Runtime.getRuntime;
import static java.lang.System.currentTimeMillis;
import static java.lang.System.getProperty;
import static java.lang.Thread.currentThread;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.valueOf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.util.concurrent.TimeUnit;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptException;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

/**
 * @author levk
 * 
 */
@Log4j
public class CliRScriptEngine extends AbstractScriptEngine {

  private @Getter @Setter String rScriptExecutable = getProperty (CliRScriptEngine.class.getName ()
                                                                  + ".rScriptExecutable", "Rscript");
  private @Getter @Setter String rScriptLaunchingOptions = getProperty (CliRScriptEngine.class.getName ()
                                                                        + ".rScriptLaunchingOptions", "");
  private @Getter @Setter int rMaximumRuntime = parseInt (getProperty (CliRScriptEngine.class.getName ()
                                                                       + ".rMaximumRuntime", "30"));
  private @Getter @Setter TimeUnit rMaximumRuntimeUnit = valueOf (getProperty (CliRScriptEngine.class.getName ()
                                                                               + ".rMaximumRuntimeUnit", "SECONDS"));

  /* (non-Javadoc)
   * @see javax.script.ScriptEngine#eval(java.lang.String,
   * javax.script.ScriptContext) */
  @Override
  public Object eval (String script, ScriptContext context) throws ScriptException {
    return eval (new StringReader (script), context);
  }

  /* (non-Javadoc)
   * @see javax.script.ScriptEngine#eval(java.io.Reader,
   * javax.script.ScriptContext) */
  @Override
  public Object eval (Reader reader, ScriptContext context) throws ScriptException {
    try {
      File script = createTempFile ("mev-r-", ".R");
      try {
        script.createNewFile ();
        try (Writer writer = new BufferedWriter (new FileWriter (script))) {
          for (int c; (c = reader.read ()) >= 0; writer.write (c));
          writer.flush ();
          final String command = rScriptExecutable + " "
                                 + rScriptLaunchingOptions + script.getAbsolutePath ();
          log.debug ("Launching R script " + script + " using " + command);
          final long before = currentTimeMillis ();
          final Process r = getRuntime ().exec (command);
          new Thread () {
            private Thread original;

            public void run () {
              try {
                Thread.sleep (rMaximumRuntimeUnit.toMillis (rMaximumRuntime));
                r.exitValue ();
                return;
              } catch (InterruptedException | IllegalThreadStateException e) {
                log.warn ("Forcibly destroying LIMMA process lauched with command "
                          + command + " after waiting for "
                          + rMaximumRuntimeUnit.convert (currentTimeMillis () - before, MILLISECONDS) + " "
                          + rMaximumRuntimeUnit.toString ().toLowerCase ());
                original.interrupt ();
                r.destroy ();
              }
            }

            private Thread initialize (Thread original) {
              this.original = original;
              return this;
            }
          }.initialize (currentThread ()).start ();
          int result = r.waitFor ();
          if (log.isDebugEnabled ())
            try (ByteArrayOutputStream buffer = new ByteArrayOutputStream ();
                 Writer debug = new BufferedWriter (new OutputStreamWriter (buffer));
                 Reader output = new BufferedReader (new InputStreamReader (r.getInputStream ()));
                 Reader error = new BufferedReader (new InputStreamReader (r.getErrorStream ()));
                 Reader code = new BufferedReader (new InputStreamReader (new FileInputStream (script)))) {
              debug.write ("R process " + script + " exited with code " + result);
              if (result != 0) {
                debug.write ("\nOriginal script:\n");
                for (int c; (c = code.read ()) >= 0; debug.write (c));
                debug.write ("\nStandard output:\n");
                for (int c; (c = output.read ()) >= 0; debug.write (c));
              }
              debug.write ("\nStandard error:\n");
              for (int c; (c = error.read ()) >= 0; debug.write (c));
              debug.flush ();
              log.debug (buffer.toString ());
            } catch (Exception e) {
              log.warn ("Unable to dump debug output for R process " + script, e);
            }
          if (result == 0)
            return result;
          else
            throw new RuntimeException ("Exited with abnormal return code " + result);
        }
      } finally {
        script.delete ();
      }
    } catch (Exception e) {
      throw new ScriptException (e);
    }
  }

  /* (non-Javadoc)
   * @see javax.script.ScriptEngine#createBindings() */
  @Override
  public Bindings createBindings () {
    throw new UnsupportedOperationException ("nyi");
  }

  /* (non-Javadoc)
   * @see javax.script.ScriptEngine#getFactory() */
  @Override
  public ScriptEngineFactory getFactory () {
    return CliRScriptEngineFactory.getInstance ();
  }
}
