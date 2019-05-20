import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.TwitterException;

public class Anime_Counter {
	private static String consumerKey = System.getenv("TWITTER_CONSUME_API");
	private static String consumerSecretKey = System.getenv("TWITTER_CONSUME_API_SECRET");
	private static String APIKey = System.getenv("TWITTER_ACCESS_TOKEN");
	private static String APISecretKey = System.getenv("TWITTER_ACCESS_TOKEN_SECRET");
	public static void main(String[] args) {
		String tweet = "ナターリアは神";
		Twitter twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(consumerKey, consumerSecretKey);
		twitter.setOAuthAccessToken(new AccessToken(APIKey,APISecretKey));
		try {
			twitter.updateStatus(tweet);
		}catch(TwitterException e){
			System.err.println(e.getMessage());
		}
	}
}
