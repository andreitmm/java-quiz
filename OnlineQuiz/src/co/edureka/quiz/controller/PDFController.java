package co.edureka.quiz.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.DocumentException;

import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;

import co.edureka.quiz.Exam;
import co.edureka.quiz.QuizQuestion;

/**
 * Servlet implementation class PDFController
 */
@WebServlet("/exam/report/generatePDF")
public class PDFController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
		
    public PDFController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PdfWriter writer=null;
		
        Exam exam=(Exam)request.getSession().getAttribute("currentExam");
		
		request.setAttribute("totalQuestion",exam.getTotalNumberOfQuestions());
		
		ArrayList<QuizQuestion> reviewQuestionList=new ArrayList<QuizQuestion>();
		
		Document dom=exam.getDom();
		
		com.itextpdf.text.Document document = new com.itextpdf.text.Document();
		response.setContentType("application/pdf");
        try {
			writer=PdfWriter.getInstance(document, response.getOutputStream());
		} catch (DocumentException e1) {
			e1.printStackTrace();
		}
		
        
        
		for(int i=0;i<exam.getTotalNumberOfQuestions();i++)
		{
			int number=i;
			String options[]=new String[4];
		    String question=null;
		    int correct=0;
		   
			NodeList qList=dom.getElementsByTagName("question");
		    NodeList childList=qList.item(i).getChildNodes();
		    
		 
		    int counter=0;
		    
		    for (int j = 0; j < childList.getLength(); j++) 
		    {
	            Node childNode = childList.item(j);
	            if ("answer".equals(childNode.getNodeName()))
	            {
	                options[counter]=childList.item(j).getTextContent();
	                counter++;
	            }
	            else if("quizquestion".equals(childNode.getNodeName()))
	            {
	            	question=childList.item(j).getTextContent();
	            }
	            else if("correct".equals(childNode.getNodeName()))
	            {
	            	correct=Integer.parseInt(childList.item(j).getTextContent());
	            }
	            
	        }		  
			
			QuizQuestion q=new QuizQuestion();
			q.setQuestionNumber(number);
			q.setQuestion(question);
			q.setCorrectOptionIndex(correct);
			q.setQuestionOptions(options);
		    q.setUserSelected(exam.getUserSelectionForQuestion(i));
			reviewQuestionList.add(number,q);
					
		}
		
		String username=(String) request.getSession().getAttribute("user");
		String examname=(String) request.getSession().getAttribute("exam");
		String started=(String) request.getSession().getAttribute("started");
		try {
			writer.setEncryption(username.getBytes(),username.getBytes(),
			        PdfWriter.ALLOW_PRINTING, PdfWriter.ENCRYPTION_AES_128);
		} catch (DocumentException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		document.open();
	
		try {document.add(new Phrase("                                     "
				+ "          "+examname.toUpperCase()+" Quiz                 ",new Font(FontFamily.HELVETICA, 16, Font.BOLD, BaseColor.BLACK)));
		     document.add(new Paragraph("\n"));
			
			document.add(new Phrase("User : "+username+"                                                              "
					+ "         "+"Started : "+started));
			
			document.add(new Paragraph("\n"));
			
		} catch (DocumentException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		int qi=1;
		try{
		for(QuizQuestion qe: reviewQuestionList)
		{    
			String que=qe.getQuestion();
			System.out.println((qe.getQuestion()));
			document.add(new Phrase("Q"+qi+". "+que,new Font(FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.BLACK)));
			qi++;
			document.add(new Paragraph("\n"));
			int i=1;
			for(String s:qe.getQuestionOptions())
			{
				System.out.println(s);
				document.add(new Chunk(i+". "+s));
				document.add(new Paragraph("\n"));
				
				
				i++;
			}
			
			
			int correct=qe.getCorrectOptionIndex()+1;
			document.add(new Phrase("Correct Answer : "+correct,new Font(FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.GREEN)));
			
			document.add(new Paragraph("\n"));
			if(qe.getUserSelected()==-1){
			document.add(new Phrase("Unanswered",new Font(FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.RED)));
			}else{
				document.add(new Phrase("You Chose : "+qe.getUserSelected(),new Font(FontFamily.HELVETICA, 12, Font.NORMAL, new BaseColor(9,154,251))));
			}
			document.add(new Paragraph("\n"));
			if(qe.getUserSelected()==(qe.getCorrectOptionIndex()+1)){
				Image image1 = Image.getInstance("D:\\correct2.png");
		        document.add(image1);
			}else{
				Image image1 = Image.getInstance("D:\\wrong.png");
		        document.add(image1);
			}
			
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("\n"));
			document.add(new Paragraph("\n"));
			
			
		}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		try {
			document.add(new Phrase("This PDF is created using iText, Thanks to Bruno Lowagie",new Font(FontFamily.COURIER, 16, Font.BOLDITALIC, BaseColor.BLACK)));
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	      document.close();
		
		
		
       
		}
		
	}

	
