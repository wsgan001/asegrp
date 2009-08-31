package edu.ncsu.csc.ase.itutor.core.asm.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.ITextSelection;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;

import edu.ncsu.csc.ase.itutor.core.asm.BytecodeUtil;

/**
 * @author Eric Bruneton
 */

public class DecompiledClass {

    public static final String ATTR_CLAS_SIZE = "class.size";
    public static final String ATTR_JAVA_VERSION = "java.version";
    public static final String ATTR_ACCESS_FLAGS = "access";

    /** key is DecompiledMethod, value is IJavaElement (Member) */
    private Map methodToJavaElt;
    private List text;
    /**
     * key is string, value is string
     */
    private Map classAttributesMap = new HashMap();
    private String value;
    private ClassNode classNode;
    
    private String[] fInterfaces;

    public DecompiledClass(final List text) {
        this.text = text;
        methodToJavaElt = new HashMap();
    }
    
    public void setAttribute(String key, String value) {
        classAttributesMap.put(key, value);
    }

    /**
     * @return the class's access flags (see {@link Opcodes}). This parameter also
     * indicates if the class is deprecated.
     */
    public int getAccessFlags() {
        int result = 0;
        String flags = (String) classAttributesMap.get(ATTR_ACCESS_FLAGS);
        if (flags == null) {
            return result;
        }
        try {
            Integer intFlags = Integer.valueOf(flags);
            result = intFlags.intValue();
        } catch (NumberFormatException e) {
            // ignore, should not happen
        }
        return result;
    }

    /**
     * @return true if the class is either abstract or interface
     */
    public boolean isAbstractOrInterface() {
        int accessFlags = getAccessFlags();
        return ((accessFlags & Opcodes.ACC_ABSTRACT) != 0)
            || ((accessFlags & Opcodes.ACC_INTERFACE) != 0);
    }

    public String getAttribute(String key) {
        return (String) classAttributesMap.get(key);
    }

    public String getText() {
        if (value == null) {
            StringBuffer buf = new StringBuffer();
            for (int i = 0; i < text.size(); ++i) {
                Object o = text.get(i);
                if (o instanceof DecompiledMethod) {
                    buf.append(((DecompiledMethod) o).getText());
                } else {
                    buf.append(o);
                }
            }
            value = buf.toString();
        }
        return value;
    }

    public String[][] getTextTable() {
        List lines = new ArrayList();
        for (int i = 0; i < text.size(); ++i) {
            Object o = text.get(i);
            if (o instanceof DecompiledMethod) {
                String[][] mlines = ((DecompiledMethod) o).getTextTable();
                for (int j = 0; j < mlines.length; ++j) {
                    lines.add(mlines[j]);
                }
            } else {
                lines.add(new String[]{"", "", o.toString(), ""});
            }
        }
        return (String[][]) lines.toArray(new String[lines.size()][]);
    }

    public int getBytecodeOffset(final int decompiledLine) {
        int currentDecompiledLine = 0;
        for (int i = 0; i < text.size(); ++i) {
            Object o = text.get(i);
            if (o instanceof DecompiledMethod) {
                DecompiledMethod m = (DecompiledMethod) o;
                Integer offset = m.getBytecodeOffset(decompiledLine
                    - currentDecompiledLine);
                if (offset != null) {
                    return offset.intValue();
                }
                currentDecompiledLine += m.getLineCount();
            } else {
                currentDecompiledLine++;
            }
        }
        return -1;
    }

    public int getBytecodeInsn(final int decompiledLine) {
        int currentDecompiledLine = 0;
        for (int i = 0; i < text.size(); ++i) {
            Object o = text.get(i);
            if (o instanceof DecompiledMethod) {
                DecompiledMethod m = (DecompiledMethod) o;
                Integer opcode = m.getBytecodeInsn(decompiledLine
                    - currentDecompiledLine);
                if (opcode != null) {
                    return opcode.intValue();
                }
                currentDecompiledLine += m.getLineCount();
            } else {
                currentDecompiledLine++;
            }
        }
        return -1;
    }

    public int getSourceLine(final int decompiledLine) {
        int currentDecompiledLine = 0;
        for (int i = 0; i < text.size(); ++i) {
            Object o = text.get(i);
            if (o instanceof DecompiledMethod) {
                DecompiledMethod m = (DecompiledMethod) o;
                int l = m.getSourceLine(decompiledLine - currentDecompiledLine);
                if (l != -1) {
                    return l;
                }
                currentDecompiledLine += m.getLineCount();
            } else {
                currentDecompiledLine++;
            }
        }
        return -1;
    }

