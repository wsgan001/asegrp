package randoop;

/**
 * The contract: <code>o1.equals(o2) => o2.equals(o1)</code>.
 */
public final class EqualsSymmetric implements ObjectContract {

  private static final long serialVersionUID = -978952647793752743L;

  @Override
  public boolean equals(Object o) {
    if (o == null)
      return false;
    if (o == this)
      return true;
    if (!(o instanceof EqualsSymmetric)) {
      return false;
    }
    return true; // no state to compare.
  }

  @Override
  public int hashCode() {
    int h = 19;
    return h;  // no state to compare.
  }
  
  public boolean evaluate(Object... objects) {

    Object o1 = objects[0];
    Object o2 = objects[1];

    if (o1.equals(o2)) {
      return o2.equals(o1);
    }
    return true;
  }

  public int getArity() {
    return 2;
  }

  public String toCommentString() {
    return "equals-symmetric on x0 and x1.";
  }

  @Override
  public boolean evalExceptionMeansFailure() {
    return true;
  }

  @Override
  public String toCodeString() {
    StringBuilder b = new StringBuilder();
    b.append(Globals.lineSep);
    b.append("// Checks the contract: ");
    b.append(" " + toCommentString() + Globals.lineSep);
    b.append("assertTrue(");
    b.append("\"Contract failed: " + toCommentString() + "\", ");
    b.append("x0.equals(x1) ? x1.equals(x0) : true");
    b.append(");");
    return b.toString();
  }

}
