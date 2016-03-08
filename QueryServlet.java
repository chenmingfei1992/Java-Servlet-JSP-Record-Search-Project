package eie320;

/*import the packages in sql to accessing the data in books.sql*/
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/*import the packages in QueryServlet get request in from client and return response back from the server*/
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet implementation class QueryServlet
 */

/*With this annotation, the contents of ¡°web.xml¡± are ignored.*/
@WebServlet("/QueryServlet")


public class QueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public QueryServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	
    protected void doGet(HttpServletRequest request,
			 HttpServletResponse response) 
			throws ServletException, IOException {
         
 
/*Defines a set of methods (  dispatch requests)that a QueryServlet uses to
 *  communicate with its QueryServlet container.                */
ServletContext context = getServletContext();

/*Returns the value of a request parameter as a String, then store in the String authorID*/
String authorID = request.getParameter("authorID");

/*First, we should justify whether the authorID entered by client is an integer or not.*/
/*Second,WE define a boolean variable called isAllNumber, as long as there is one digit is 
 * not a integer, then isAllNumber is assigned to false/*
/*Third we use a for loop to check every digit, the method is using charAt() to get
 *  every digit and then compare with'0' and '9', if it is not in the range, return false.
 */
Boolean isAllNumber=true; 
for(int i=0;i<authorID.length();i++)
	{if(authorID.charAt(i)<'0' || authorID.charAt(i)>'9')
	isAllNumber=false;}

/*If the input authorID is not an integer, we should set the attribute "error" with
 * String "AuthorID must be an integer !" and then dispatch it to the user-query.jsp.
 * then, user-query.jsp will run again with the error message  showed,the client 
 * will be asked to enter in another authorID again.
 */
if(!isAllNumber)
{request.setAttribute("error", "AuthorID must be an integer !");
response.setContentType("text/plain");
RequestDispatcher dispatcher = 	
	context.getRequestDispatcher("/user-query.jsp");
dispatcher.forward(request, response);
     }	

 else
	 /*If the input authorID is  an integer, we should call the QueryServlet(String s) method
	  * we send the String authorID as parameter and then we will get a result,the result can 
	  * be the value of fullname in book.sql or null.
	  */
       
	 /*If the result from getQueryResult(String s) is null, it can be considered into two cases.
	  * One case is the client did not enter anything and click the Query, if this occurs, we should 
	  * set the "error" with String " Please type in an authorID before click query,thank you"
	  */
	 if(getQueryResult(authorID)==null)
     {   if(authorID.length()==0)
        { request.setAttribute("error"," Please type in an authorID before click query,thank you");}
     /* The other case is the authorID is not in the range of books.sql,the method did not find the record.
      * If this occurs, we should set the "No record found"
	  */
         else
            {request.setAttribute("error", "No record found");}

     /*Either of the two cases occurs, we should dispatch it to the user-query.jsp using 
      * context.getRequestDispatcher("/user-query.jsp");
      * then, user-query.jsp will run again with the error message  showed,the client 
      * will be asked to enter in another authorID again.
      */
            response.setContentType("text/plain");
            RequestDispatcher dispatcher = 	
	        context.getRequestDispatcher("/user-query.jsp");
            dispatcher.forward(request, response);
            }
     
    
     /*If the result from getQueryResult(String s) is the record obtained from the books.sql, which
      * is the name of the author with the authorID the client typed in, then we should set the 
      * attribute fullname with getQueryResult(authorID), which is the record it found.
      * Set the attribute authorID with the authorID the client typed in.
      * Then dispatch it to the show-result.jsp to show the searching result to the client.
      */
     
       else
      {
       request.setAttribute("authorID", new Integer(authorID));
       request.setAttribute("fullname", getQueryResult(authorID));

       response.setContentType("text/plain");

       RequestDispatcher dispatcher = 	
	   context.getRequestDispatcher("/show-result.jsp");
       dispatcher.forward(request, response);
      } 
}



    
public String getQueryResult(String authorID)
{   
	/*We declare a String varibale to store the comparing result and then return*/
	String fullname=null;

    // database URL       
    final String DATABASE_URL = "jdbc:mysql://localhost:3306/books";
    Connection connection = null; // manages connection
    Statement statement = null; // query statement
    ResultSet resultSet = null; // manages results
  
    // connect to database books and query database
    try 
    {
       // establish connection to database                              
       connection = DriverManager.getConnection( 
			 DATABASE_URL, "guest", "guest" );

       // create Statement for querying database
       statement = connection.createStatement();
       
       // query database                                        
       resultSet = statement.executeQuery(            
          "SELECT authorID, firstName, lastName FROM authors" );
       
       // process query results
       ResultSetMetaData metaData = resultSet.getMetaData();
   
       
      
       while ( resultSet.next() ) 
       {   
    	   /*Compare the String authorID with the String resultSet.getString(1), which is the
    	    * fist the row number in the first column, in fact, it is actual the authorID */ 
    	   /*The result is the substraction  of the ASCII code, if it is equal to 0, that means t
    	    * the two strings are identical, then the authorID matches.
    	    * Then we store the String in the second and third column in the fullname.
    	    */
          if (resultSet.getString(1).compareTo(authorID)==0)
        	  {
        	   fullname=resultSet.getString(2)+" "+resultSet.getString(3);}
         
        } // end while
    }  // end try
       
       
    catch ( SQLException sqlException )                                
    {                                                                  
       sqlException.printStackTrace();
    } // end catch                                                     
    finally // ensure resultSet, statement and connection are closed
    {                                                             
       try                                                        
       {                                                          
          resultSet.close();                                      
          statement.close();                                      
          connection.close();                                     
       } // end try                                               
       catch ( Exception exception )                              
       {                                                          
          exception.printStackTrace();                            
       } // end catch                                             
    } // end finally                                              
      return fullname;
       }
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
