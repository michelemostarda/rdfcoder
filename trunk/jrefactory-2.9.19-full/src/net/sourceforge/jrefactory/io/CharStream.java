package net.sourceforge.jrefactory.io;

import java.io.InputStreamReader;


/**
 *  Basic character stream. The javacc tool creates one of four different types
 *  of character streams. To be able to switch between different types, I've
 *  created this parent class. The parent class invokes the appropriate child
 *  class that was created by javacc. <P>
 *
 *
 *@author     Mike Atkinson
 *@author     <a href="mailto:JRefactory@ladyshot.demon.co.uk">Mike Atkinson</a>
 *@version    $Id$
 */
public abstract class CharStream {
   /**
    *  Are there more characters available
    */
   protected int available;
   /**
    *  The buffer column
    */
   protected int bufcolumn[];

   /**
    *  The buffer
    */
   protected char[] buffer;
   /**
    *  The buffer line
    */
   protected int bufline[];
   /**
    *  The buffer location
    */
   public int bufpos = -1;
   /**
    *  The buffer size
    */
   protected int bufsize;

   /**
    *  The column index
    */
   protected int column = 0;
   /**
    *  Index into the buffer
    */
   protected int inBuf = 0;

   /**
    *  The input
    */
   protected java.io.Reader inputStream;
   /**
    *  The line index
    */
   protected int line = 1;
   /**
    *  The maximum next character index
    */
   protected int maxNextCharInd = 0;

   /**
    *  Was the previous character a CR?
    */
   protected boolean prevCharIsCR = false;
   /**
    *  Was the previous character a LF?
    */
   protected boolean prevCharIsLF = false;
   /**
    *  Index of the current token's starting point
    */
   protected int tokenBegin;
   /**
    *  Use the ascii character stream
    */
   public final static int ASCII = 1;
   /**
    *  Use the unicode character stream
    */
   public final static int FULL_CHAR = 3;
   /**
    *  Use the Java character stream
    */
   public final static int JAVA_LIKE = 4;
   /**
    *  Use the unicode character stream
    */
   public final static int UNICODE = 2;

   /**
    *  This field determines which type of character stream to use
    */
   private static int charStreamType = -1;

   /**
    *  Is this a parser
    */
   public final static boolean staticFlag = false;


   /**
    *  Description of the Method
    *
    *@return    Description of the Returned Value
    */
   public abstract String GetImage();


   /**
    *  Description of the Method
    *
    *@param  len  Description of Parameter
    *@return      Description of the Returned Value
    */
   public abstract char[] GetSuffix(int len);


   /**
    *  Gets the BeginColumn attribute of the CharStream class
    *
    *@return    The BeginColumn value
    */
   public int getBeginColumn() {
		return bufcolumn[tokenBegin];
	}

   /**
    *  Gets the BeginLine attribute of the CharStream class
    *
    *@return    The BeginLine value
    */
   public int getBeginLine(){
		return bufline[tokenBegin];
	}

   /**
    *  Gets the Column attribute of the CharStream class
    *
    *@return    The Column value
   * @deprecated 
   * @see #getEndColumn
    */
   public int getColumn() {
       return bufcolumn[bufpos];
   }


   /**
    *  Gets the EndColumn attribute of the CharStream class
    *
    *@return    The EndColumn value
    */
   public int getEndColumn() {
       return bufcolumn[bufpos];
   }
        

   /**
    *  Gets the EndLine attribute of the CharStream class
    *
    *@return    The EndLine value
    */
   public int getEndLine() {
       return bufline[bufpos];
   }

   /**
    *  Gets the Line attribute of the CharStream class
    *
    *@return    The Line value
   * @deprecated 
   * @see #getEndLine
    */
   public int getLine() {
		return bufline[bufpos];
	}


   /**
    *  Description of the Method
    *
    *@return                          Description of the Returned Value
    *@exception  java.io.IOException  Description of Exception
    */
   public char BeginToken() throws java.io.IOException {
            tokenBegin = -1;
            char c = readChar();
            tokenBegin = bufpos;

            return c;
   }

