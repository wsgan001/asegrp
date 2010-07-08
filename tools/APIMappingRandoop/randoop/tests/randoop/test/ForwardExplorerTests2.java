package randoop.test;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import randoop.ForwardGenerator;
import randoop.Sequence;
import randoop.main.GenInputsAbstract;
import randoop.test.treeadd.TreeAdd;
import randoop.test.treeadd.TreeNode;
import randoop.util.Reflection;

public class ForwardExplorerTests2 extends TestCase {

  public void test5() throws Exception {

    // This test throws Randoop into an infinite loop. Disabling
    // TODO look into it.
    if (true) return;

    List<Class<?>> classes = new ArrayList<Class<?>>();
    classes.add(TreeNode.class);
    classes.add(TreeAdd.class);

    System.out.println(classes);

    //SimpleExplorer exp = new SimpleExplorer(classes, Long.MAX_VALUE, 100);
    ForwardGenerator exp =
      new ForwardGenerator(Reflection.getStatements(classes, null), null, Long.MAX_VALUE, 100, null);
    GenInputsAbstract.forbid_null = false;
    exp.explore();
    for (Sequence s : exp.allSequences()) {
      s.toCodeString();
    }
  }

}
