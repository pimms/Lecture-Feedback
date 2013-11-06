package chipmunk.unlimited.feedback;

import java.io.ByteArrayInputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * @class TimeEditParser
 * Parser of TimeEdit HTML. The parsing is done using XPath and
 * DOM traversal. 
 */
public class TimeEditParser extends AsyncHttpResponseHandler {
	/* The constants defines how the parser interprets
	 * the received HTML.  
	 */
	public static final int CONTENT_TIMETABLE = 1;
	
	/* Returns the root node containing the time-table contents */
	private static final String TIMETABLE_XPATH_ROOT = "//tbody[count(tr[@class='columnHeaders']) != 0]";
	
	private int mContentType = 1;
	
	
	public TimeEditParser(int contentType) {
		mContentType = contentType;
	}
	
	
	@Override 
	public void onSuccess(String response) {
		if (mContentType == CONTENT_TIMETABLE) {
			parseTimeTable(response);
		}
	}
	
	@Override
	public void onFailure(Throwable throwable, String response) {
		
	}
	
	
	/**
	 * Parse and retrieve lecture data from a time-table webpage.
	 * 
	 * @param rawHtml
	 * HTML expected to make up a TimeEdit time-table page.
	 */
	private void parseTimeTable(String rawHtml) {
		try {
			rawHtml = commentHtmlLines(rawHtml, 3, 10);
			Log.d("HTML", rawHtml);
			
			DocumentBuilder b = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = b.parse(new ByteArrayInputStream(rawHtml.getBytes()));
			
			/* Retrieve the <tbody> element containing the 
			 * desired data. 3 <tbody> elements exists, but only one
			 * contain usedful data. The XPATH expression should 
			 * filter out the other two. 
			 */
			XPath xpath = XPathFactory.newInstance().newXPath();
			NodeList nodes = (NodeList)xpath.evaluate(TIMETABLE_XPATH_ROOT, 
								doc.getDocumentElement(), XPathConstants.NODESET);
			
			if (nodes.getLength() != 1) {
				/* We're likely without internet connection, or the
				 * webpage structure has changed.  
				 */
				displayErrorDialog("Invalid DOM structure received. Time-table expected.");
			} else {
				/* Parse the <tbody> element */
				Element tbody = (Element)nodes.item(0);
				parseTimeTableDOM(tbody);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			displayErrorDialog(ex.getMessage());
		} 
	}
	
	private void parseTimeTableDOM(Element tbody) {
		String date = "";
		
		// Discard the first child 
		Node node = tbody.getFirstChild();
		
		while (node != null ) {
			String attrClass = getNodeAttribute(node, "class");
			
			if (attrClass == null) {
				/* The element indicates a new day */
				Node dateNode = node.getFirstChild().getNextSibling();
				date = dateNode.getTextContent();
			} else if (attrClass == "clickable2") {
				/* The first two nodes are irrelevant */
				Node tdNode = node.getFirstChild().getNextSibling().getNextSibling();
				
				String course = tdNode.getTextContent();
				tdNode = tdNode.getNextSibling();
				
				String room = tdNode.getTextContent();
				tdNode = tdNode.getNextSibling();
				
				String lecturer = tdNode.getTextContent();
				
				Log.d("DEBUGZ", course + lecturer + room);
			}
			
			node = node.getNextSibling();
		}
	}
	
	
	/**
	 * Comment lines in the HTML.
	 * 
	 * @param html
	 * The webpage to be cleaned up.
	 * 
	 * @param firstLine
	 * 0-based index of the first line to be commented
	 * 
	 * @param lastLine
	 * 0-based index of the last line to be commented. Should
	 * be larger than firstLine.
	 * 
	 * @return
	 * The cleaned up webpage.
	 */
	private String commentHtmlLines(String html, int firstLine, int lastLine) {
		if (lastLine <= firstLine) {
			return html;
		}
		
		StringBuilder builder = new StringBuilder(html);
		int index = 0;
		int line = 0;
		
		while (index < builder.length()) {
			char[] ch = new char[1];
			builder.getChars(index, index+1, ch, 0);
			index++;
			
			if (ch[0] == '\n') {
				line++;
			}
			
			if (line == firstLine) {
				builder.insert(index, "<!-- ");
				firstLine = -1;
			}
			
			if (line + 1 == lastLine) {
				builder.insert(index, " --> ");
				break;
			}
		}
		
		return builder.toString();
	}
	
	private String getNodeAttribute(Node node, String attribute) {
		Node attr = node.getAttributes().getNamedItem(attribute);
		
		if (attr != null) {
			return attr.getTextContent();
		}
		
		return null;
	}
	
	/**
	 * Display an error dialog
	 */
	private void displayErrorDialog(String message) {
		Log.e("Until further ado", message);
	}
}






























