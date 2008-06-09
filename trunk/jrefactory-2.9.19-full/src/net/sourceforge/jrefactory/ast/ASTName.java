/*
 *  Author:  Mike Atkinson
 *
 *  This software has been developed under the copyleft
 *  rules of the GNU General Public License.  Please
 *  consult the GNU General Public License for more
 *  details about use and distribution of this software.
 */
package net.sourceforge.jrefactory.ast;

import java.util.StringTokenizer;

import net.sourceforge.jrefactory.parser.JavaParserTreeConstants;
import net.sourceforge.jrefactory.parser.JavaParserVisitor;
import net.sourceforge.jrefactory.parser.JavaParser;

/**
 *  Stores a name. The name can consist of a number of parts separated by periods.
 *
 * The name consists of parts which are stored as ASTIdentifier nodes in this objects children.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTName extends SimpleNode implements Cloneable {
   /**  The cached value of the name. */
   protected String name = null;


   /**
    *  Constructor for the ASTName node.
    *
    */
   public ASTName() {
      super(JavaParserTreeConstants.JJTNAME);
   }


   /**
    *  Constructor for the ASTName node.
    *
    * @param  identifier  The id of this node (JJTNAME).
    */
   public ASTName(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTName node.
    *
    * @param  parser      The JavaParser that created this ASTName node.
    * @param  identifier  The id of this node (JJTNAME).
    */
   public ASTName(JavaParser parser, int identifier) {
      super(parser, identifier);
   }



   /**
    *  Add a component of the name
    *
    * @param  ndx    the index of the part requested
    * @param  value  The new NamePart value
    */
   public void setNamePart(int ndx, String value) {
      name = null;
      int count = 0;
      for (int i = 0; i < children.length; i++) {
         if (children[i] instanceof ASTIdentifier) {
            if (count == ndx) {
               ((ASTIdentifier)children[i]).setName(value);
               return;
            }
            count++;
         }
      }
   }


   /**
    *  Convert this object from a string (used for PMD testing)
    *
    * @param  image  The name in form "org.test.AName" for example
    */
   public void setImage(String image) {
      fromString(image);
   }


   /**
    *  Add a component of the name
    *
    * @param  ndx  the index of the part requested
    * @return      the portion of the name requested
    */
   public String getNamePart(int ndx) {
      int count = 0;
      for (int i = 0; i < children.length; i++) {
         if (children[i] instanceof ASTIdentifier) {
            if (count == ndx) {
               return ((ASTIdentifier)children[i]).getName();
            }
            count++;
         }
      }

      return null;
   }


   /**
    *  Get the node's name
    *
    * @return    the name
    */
   public String getName() {
      if (name == null) {
         if (children == null) {
            name = "";
            return name;
         }
         //  Local Variables
         StringBuilder buf = new StringBuilder();
         boolean first = true;

         //  Iterate through the parts
         for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof ASTIdentifier) {
               if (!first) {
                  buf.append(".");
               }
               buf.append(((ASTIdentifier)children[i]).getName());
               first = false;
            }
         }

         //  Return the buffer
         name = buf.toString();
      }
      return name;
   }


   /**
    *  Get the length of the name
    *
    * @return    the number of parts in the name
    */
   public int getNameSize() {
      int count = 0;
      for (int i = 0; i < children.length; i++) {
         if (children[i] instanceof ASTIdentifier) {
            count++;
         }
      }
      return count;
   }


   /**
    *  Add a component of the name
    *
    * @param  ndx    the index of the part requested
    * @param  value  Description of Parameter
    */
   public void insertNamePart(int ndx, String value) {
      name = null;
      int count = 0;
      ASTIdentifier ident = new ASTIdentifier(parser, JavaParserTreeConstants.JJTIDENTIFIER);
      ident.setName(value);
      for (int i = 0; i < children.length; i++) {
         if (children[i] instanceof ASTIdentifier) {
            if (count == ndx) {
               jjtInsertChild(ident, i);
               return;
            }
            count++;
         }
      }
      ident.setLineAndColumnInfo(getBeginLine(), getBeginColumn(), getEndLine(), getEndColumn());
   }


   /**
    *  Set the node's name
    *
    * @param  newName  the new name
    */
   public void addNamePart(String newName) {
      name = null;
      ASTIdentifier ident = new ASTIdentifier(parser, JavaParserTreeConstants.JJTIDENTIFIER);
      ident.setName(newName);
      jjtAddChild(ident, ((children == null) ? 0 : children.length));
   }


   /**
    *  Convert this object from a string
    *
    * @param  input  Description of Parameter
    */
   public void fromString(String input) {
      //  Clean the old one
      name = null;//FIXME? = input
      children = null;

      //  Load it
      StringTokenizer tok = new StringTokenizer(input, ".");
      while (tok.hasMoreTokens()) {
         String next = tok.nextToken();
         //System.out.println("ASTName.fromString.addNamePart("+next+")");
         addNamePart(next);
      }
   }



   /**
    *  Checks to see if the two names are equal
    *
    * @param  other  Description of Parameter
    * @return        Description of the Returned Value
    */
   public boolean equals(Object other) {
      if (other == this) {
         return true;
      }
      if (other instanceof ASTName) {
         ASTName otherName = (ASTName)other;

         if (otherName.getNameSize() == getNameSize()) {
            return getName().equals(otherName.getName());
         }
      }
      return false;
   }


   /**
    *  Computes a hash value for this node.
    *
    * @return    The hash value for this node.
    */
   public int hashCode() {
      if (name == null) {
         name = getName();
      }
      return name.hashCode();
   }


   /**
    *  Determines if two names start with the same series of items
    *
    * @param  otherName  Description of Parameter
    * @return            Description of the Returned Value
    */
   public boolean startsWith(ASTName otherName) {
      //  To start with the other name, the other name must be less than or equal in parts
      if (otherName.getNameSize() > getNameSize()) {
         return false;
      }

      //  Look for the point where they are different
      int last = Math.min(otherName.getNameSize(), getNameSize());
      for (int ndx = 0; ndx < last; ndx++) {
         if (!getNamePart(ndx).equals(otherName.getNamePart(ndx))) {
            return false;
         }
      }

      //  They must be the same
      return true;
   }



   /**
    *  Change starting part. Presumes that otherName is less than the length of the current name.
    *
    * @param  oldBase  Description of Parameter
    * @param  newBase  Description of Parameter
    * @return          Description of the Returned Value
    */
   public ASTName changeStartingPart(ASTName oldBase, ASTName newBase) {
      ASTName result = new ASTName();

      int last = newBase.getNameSize();
      for (int ndx = 0; ndx < last; ndx++) {
         result.addNamePart(newBase.getNamePart(ndx));
      }

      int end = getNameSize();
      int start = oldBase.getNameSize();
      for (int ndx = start; ndx < end; ndx++) {
         result.addNamePart(getNamePart(ndx));
      }
      result.setLineAndColumnInfo(getBeginLine(), getBeginColumn(), getEndLine(), getEndColumn());

      return result;
   }



   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTName node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }
}

