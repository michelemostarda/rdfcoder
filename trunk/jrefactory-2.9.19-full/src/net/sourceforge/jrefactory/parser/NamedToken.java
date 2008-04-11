/*
 *  Author:  Mike Atkinson
 *
 *  This software has been developed under the copyleft
 *  rules of the GNU General Public License.  Please
 *  consult the GNU General Public License for more
 *  details about use and distribution of this software.
 */
package net.sourceforge.jrefactory.parser;




/**
 *  Description of the Class
 *
 * @author     Mike Atkinson
 * @since      2.9.11
 * @created    October 14, 1999
 */
public class NamedToken {
   private String id;
   private Token token;


   /**
    *  Creates a named token
    *
    * @param  initID     the id
    * @param  initToken  the token
    * @since             2.9.11
    */
   public NamedToken(String initID, Token initToken) {
      id = initID.intern();
      token = initToken;
   }


   /**
    *  Return the id
    *
    * @return    the id
    * @since     2.9.11
    */
   public String getID() {
      return id;
   }


   /**
    *  Return the token
    *
    * @return    the token
    * @since     2.9.11
    */
   public Token getToken() {
      return token;
   }


   /**
    *  Check if the id matches
    *
    * @param  match  does it match
    * @return        true if it matches
    * @since         2.9.11
    */
   public boolean check(String match) {
      return (match.equals(id));
   }


   /**
    *  Create a human readable representation of the token
    *
    * @return    A representation of the token
    * @since     JRefactory 2.7.00
    */
   public String toString() {
      return "id=" + id + " token=" + token.toString(true);
   }
}

