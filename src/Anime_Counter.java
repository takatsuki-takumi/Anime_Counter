import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.TwitterException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.JsonNode;
import java.util.Calendar;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.apache.commons.lang3.time.DateUtils;
import java.util.LinkedHashSet;
import java.util.TimeZone;

public class Anime_Counter {
	private static String consumerKey = System.getenv("TWITTER_CONSUME_API");
	private static String consumerSecretKey = System.getenv("TWITTER_CONSUME_API_SECRET");
	private static String APIKey = System.getenv("TWITTER_ACCESS_TOKEN");
	private static String APISecretKey = System.getenv("TWITTER_ACCESS_TOKEN_SECRET");
	private static String AnnictToken = System.getenv("ANNICT_TOKEN");
	private static String UserName = System.getenv("USER_NAME");
	private static int FOR_NUMBER = 50;
	
	public static void main(String[] args) throws ParseException{
		String message = make_message();
		//tweet("hoge");
	}
	
	public static void  tweet(String message) {
		String tweet = " hoge";
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(consumerKey, consumerSecretKey);
		twitter.setOAuthAccessToken(new AccessToken(APIKey,APISecretKey));
		try {
			twitter.updateStatus(tweet);
		}catch(TwitterException e){
			System.err.println(e.getMessage());
		}
	}
	
	public static Boolean compare_now(Date getdate){
		Date now = new Date();
		Date convert_date1 = DateUtils.truncate(getdate, Calendar.DAY_OF_MONTH);
		Date convert_date2 = DateUtils.truncate(now, Calendar.DAY_OF_MONTH);
		return convert_date1.equals(convert_date2);
	}
	
	public static String make_message() throws ParseException {
		//get info
		String strurl = "https://api.annict.com/v1/activities?fields=work.title,review.created_at,record.created_at&per_page=50&filter_username=" + "temtemtemp" + "&access_token=" + AnnictToken;
		HttpURLConnection con = null;
		Integer watch_counter = 0;
		ArrayList<String> watch_list = new ArrayList<String>();
		String message = "";
		
		try {
			URL url = new URL(strurl);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			con.setDoOutput(true);
			con.getContent();
			
			InputStream inst = con.getInputStream();
			InputStreamReader isr = new InputStreamReader(inst);
			BufferedReader br = new BufferedReader(isr);
			
			String line = br.readLine();
			ObjectMapper obm = new ObjectMapper();
			JsonNode animap = obm.readTree(line);
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			
			for(int num = 0;num < FOR_NUMBER; num++) {
				try {
					if(compare_now(format.parse(animap.get("activities").get(num).get("record").get("created_at").toString().replace("T", " ").replace("Z", "").replace("\"", "")))) {
						watch_counter++;
						watch_list.add(animap.get("activities").get(num).get("work").get("title").toString().replace("\"", ""));
					}else {
						break;
					}
				}catch(NullPointerException e) {
					
				}
			}
			if(watch_list.size() != 0){
				Integer index = 0;
				ArrayList<String> watch_list_hash = new ArrayList<String>(new LinkedHashSet<>(watch_list));
				message = "今日はアニメを" + watch_counter.toString() + "本見ました！\r\r";
				message = message + "見たアニメリスト";
				while(true) {
					try {
						if((138 - message.length()) >= watch_list_hash.get(index).toString().length()){
							message = message + "\r";
							message  = message + watch_list_hash.get(index).toString();
						}else {
							break;
						}
						index++;
					}catch(IndexOutOfBoundsException e) {
						break;
					}
				}
			}else {
				message = "今日のアニメ視聴はお休みです";
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		System.out.println(message);
		return message;
	}
}
