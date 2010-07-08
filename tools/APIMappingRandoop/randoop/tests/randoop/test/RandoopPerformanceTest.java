package randoop.test;

import java.io.InputStream;
import java.util.List;

import randoop.ForwardGenerator;
import randoop.Globals;
import randoop.StatementKind;
import randoop.main.GenInputsAbstract;
import randoop.util.Reflection;

public class RandoopPerformanceTest extends AbstractPerformanceTest {

  @Override
  void execute() {
    String resourcename = "resources/java.util.classlist.java1.6.txt";

    InputStream classStream =
      ForwardExplorerPerformanceTest.class.getResourceAsStream(resourcename);

    List<StatementKind> m =
      Reflection.getStatements(Reflection.loadClassesFromStream(classStream, resourcename),null);
    System.out.println("done creating model.");
    GenInputsAbstract.dontexecute = true; // FIXME make this an instance field?
    Globals.nochecks = true;
    ForwardGenerator explorer = new ForwardGenerator(m, null, Long.MAX_VALUE, 100000, null);
    explorer.explore();
  }

  @Override
  int expectedTimeMillis() {
    return 10000;
  }

}