   /**
    *  Description of the Method
    *
    *@param  dstream      Description of Parameter
    *@param  startline    Description of Parameter
    *@param  startcolumn  Description of Parameter
    *@param  buffersize   Description of Parameter
    */
   public abstract void ReInit(java.io.Reader dstream, int startline, int startcolumn, int buffersize);


   /**
    *  Description of the Method
    *
    *@param  dstream      Description of Parameter
    *@param  startline    Description of Parameter
    *@param  startcolumn  Description of Parameter
    */
    public void ReInit(java.io.Reader dstream, int startline, int startcolumn) {
        ReInit(dstream, startline, startcolumn, 4096);
    }


   /**
    *  Description of the Method
    *
    *@param  dstream      Description of Parameter
    *@param  startline    Description of Parameter
    *@param  startcolumn  Description of Parameter
    *@param  buffersize   Description of Parameter
    */
   public void ReInit(java.io.InputStream dstream, int startline, int startcolumn, int buffersize) {
       ReInit(dstream, startline, startcolumn, buffersize);
    }

   /**
    *  Description of the Method
    *
    *@param  dstream      Description of Parameter
    *@param  startline    Description of Parameter
    *@param  startcolumn  Description of Parameter
    */
   public void ReInit(java.io.InputStream dstream, int startline, int startcolumn) {
       ReInit(new java.io.InputStreamReader(dstream), startline, startcolumn, 4096);
    }


   /**
    *  Description of the Method
    *
    *@param  newLine  Description of Parameter
    *@param  newCol   Description of Parameter
    */
   public abstract void adjustBeginLineColumn(int newLine, int newCol);


   /**
    *  Description of the Method
    *
    *@param  amount  Description of Parameter
    */
   public abstract void backup(int amount);


   /**
    *  Description of the Method
    *
    *@return                          Description of the Returned Value
    *@exception  java.io.IOException  Description of Exception
    */
   public abstract char readChar() throws java.io.IOException;


   public static void setCharStreamType(int type) {
      charStreamType = type;
   }

   /**
    *  Constructor for the CharStream object
    *
    *@param  dstream      Description of Parameter
    *@param  startline    Description of Parameter
    *@param  startcolumn  Description of Parameter
    *@param  buffersize   Description of Parameter
    *@return              Description of the Returned Value
    */
   public static CharStream make(java.io.Reader dstream, int startline,
                                 int startcolumn, int buffersize) {
      if (charStreamType == JAVA_LIKE) {
          return new JavaCharStream(dstream, startline, startcolumn, buffersize);
      } else if (charStreamType != UNICODE) {
          return new ASCII_CharStream(dstream, startline, startcolumn, buffersize, charStreamType == FULL_CHAR);
      } else {
          return new SimpleCharStream(dstream, startline, startcolumn, buffersize);
      }
   }


   /**
    *  Constructor for the CharStream object
    *
    *@param  dstream      Description of Parameter
    *@param  startline    Description of Parameter
    *@param  startcolumn  Description of Parameter
    *@return              Description of the Returned Value
    */
   public static CharStream make(java.io.Reader dstream, int startline, int startcolumn) {
      return make(dstream, startline, startcolumn, 4096);
   }


   /**
    *  Constructor for the CharStream object
    *
    *@param  dstream      Description of Parameter
    *@param  startline    Description of Parameter
    *@param  startcolumn  Description of Parameter
    *@param  buffersize   Description of Parameter
    *@return              Description of the Returned Value
    */
   public static CharStream make(java.io.InputStream dstream, int startline,
                                 int startcolumn, int buffersize) {
       return make(new InputStreamReader(dstream), startline, startcolumn, buffersize);
   }


   /**
    *  Constructor for the CharStream object
    *
    *@param  dstream      Description of Parameter
    *@param  startline    Description of Parameter
    *@param  startcolumn  Description of Parameter
    *@return              Description of the Returned Value
    */
   public static CharStream make(java.io.InputStream dstream, int startline, int startcolumn) {
      return make(new InputStreamReader(dstream), startline, startcolumn, 4096);
   }
}

