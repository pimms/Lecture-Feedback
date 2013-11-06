package chipmunk.unlimited.feedback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

import com.loopj.android.http.AsyncHttpResponseHandler;

/**
 * @class TimeEditParser
 * Parser of TimeEdit HTML. The parsing is done using XPath and
 * DOM traversal.
 * 
 * This class is heavily based on TobbenTM's implementation
 * of a TimeEdit parser. The TimeEdit HTML is full of invalid
 * HTML, causing the w3c.dom-parser to fail. 
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
			String[][] res = parseTimeTable(response);
			Log.d("ITEM", res.length + " items.");
			for (String[] arr : res) {
				Log.d("ITEM ("+arr.length+")", "[0]="+arr[0] + "[1]=" + arr[1] + "[2]=" + arr[2] + "[3]=" + arr[3] + "[4]=" + arr[4]);
			}
		}
	}
	
	@Override
	public void onFailure(Throwable throwable, String response) {
		
	}
	
	
	public static String[][] parseTimeTable(String html){
        String[] split1 = html
                .replaceAll("<span class=\"tesprite tesprite-floatright tesprite-new\"></span>", "")
                .split("/table");
        String[] split2 = split1[0].split("changeDateLink headline leftRounded t .*\">");

        String[][] results = new String[split2.length-1][6];
        ArrayList<ArrayList<String>> master = new ArrayList<ArrayList<String>>();
        String currentdate = "";

        for(int i = 1; i < split2.length -1; i++){
            currentdate = split2[i].split("</td>")[0].replaceAll(" Today", "");
            String[] split3 = split2[i].split("</tr>");
            master.add(dateEntry(currentdate));
            for(int j = 1; j < split3.length -1; j++){
                ArrayList<String> inner = new ArrayList<String>();
                String[] data = tagData(split3[j]);
                inner.add(currentdate);
                for(String s : data){
                    inner.add(s);
                }
                master.add(inner);
            }
        }
        return dimensionalPortal(master);
    }

    public static String[][] parseTimeTable(String html, boolean room){
        String[] split1 = html
                .replaceAll("<span class=\"tesprite tesprite-floatright tesprite-new\"></span>", "")
                .split("/table");
        String[] split2 = split1[0].split("changeDateLink headline leftRounded t .*\">");
        String startTime, endTime;

        String[][] results = new String[split2.length-1][6];
        ArrayList<ArrayList<String>> master = new ArrayList<ArrayList<String>>();
        String currentdate = "";

        for(int i = 1; i < split2.length -1; i++){
            currentdate = split2[i].split("</td>")[0].replaceAll(" Today", "");
            String[] split3 = split2[i].split("</tr>");
            master.add(dateEntry(currentdate));
            endTime = "0800";
            for(int j = 1; j < split3.length -1; j++){
                ArrayList<String> inner = new ArrayList<String>();
                String[] data = tagData(split3[j]);
                if(room){
                    startTime = data[0].split("\n")[0].replace(":", "");
                    if(
                            Integer.parseInt(startTime) >= 815 &&
                            Integer.parseInt(startTime) > Integer.parseInt(endTime)+16 &&
                            Integer.parseInt(endTime) < 1600 ){
                        master.add(clearEntry(currentdate, timeString(endTime, startTime)));
                        Log.d("TIME", "Added clear entry: " + timeString(endTime, startTime));
                    }
                    endTime = data[0].split("\n")[2].replace(":", "");
                }
                inner.add(currentdate);
                for(String s : data){
                    inner.add(s);
                }
                Log.d("TIME", "Lecture time: \t\t" + data[1]);
                master.add(inner);
            }
        }
        return dimensionalPortal(master);
    }

    public static String[][] search(String html, String term){
        //Log.d("PARSING", "Starting parser");
        List<String> id = new ArrayList<String>();
        List<String> name = new ArrayList<String>();
        List<String> ids = new ArrayList<String>();
        List<String> names = new ArrayList<String>();

        //The reason for splitting at data-id="-1" is that there is no valuable data
        // after it, and it would just cause problems.
        //Still not bug-free though, and should be replaced with a better regex-based
        // solution.
        String[] split = html.split("data-id=\"-1\"");

        ids.addAll(Arrays.asList(split[0].split("data-id=\"")));
        ids.remove(0);
        names.addAll(Arrays.asList(split[0].split("data-name=\"")));
        names.remove(0);


        for (String s : ids){
            id.add(s.split("\"")[0]);
        }
        for (String s : names){
            name.add(s.split("\"")[0]);
        }

        String[][] result = new String[id.size()][2];
        Log.d("HIG.SEARCH.ARRAY", name.toString() + id.toString());

        //Join the two arrays into one 2D array
        for(int i=0; i < id.size(); i++){
            result[i][0] = id.get(i);
            result[i][1] = name.get(i);
        }
        return result;
    }

    private static String[] tagData(String html){
        String[] split = html.replaceAll("(?m:</tr>|^[\\s]*)", "").split("[\\s]*<[^<]+?>");
        List<String> tagdata = new ArrayList<String>();
        //Log.d("TAG DATA \t", Arrays.toString(split));
        for(int i = 4; i < split.length -4; i += 1){
            if(i == 4)tagdata.add(split[i].replace(" ", "\n"));
            if(i == 6 || i == 8 || i == 10 || i == 12) tagdata.add(split[i]);
        }
        String[] tagdata2 = new String[tagdata.size()];
        tagdata2 = tagdata.toArray(tagdata2);
        return tagdata2;
    }

    private static String[][] dimensionalPortal(ArrayList<ArrayList<String>> arraylist){
        final int size = arraylist.size();
        String[][] sarr = new String[size][];
        for(int i = 0; i < size; i++) {
            ArrayList<String> innerlist = arraylist.get(i);
            final int innerSize = innerlist.size();
            sarr[i] = new String[innerSize];
            for(int j = 0; j < innerSize; j++) {
                sarr[i][j] = innerlist.get(j);
            }
        }
        return sarr;
    }

    private static ArrayList<String> dateEntry(String date){
        ArrayList<String> arr = new ArrayList<String>();
        arr.add(date);
        arr.add("");
        arr.add("HIGREADER.newDate");
        arr.add("");
        arr.add("");
        arr.add("");
        return arr;
    }

    private static ArrayList<String> clearEntry(String date, String time){
        ArrayList<String> arr = new ArrayList<String>();
        arr.add(date);
        arr.add(time);
        arr.add("HIGREADER.clear");
        arr.add("");
        arr.add("");
        arr.add("");
        return arr;
    }

    private static String timeString(String start, String end){
        return start.replaceFirst("([0-9]{2})", "$1:") + " - " + end.replaceFirst("([0-9]{2})", "$1:");
    }
}





























