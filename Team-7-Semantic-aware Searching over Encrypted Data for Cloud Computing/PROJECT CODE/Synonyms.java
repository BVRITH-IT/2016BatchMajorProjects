package ct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Synonyms {

    private final String USER_AGENT = "Mozilla/5.0";

    public static void main(String[] args) throws Exception {
        Synonyms http = new Synonyms();

        Scanner sc = new Scanner(System.in);

        System.out.println("Write a word...");
        String wordToSearch = sc.next();

        http.searchSynonym(wordToSearch);
    }



    public ArrayList searchSynonym(String wordToSearch) throws Exception {
        System.out.println("Sending request...");
		ArrayList al=new ArrayList();
		al.clear();

        String url = "https://api.datamuse.com/words?rel_syn=" + wordToSearch;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending request to: " + url);
        System.out.println("JSON Response: " + responseCode + "\n");

        // ordering the response
        StringBuilder response;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            String inputLine;
            response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            // converting JSON array to ArrayList of words
            ArrayList<Word> words = mapper.readValue(
                response.toString(),
                mapper.getTypeFactory().constructCollectionType(ArrayList.class, Word.class)
            );

            System.out.println("Synonym word of '" + wordToSearch + "':");
            if(words.size() > 0) {
                for(Word word : words) {
                    System.out.println((words.indexOf(word) + 1) + ". " + word.getWord() + "");
					al.add(word.getWord());
                }
            }
            else {
               System.out.println("none synonym word!");
            }
        }
        catch (IOException e) {
            e.getMessage();
        }
		return al;
    }

    // word and score attributes are from DataMuse API
    static class Word {
        private String word;
        private int score;

        public String getWord() {return this.word;}
        public int getScore() {return this.score;}
    }
}