    public DecompiledMethod getMethod(final int decompiledLine) {
        int currentDecompiledLine = 0;
        for (int i = 0; i < text.size(); ++i) {
            Object o = text.get(i);
            if (o instanceof DecompiledMethod) {
                DecompiledMethod m = (DecompiledMethod) o;
                int l = m.getSourceLine(decompiledLine - currentDecompiledLine);
                if (l != -1) {
                    return m;
                }
                currentDecompiledLine += m.getLineCount();
            } else {
                currentDecompiledLine++;
            }
        }
        return null;
    }

    public DecompiledMethod getMethod(final String signature) {
        for (int i = 0; i < text.size(); ++i) {
            Object o = text.get(i);
            if (o instanceof DecompiledMethod) {
                DecompiledMethod m = (DecompiledMethod) o;
                if (signature.equals(m.getSignature())) {
                    return m;
                }
            }
        }
        return null;
    }

    public IJavaElement getJavaElement(int decompiledLine, IClassFile clazz) {
        DecompiledMethod method = getMethod(decompiledLine);
        if (method != null) {
            IJavaElement javaElement = (IJavaElement) methodToJavaElt
                .get(method);
            if (javaElement == null) {
                javaElement = BytecodeUtil.getMethod(clazz, method.getSignature());
                if (javaElement != null) {
                    methodToJavaElt.put(method, javaElement);
                } else {
                    javaElement = clazz;
                }
            }
            return javaElement;
        }
        return clazz;
    }

    public int getDecompiledLine(String methSignature) {
        int currentDecompiledLine = 0;
        for (int i = 0; i < text.size(); ++i) {
            Object o = text.get(i);
            if (o instanceof DecompiledMethod) {
                DecompiledMethod m = (DecompiledMethod) o;
                if (methSignature.equals(m.getSignature())) {
                    return currentDecompiledLine;
                }
                currentDecompiledLine += m.getLineCount();
            } else {
                currentDecompiledLine++;
            }
        }
        return 0;
    }

    /**
     * @param decompiledLine
     * @return array with two elements, first is the local variables table, second is the
     * operands stack content. "null" value could be returned too.
     */
    public String[] getFrame(final int decompiledLine,
        final boolean showQualifiedNames) {
        int currentDecompiledLine = 0;
        for (int i = 0; i < text.size(); ++i) {
            Object o = text.get(i);
            if (o instanceof DecompiledMethod) {
                DecompiledMethod m = (DecompiledMethod) o;
                String[] frame = m.getFrame(decompiledLine
                    - currentDecompiledLine, showQualifiedNames);
                if (frame != null) {
                    return frame;
                }
                currentDecompiledLine += m.getLineCount();
            } else {
                currentDecompiledLine++;
            }
        }
        return null;
    }

    public String[][][] getFrameTables(final int decompiledLine,
        boolean useQualifiedNames) {
        int currentDecompiledLine = 0;
        for (int i = 0; i < text.size(); ++i) {
            Object o = text.get(i);
            if (o instanceof DecompiledMethod) {
                DecompiledMethod m = (DecompiledMethod) o;
                String[][][] frame = m.getFrameTables(decompiledLine
                    - currentDecompiledLine, useQualifiedNames);
                if (frame != null) {
                    return frame;
                }
                currentDecompiledLine += m.getLineCount();
            } else {
                currentDecompiledLine++;
            }
        }
        return null;
    }

    public int getDecompiledLine(final int sourceLine) {
        int currentDecompiledLine = 0;
        for (int i = 0; i < text.size(); ++i) {
            Object o = text.get(i);
            if (o instanceof DecompiledMethod) {
                DecompiledMethod m = (DecompiledMethod) o;
                int l = m.getDecompiledLine(sourceLine);
                if (l != -1) {
                    return l + currentDecompiledLine;
                }
                currentDecompiledLine += m.getLineCount();
            } else {
                currentDecompiledLine++;
            }
        }
        return -1;
    }

