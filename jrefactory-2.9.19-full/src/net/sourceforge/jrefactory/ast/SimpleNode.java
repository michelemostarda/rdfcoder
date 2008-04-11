/*
 *  Author:  Mike Atkinson
 *
 *  This software has been developed under the copyleft
 *  rules of the GNU General Public License.  Please
 *  consult the GNU General Public License for more
 *  details about use and distribution of this software.
 */
package net.sourceforge.jrefactory.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import net.sourceforge.jrefactory.parser.*;


/**
 *  This object is the base class for all items in the AST (abstract syntax tree).
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class SimpleNode implements Node {

   /**  Description of the Field  */
   protected Node parent;
   /**  Description of the Field  */
   protected Node[] children;
   /**  Description of the Field  */
   protected int id;
   /**  Description of the Field  */
   protected JavaParser parser;
   /**  Description of the Field  */
   public Vector specials;
   private int beginLine = -1;
   private int endLine;
   private int beginColumn = -1;
   private int endColumn;

   private Scope scope;


   /**
    *  Constructor for the SimpleNode object
    *
    * @param  i  Description of Parameter
    */
   public SimpleNode(int i) {
      id = i;
      specials = null;
   }


   /**
    *  Constructor for the SimpleNode object
    *
    * @param  parser  Description of Parameter
    * @param  i       Description of Parameter
    */
   public SimpleNode(JavaParser parser, int i) {
      this(i);
      this.parser = parser;
   }


   /**
    *  Sets the scope attribute of the SimpleNode object
    *
    * @param  scope  The new scope value
    */
   public void setScope(Scope scope) {
      this.scope = scope;
   }


   /**
    *  Gets the beginLine attribute of the SimpleNode object
    *
    * @return    The beginLine value
    */
   public int getBeginLine() {
      if (beginLine != -1) {
         return beginLine;
      } else {
         if ((children != null) && (children.length > 0)) {
            return ((SimpleNode)children[0]).getBeginLine();
         } else {
            throw new RuntimeException("Unable to determine begining line of Node.");
         }
      }
   }


   /**
    *  Gets the beginColumn attribute of the SimpleNode object
    *
    * @return    The beginColumn value
    */
   public int getBeginColumn() {
      if (beginColumn != -1) {
         return beginColumn;
      } else {
         if ((children != null) && (children.length > 0)) {
            return ((SimpleNode)children[0]).getBeginColumn();
         } else {
            throw new RuntimeException("Unable to determine begining line of Node.");
         }
      }
   }


   /**
    *  Gets the endLine attribute of the SimpleNode object
    *
    * @return    The endLine value
    */
   public int getEndLine() {
      return endLine;
   }


   /**
    *  Gets the endColumn attribute of the SimpleNode object
    *
    * @return    The endColumn value
    */
   public int getEndColumn() {
      return endColumn;
   }


   /**
    *  Gets the special associated with a particular key
    *
    * @param  key  the key
    * @return      the value
    */
   public Token getSpecial(String key) {
      if ((specials == null) || (key == null)) {
         return null;
      }

      int last = specials.size();
      for (int ndx = 0; ndx < last; ndx++) {
         NamedToken named = (NamedToken)specials.elementAt(ndx);
         if (named.check(key)) {
            return named.getToken();
         }
      }

      return null;
   }


   /**
    *  Is javadoc required?
    *
    * @return    The required value
    */
   public boolean isRequired() {
      return false;
   }


   /**
    *  Gets the scope attribute of the SimpleNode object
    *
    * @return    The scope value
    */
   public Scope getScope() {
      if (scope == null) {
         return ((SimpleNode)parent).getScope();
      }
      return scope;
   }


   /**
    *  Gets the name attribute of the SimpleNode object
    *
    * @return    The name value
    */
   public String getName() {
      return "";
      //throw new Error("should not occur");
   }


   /**
    *  Gets the image attribute of the SimpleNode object
    *
    * @return    The image value
    */
   public String getImage() {
      return getName();
   }


   /**  Description of the Method  */
   public void jjtOpen() {
      if (parser.token.next != null) {
         beginLine = parser.token.next.beginLine;
         beginColumn = parser.token.next.beginColumn;
      }
   }


   /**  Description of the Method  */
   public void jjtClose() {
      if (beginLine == -1 && (children == null || children.length == 0)) {
         beginColumn = parser.token.beginColumn;
      }
      if (beginLine == -1) {
         beginLine = parser.token.beginLine;
      }
      endLine = parser.token.endLine;
      endColumn = parser.token.endColumn;
   }


   public void setLineAndColumnInfo(int beginLine, int beginColumn, int endLine, int endColumn) {
      this.beginLine = beginLine;
      this.beginColumn = beginColumn;
      this.endLine = endLine;
      this.endColumn = endColumn;
   }


   /**
    *  A unit test for JUnit
    *
    * @param  i  Description of Parameter
    */
   public void testingOnly__setBeginLine(int i) {
      this.beginLine = i;
   }


   /**
    *  A unit test for JUnit
    *
    * @param  i  Description of Parameter
    */
   public void testingOnly__setBeginColumn(int i) {
      this.beginColumn = i;
   }



   /**
    *  Return the id for this node
    *
    * @return    the id
    */
   public int jjtGetID() {
      return id;
   }


   /**
    *  Description of the Method
    *
    * @param  n  Description of Parameter
    */
   public void jjtSetParent(Node n) {
      parent = n;
   }


   /**
    *  Description of the Method
    *
    * @return    Description of the Returned Value
    */
   public Node jjtGetParent() {
      return parent;
   }


   /**
    *  Description of the Method
    *
    * @param  n  Description of Parameter
    * @param  i  Description of Parameter
    */
   public void jjtAddChild(Node n, int i) {
      if (children == null) {
         children = new Node[i + 1];
      } else if (i >= children.length) {
         Node[] c = new Node[i + 1];
         System.arraycopy(children, 0, c, 0, children.length);
         children = c;
      }
      children[i] = n;
      n.jjtSetParent(this);
   }


   /**
    *  Description of the Method
    *
    * @param  n  Description of Parameter
    */
   public void jjtAddFirstChild(Node n) {
      if (children == null) {
         children = new Node[1];
      }
      children[0] = n;
      n.jjtSetParent(this);
   }


   /**
    *  Insert the node numbered i
    *
    * @param  n  Description of Parameter
    * @param  i  The index of the node to remove
    */
   public void jjtInsertChild(Node n, int i) {
      if (children == null) {
         children = new Node[i + 1];
      } else {
         Node[] c = new Node[Math.max(children.length + 1, i + 1)];
         System.arraycopy(children, 0, c, 0, i);
         System.arraycopy(children, i, c, i + 1, children.length - i);
         children = c;
      }

      //  Store the node
      children[i] = n;
      n.jjtSetParent(this);
   }


   /**
    *  Description of the Method
    *
    * @param  i  Description of Parameter
    * @return    Description of the Returned Value
    */
   public Node jjtGetChild(int i) {
      return children[i];
   }


   /**
    *  This method returns a child node. The children are numbered from zero, left to right.<p>
    *
    *  Same as jjtGetFirstChild();
    *
    * @return    Description of the Returned Value
    */
   public Node jjtGetFirstChild() {
      return children[0];
   }


   /**
    *  Description of the Method
    *
    * @return    Description of the Returned Value
    */
   public int jjtGetNumChildren() {
      return (children == null) ? 0 : children.length;
   }


   /**
    *  Description of the Method
    *
    * @return    Description of the Returned Value
    */
   public boolean hasAnyChildren() {
      return ((children != null) && (children.length > 0));
   }


   /**
    *  Remove the node numbered i
    *
    * @param  i  The index of the node to remove
    */
   public void jjtDeleteChild(int i) {
      if ((children == null) || (children.length < i) || (i < 0)) {
         System.out.println("Skipping this delete operation");
      } else {
         Node[] c = new Node[children.length - 1];
         System.arraycopy(children, 0, c, 0, i);
         System.arraycopy(children, i + 1, c, i, children.length - i - 1);
         children = c;
      }
   }


   /**
    *  Description of the Method
    *
    * @param  key    Description of Parameter
    * @param  value  Description of Parameter
    */
   public void addSpecial(String key, Token value) {
      if (value == null) {
         return;
      }

      if (specials == null) {
         init();
      }

      specials.addElement(new NamedToken(key, value));
   }


   /**
    *  Removes a special associated with a key
    *
    * @param  key  the special to remove
    */
   public void removeSpecial(String key) {
      if ((specials == null) || (key == null)) {
         return;
      }

      int last = specials.size();
      for (int ndx = 0; ndx < last; ndx++) {
         NamedToken named = (NamedToken)specials.elementAt(ndx);
         if (named.check(key)) {
            specials.removeElementAt(ndx);
            return;
         }
      }
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  Description of Parameter
    * @param  data     Description of Parameter
    * @return          Description of the Returned Value
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }


   /**
    *  Accept the visitor.
    *
    * @param  visitor  Description of Parameter
    * @param  data     Description of Parameter
    * @return          Description of the Returned Value
    */
   public Object childrenAccept(JavaParserVisitor visitor, Object data) {
      if (children != null) {
         for (int i = 0; i < children.length; ++i) {
            if (children[i] != null) {
               children[i].jjtAccept(visitor, data);
            } else {
               // FIXME warn
            }
         }
      }
      return data;
   }


   /**
    *  You can override these two methods in subclasses of SimpleNode to
    *  customize the way the node appears when the tree is dumped.  If
    *  your output uses more than one line you should override
    *  toString(String), otherwise overriding toString() is probably all
    *  you need to do.
    *
    * @return    Description of the Returned Value
    */
   public String toString() {
      return JavaParserTreeConstants.jjtNodeName[id];
   }


   /**
    *  You can override these two methods in subclasses of SimpleNode to
    *  customize the way the node appears when the tree is dumped.  If
    *  your output uses more than one line you should override
    *  toString(String), otherwise overriding toString() is probably all
    *  you need to do.
    *
    * @param  prefix  Description of Parameter
    * @return         Description of the Returned Value
    */
   public String toString(String prefix) {
      return prefix + toString();
   }



   /**
    *  Dump the node data (including the special tokens) to <code>System.err</code>
    *
    *  Override this method if you want to customize how the node dumps
    *  out its children.
    *
    * @param  prefix  prefixed to every line of the output
    */
   public void dump(String prefix) {
      System.err.println(dumpString(prefix));
   }


   /**
    *  Dump the node data (including the special tokens) to a String.
    *
    * @param  prefix  prefixed to every line of the output
    * @return         dump of the Node data.
    * @since          JRefactory 2.7.00
    */
   public String dumpString(String prefix) {
      StringBuffer sb = new StringBuffer(toString(prefix));
      sb.append(prefix+"parent="+parent+"\n");
      sb.append(prefix+"specials={\n");
      java.util.Iterator i=specials.iterator();
      while (i.hasNext()) {
         sb.append(prefix+"   "+i.next()+"\n");
      }
      sb.append(prefix+"}\n");
      dumper(this, sb, prefix + " ");
      return sb.toString();
   }


   /**
    *  Description of the Method
    *
    * @param  targetType  Description of the Parameter
    * @return             Description of the Return Value
    */
   public List findChildrenOfType(Class targetType) {
      List list = new ArrayList();
      findChildrenOfType(targetType, list);
      return list;
   }


   /**
    *  Description of the Method
    *
    * @param  targetType  Description of the Parameter
    * @param  results     Description of the Parameter
    */
   public void findChildrenOfType(Class targetType, List results) {
      findChildrenOfType(this, targetType, results, true);
   }


   /**
    *  Description of the Method
    *
    * @param  targetType                Description of the Parameter
    * @param  results                   Description of the Parameter
    * @param  descendIntoNestedClasses  Description of the Parameter
    */
   public void findChildrenOfType(Class targetType, List results, boolean descendIntoNestedClasses) {
      this.findChildrenOfType(this, targetType, results, descendIntoNestedClasses);
   }


   /**
    *  Description of the Method
    *
    * @return    Description of the Returned Value
    */
   protected String printModifiers() {
      return "";
   }


   /**  Initializes any variables that are not required  */
   protected void init() {
      if (specials == null) {
         specials = new Vector();
      }
   }


   /**
    *  Description of the Method
    *
    * @param  node                      Description of the Parameter
    * @param  targetType                Description of the Parameter
    * @param  results                   Description of the Parameter
    * @param  descendIntoNestedClasses  Description of the Parameter
    */
   private void findChildrenOfType(Node node, Class targetType, List results, boolean descendIntoNestedClasses) {
      if (node.getClass().equals(targetType)) {
         results.add(node);
      }
      if (node.getClass().equals(ASTNestedClassDeclaration.class) && !descendIntoNestedClasses) {
         return;
      }
      if (node.getClass().equals(ASTClassBodyDeclaration.class) && ((ASTClassBodyDeclaration)node).isAnonymousInnerClass() && !descendIntoNestedClasses) {
         return;
      }
      for (int i = 0; i < node.jjtGetNumChildren(); i++) {
         Node child = node.jjtGetChild(i);
         if (child.jjtGetNumChildren() > 0) {
            findChildrenOfType(child, targetType, results, descendIntoNestedClasses);
         } else {
            if (child.getClass().equals(targetType)) {
               results.add(child);
            }
         }
      }
   }


   /**
    *  Description of the Method
    *
    * @param  node    Description of Parameter
    * @param  sb      Description of Parameter
    * @param  prefix  Description of Parameter
    */
   private static void dumper(SimpleNode node, StringBuffer sb, String prefix) {
      if (node.children != null) {
         for (int i = 0; i < node.children.length; ++i) {
            SimpleNode n = (SimpleNode)node.children[i];
            if (n != null) {
               sb.append(n.toString(prefix)).append(" ").append(n.getImage());
               sb.append(n.printModifiers());
               dumper(n, sb, prefix + " ");
            }
         }
      }
   }

}

