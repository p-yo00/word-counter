import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Collections;
import java.util.Comparator;

class WordKey implements Comparable {
	String word1;
	String word2;

	public WordKey(String word1, String word2) {
		this.word1 = word1;
		this.word2 = word2;
	}
	
	@Override
	public String toString() {
		return word1+", "+word2;
	}

	@Override
	public boolean equals(Object obj) { 
		WordKey temp = (WordKey) obj;
		if ((word1.equals(temp.word1)) && (word2.equals(temp.word2))) return true;
		return false;
	}

	@Override
	public int hashCode() {
		String str=word1+word2;
		return str.hashCode();
	}


	@Override
	public int compareTo(Object o) {
		WordKey temp = (WordKey) o;
		int k=word1.compareTo(temp.word1);
		int k2=word2.compareTo(temp.word2);
		return (k==0)?k2:k;
	}
	
	

}

public class HW3 {
	static Map totalCount = new HashMap<String, Integer>();
	static Map lineCount = new HashMap<WordKey, Integer>();
	static List stopword = new ArrayList<String>();
	
	static void frequencyCount(String sentence) {
		
		Pattern alpha = Pattern.compile("[a-z]");
		sentence=sentence.replace("- +eol+","");
		sentence=sentence.replace("+eol+","");
		sentence=sentence.replace("'s", "");

		String[] word=sentence.split(" ");
		StringBuffer result = new StringBuffer();
		List<String> overlap = new ArrayList<>();
		List<String> overlap2 = new ArrayList<>();
		List overlap3 = new ArrayList<WordKey>();
		
		for(int i=0; i<word.length; i++) {
			result=new StringBuffer();
			Matcher matcher = alpha.matcher(word[i]);
			
			while (matcher.find()) {
			    result.append(matcher.group().trim()); 
			}
			word[i]=result.toString();
		}
		
		for(int i=0; i<word.length-1;i++) {
			if(stopword.contains(word[i])) continue;
			if(word[i].isEmpty()) continue;
			int n=totalCount.containsKey(word[i])?(int)totalCount.get(word[i])+1:1;
			totalCount.put(word[i], n);
			
			if(overlap.contains(word[i])) continue;
			overlap.add(word[i]);
			
			
			for(int j=i+1; j<word.length;j++) {
				if(stopword.contains(word[j])) continue;
				if(word[j].isEmpty()) continue;
				
				if(overlap.contains(word[j])) continue;
				if(overlap2.contains(word[j])) continue;
				
				overlap2.add(word[j]);
				
				WordKey key=orderKey(word[i],word[j]);
				if(overlap3.contains(key)) continue;
				overlap3.add(key);
				
				n=lineCount.containsKey(key)?(int)lineCount.get(key)+1:1;
				lineCount.put(key, n);
			}
			overlap2.clear();
		}
		overlap.clear();
		overlap3.clear();
		
		int n=totalCount.containsKey(word[word.length-1])?(int)totalCount.get(word[word.length-1])+1:1;
		totalCount.put(word[word.length-1], n);
		
		
	}
	
	static WordKey orderKey(String key1, String key2) {
		WordKey wordkey;
		if(key1.compareTo(key2)<0) 
			wordkey = new WordKey(key1,key2);
		else
			wordkey = new WordKey(key2, key1);
		
		return wordkey;
	}
	
	static List orderHash(Map<WordKey, Integer> map) {
		List<Entry<WordKey, Integer>> entries = new ArrayList<>(map.entrySet());
		
		Collections.sort(entries, new Comparator<Entry<WordKey, Integer>>() {
			public int compare(Entry<WordKey, Integer> o1, Entry<WordKey, Integer> o2)
			{
				int order1 = o2.getValue().compareTo(o1.getValue());
				int order2 = o1.getKey().compareTo(o2.getKey());
				return (order1==0)? order2 : order1;
			}
		});
		
		return entries;
	}
	
	static List orderHash2(Map<String, Integer> map) {
		List<Entry<String, Integer>> entries = new ArrayList<>(map.entrySet());
		
		Collections.sort(entries, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2)
			{
				int order1 = o2.getValue().compareTo(o1.getValue());
				int order2 = o1.getKey().compareTo(o2.getKey());
				return (order1==0)? order2 : order1;
			}
		});
		
		return entries;
	}
	
	static void printWord(int k) {
		List totalList = orderHash2(totalCount);
		List lineList = orderHash(lineCount);
		
		System.out.print("Tok-k 문자열: ");
		for(int i=0; i<k; i++) {
			Entry<String, Integer> entry = (Entry<String, Integer>) totalList.get(i);
			System.out.printf("%s(%d) ",entry.getKey(), entry.getValue());
		}
		
		System.out.print("\nTok-k 단어쌍: ");
		for(int i=0; i<k; i++) {
			Entry<WordKey, Integer> entry = (Entry<WordKey, Integer>) lineList.get(i);
			System.out.printf("[%s, %s](%d) ",entry.getKey().word1,entry.getKey().word2,entry.getValue());
		}
	}

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);

		System.out.print("파일 이름, 단어 쌍의 수? ");
		String[] input = scanner.nextLine().split(" ");
		String filename = input[0];
		int k = Integer.parseInt(input[1]);

		try {
			scanner = new Scanner(new FileInputStream("stop.txt"));
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		while (scanner.hasNextLine()) {
			stopword.add(scanner.nextLine());
		}

		try {
			scanner = new Scanner(new FileInputStream(filename));
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		
		StringBuffer sb = new StringBuffer();
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			sb.append(line);
			sb.append("+eol+");
		}
		
		String str=sb.toString();
		
		StringTokenizer st = new StringTokenizer(str, ".|!|\\?");
		
		for (int i = 0; st.hasMoreTokens(); i++) {
			frequencyCount(st.nextToken().trim().toLowerCase());
		}
		
		  List<Entry<WordKey, Integer>> entries = new ArrayList<Entry<WordKey,
		  Integer>>(lineCount.entrySet());
		 
		printWord(k);
		

	}
}