    /**
     * Converts method relative decompiled line to class absolute decompiled position
     * @param m1 method for which we need absolute line position
     * @param decompiledLine decompiled line, relative to given method (non global coord)
     * @return
     */
    public int getDecompiledLine(final DecompiledMethod m1, final int decompiledLine) {
        int currentDecompiledLine = 0;
        for (int i = 0; i < text.size(); ++i) {
            Object o = text.get(i);
            if (o instanceof DecompiledMethod) {
                if (o == m1){
                    return currentDecompiledLine + decompiledLine;
                }
                DecompiledMethod m = (DecompiledMethod) o;
                currentDecompiledLine += m.getLineCount();
            } else {
                currentDecompiledLine++;
            }
        }
        return -1;
    }

    public List getErrorLines() {
        List errors = new ArrayList();
        int currentDecompiledLine = 0;
        for (int i = 0; i < text.size(); ++i) {
            Object o = text.get(i);
            if (o instanceof DecompiledMethod) {
                DecompiledMethod m = (DecompiledMethod) o;
                int l = m.getErrorLine();
                if (l != -1) {
                    errors.add(new Integer(l + currentDecompiledLine));
                }
                currentDecompiledLine += m.getLineCount();
            } else {
                currentDecompiledLine++;
            }
        }
        return errors;
    }

    public DecompiledMethod getBestDecompiledMatch(int sourceLine) {
        DecompiledMethod bestM = null;

        for (int i = 0; i < text.size(); ++i) {
            Object o = text.get(i);
            if (o instanceof DecompiledMethod) {
                DecompiledMethod m = (DecompiledMethod) o;
                int line = m.getBestDecompiledLine(sourceLine);
                if (line > 0) {
                    // doesn't work if it is a <init> or <cinit> which spawns over
                    // multiple locations in code
                    if(m.isInit()){
                        if(bestM != null){
                            int d1 = sourceLine - bestM.getFirstSourceLine();
                            int d2 = sourceLine - m.getFirstSourceLine();
                            if(d2 < d1){
                                bestM = m;
                            }
                        } else {
                            bestM = m;
                        }
                    } else {
                        return m;
                    }
                } else {
                    // check for init blocks which composed from different code lines
                    if(bestM != null && bestM.isInit()){
                        if(bestM.getFirstSourceLine() < m.getFirstSourceLine()
                            && bestM.getLastSourceLine() > m.getLastSourceLine()){
                            bestM = null;
                        }
                    }
                }
            }
        }
        return bestM;
    }

    public LineRange getDecompiledRange(ITextSelection sourceRange) {
        int startLine = sourceRange.getStartLine() + 1;
        int endLine = sourceRange.getEndLine() + 1;
        int startDecompiledLine = getDecompiledLine(startLine);
        DecompiledMethod m1 = null;
        DecompiledMethod m2 = null;
        if (startDecompiledLine < 0) {
            m1 = getBestDecompiledMatch(startLine);
            m2 = getBestDecompiledMatch(endLine);
            if (m1 != null && m1.equals(m2)) {
                int methodStartLine = getDecompiledLine(m1.getSignature());
                startDecompiledLine = m1.getBestDecompiledLine(startLine);
                if (startDecompiledLine >= 0) {
                    startDecompiledLine = methodStartLine + startDecompiledLine;
                } else {
                    startDecompiledLine = methodStartLine + m1.getLineCount();
                }
            }
        }
        int endDecompiledLine = getDecompiledLine(endLine);
        if (endDecompiledLine < 0) {
            if(m2 == null) {
                m2 = getBestDecompiledMatch(endLine);
            }
            if (m2 != null && m2.equals(m1)) {
                int methodStartLine = getDecompiledLine(m2.getSignature());
                endDecompiledLine = m2.getBestDecompiledLine(endLine);
                if (endDecompiledLine >= 0) {
                    endDecompiledLine = methodStartLine + endDecompiledLine;
                } else {
                    endDecompiledLine = methodStartLine + m2.getLineCount();
                }
                // TODO dirty workaround
                if(endDecompiledLine < startDecompiledLine){
                    endDecompiledLine = startDecompiledLine + 1;
                }
            }
        }
        return new LineRange(startDecompiledLine, endDecompiledLine);
    }

    public LineRange getSourceRange(LineRange decompiledRange) {
        int startSourceLine = getSourceLine(decompiledRange.startLine);
        int endSourceLine = getSourceLine(decompiledRange.endLine);
        return new LineRange(startSourceLine, endSourceLine);
    }

    public void setClassNode(ClassNode classNode) {
        this.classNode = classNode;
    }

    public ClassNode getClassNode() {
        return classNode;
    }
}
