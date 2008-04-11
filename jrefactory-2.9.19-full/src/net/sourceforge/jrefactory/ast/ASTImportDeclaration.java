/*
 *  Author:  Mike Atkinson
 *
 *  This software has been developed under the copyleft
 *  rules of the GNU General Public License.  Please
 *  consult the GNU General Public License for more
 *  details about use and distribution of this software.
 */
package net.sourceforge.jrefactory.ast;

import net.sourceforge.jrefactory.parser.JavaParserVisitor;
import net.sourceforge.jrefactory.parser.JavaParser;

/**
 *  Stores an import declaration that appears at the beginning of a java file.
 *
 * @author    Mike Atkinson
 * @since     jRefactory 2.9.0, created October 16, 2003
 */
public class ASTImportDeclaration extends SimpleNode {
   private boolean importPackage;
   private boolean staticImport;// JDK 1.5


   /**
    *  Constructor for the ASTImportDeclaration node.
    *
    * @param  identifier  The id of this node (JJTIMPORTDECLARATION).
    */
   public ASTImportDeclaration(int identifier) {
      super(identifier);
   }


   /**
    *  Constructor for the ASTImportDeclaration node.
    *
    * @param  parser      The JavaParser that created this ASTImportDeclaration node.
    * @param  identifier  The id of this node (JJTIMPORTDECLARATION).
    */
   public ASTImportDeclaration(JavaParser parser, int identifier) {
      super(parser, identifier);
   }


   /**
    *  Set when including everything in a package
    *
    * @param  way  <code>true</code> if we are importing the package.
    */
   public void setImportPackage(boolean way) {
      importPackage = way;
   }


   /**
    *  Set when including everything in a package. JDK 1.5 indicate that this is a static import.
    *
    * @param  isStatic  <code>true</code> if we are importing statics from the package
    * @since            JRefactory 2.7.00
    */
   public void setStaticImport(boolean isStatic) {
      staticImport = isStatic;
   }


   /**
    *  Return whether we are importing a package
    *
    * @return    <code>true</code> if the whole package is being imported (with ".*;").
    */
   public boolean isImportingPackage() {
      return importPackage;
   }


   /**
    *  Return whether we are importing a the static from this package. JDK 1.5
    *
    * @return    <code>true</code> if we are importing statics from the package
    * @since     JRefactory 2.7.00
    */
   public boolean isStaticImport() {
      return staticImport;
   }


   /**
    *  Gets the importOnDemand attribute of the ASTImportDeclaration node.
    *
    * @return    <code>true</code> if the whole package was imported (with ".*;").
    */
   public boolean isImportOnDemand() {
      return importPackage;
   }


   /**
    *  Gets the importedNameNode attribute of the ASTImportDeclaration node.
    *
    * @return    The name of the class or package being imported.
    */
   public ASTName getImportedNameNode() {
      return (ASTName)jjtGetFirstChild();
   }



   /**
    *  Accept the visitor.
    *
    * @param  visitor  An implementation of JavaParserVisitor that processes the ASTImportDeclaration node.
    * @param  data     Some data being passed between the visitor methods.
    * @return          Usually the data parameter (possibly modified).
    */
   public Object jjtAccept(JavaParserVisitor visitor, Object data) {
      return visitor.visit(this, data);
   }

}

