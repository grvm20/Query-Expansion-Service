import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.bind.DatatypeConverter;


public class ExtractSample {
	String accountKey ="53P3c4mc5Y4VeQJOy4bA+I2+/a84DbSf/lqWXgZVkmk";
	static HashMap<String,ArrayList<String>> qset= new HashMap<String,ArrayList<String>>();
	
	public static void create_query_set(String category){
				
		String fileName = "Resources/" + category + ".txt";
		System.out.println("Generating query set for:"+category);
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			String line;
			while ((line = br.readLine()) != null) {
				String [] s= line.split(" ");
				ArrayList<String> temp=new ArrayList<String>();
				if(qset.containsKey(s[0])){
					temp=qset.get(s[0]);
					temp.add(line.substring(line.indexOf(" "), line.length()));
									}
				else{
					
					temp.add(line.substring(line.indexOf(" "), line.length()));
				}
				
				qset.put(s[0], temp);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Query set for "+category+" done");
	}
	public String[] get_query_list(String cat,String [] categories){
		cat=cat.toLowerCase();
		System.out.println("Getting query for category:"+cat);
		String[] arr;
		if(cat.equals("root")){
			String[] arr2={"Computers","Health","Sports"};
			arr=arr2;
		}
		else if (cat.equals("health")){
			String[] arr2={"Fitness","Diseases"};
			arr=arr2;
		}
		else if (cat.equals("sports")){
			String[] arr2={"Soccer","Basketball"};
			arr=arr2;
			System.out.println("Check2"+arr.length);
		}
		else if (cat.equals("computers")){
			String[] arr2={"Hardware","Programming"};
			arr=arr2;
		}
		else{
			arr=null;
			System.out.println("Null");
		}
		int totlength=0;
		int c=0;
		System.out.println("Check1"+arr.length);
		for (String s1:arr){
			ArrayList<String> temp=qset.get(s1);
			totlength+=temp.size();
		}
		String [] queries=new String[totlength];
		for(String s1 : arr){
			ArrayList<String> temp=qset.get(s1);
			
			for (int i=0;i<temp.size();i++){
				queries[c++]=temp.get(i);
			}
		}
		
		return queries;
	}
	public String searchBingResult(String bingUrl) throws IOException{
		
		//String accountKeyEnc = Base64.getEncoder().encodeToString((accountKey + ":" + accountKey).getBytes());
		String accountKeyEnc = DatatypeConverter.printBase64Binary((accountKey + ":" + accountKey).getBytes());

		URL url = new URL(bingUrl);
		URLConnection urlConnection = url.openConnection();
		urlConnection.setRequestProperty("Authorization", "Basic " + accountKeyEnc);

		InputStream inputStream = (InputStream) urlConnection.getContent();
		byte[] contentRaw = new byte[urlConnection.getContentLength()];
		inputStream.read(contentRaw);
		String content = new String(contentRaw);

		//The content string is the xml/json output from Bing.
		return content;
	}
	public ArrayList<String> getTop4URL(String database,String query){
		ArrayList<String> result = new ArrayList<String>();
		String bingQuery = "https://api.datamarket.azure.com/Data.ashx/Bing/SearchWeb/v1/Web?Query=%27site%3a"
							+ database + "%20" + query.replace(" ", "+") + "%27&$top=4&$format=Atom";
		try {
			String content = searchBingResult(bingQuery);
			Pattern p = Pattern.compile("<d:Url m:type=\"Edm.String\">(.+?)</d:Url>");
			Matcher m = p.matcher(content);
			while (m.find())
				result.add(m.group(1));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void create_output_file(String cat,String db_name,Map<String,Integer> map){
		String filename=cat+"-"+db_name+".txt";
		String[] keys=map.keySet().toArray(new String[map.keySet().size()]);
		Arrays.sort(keys);
		Writer writer = null;

		try {
		    writer = new BufferedWriter(new OutputStreamWriter(
		          new FileOutputStream(filename), "utf-8"));
		    for(String s : keys)
		    {		    	
		    	String new1=s+"#"+map.get(s).toString()+"#\n";
		    	writer.write(new1);
		    }
		    
		} catch (IOException ex) {
		  ex.printStackTrace();
		} finally {
			System.out.println("File write complete");		
		   try {writer.close();} catch (Exception ex) {/*ignore*/}
		}
		
	}
	
	public static ArrayList<String> runLynx(String url) {

        int buffersize = 40000;
        StringBuffer buffer = new StringBuffer(buffersize);

        try {
        	
            String cmdline[] = {"/usr/bin/lynx", "--dump", url };
            Process p = Runtime.getRuntime().exec(cmdline);
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            char[] cbuf = new char[1];

            while (stdInput.read(cbuf, 0, 1) != -1 || stdError.read(cbuf, 0, 1) != -1) {
                buffer.append(cbuf);
            }
            p.waitFor();
            stdInput.close();
            stdError.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        // Remove the References at the end of the dump
        int end = buffer.indexOf("\nReferences\n");

        if (end == -1) {
            end = buffer.length();
        }
        // Remove everything inside [   ] and do not write more than two consecutive spaces
        boolean recording = true;
        boolean wrotespace = false;
        StringBuffer output = new StringBuffer(end);

        for (int i = 0; i < end; i++) {
            if (recording) {
                if (buffer.charAt(i) == '[') {
                    recording = false;
                    if (!wrotespace) {
                        output.append(' ');
                        wrotespace = true;
                    }
                    continue;
                } else {
                    if (Character.isLetter(buffer.charAt(i)) && buffer.charAt(i)<128) {
                        output.append(Character.toLowerCase(buffer.charAt(i)));
                        wrotespace = false;
                    } else {
                        if (!wrotespace) {
                            output.append(' ');
                            wrotespace = true;
                        }
                    }
                }
            } else {
                if (buffer.charAt(i) == ']') {
                    recording = true;
                    continue;
                }
            }
        }
        ArrayList<String> document = new ArrayList<String>();
        StringTokenizer st = new StringTokenizer(output.toString());

        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            //System.out.println(tok);
            document.add(tok);
        }
        return document;
    }

	public void create_summary(String [] categories,String db_name){
		System.out.println("Extracting topic content summaries");
		Map<String, Integer> map = new HashMap<String, Integer>();
		int l=categories.length;
		for (String cat : categories){
			
			if(l==3 && cat==categories[0]){
				continue ;
			}
			ArrayList<String> duplicates=new ArrayList<String>();
			
			String [] cat_query=get_query_list(cat,categories);
			int query_length=cat_query.length;
			for (int i=0;i<query_length;i++){
				ArrayList<String> top4=getTop4URL(db_name,cat_query[i]);
				for(String url:top4){
					if(!duplicates.contains(url)){
						duplicates.add(url);
						//check encoding of url here
						ArrayList<String> setOfWords=runLynx(url);
						ArrayList<String> newdocSet=new ArrayList<>();
						if(!setOfWords.isEmpty()){
							for (String s : setOfWords){
								if(!newdocSet.contains(s)){
									newdocSet.add(s);
									if(map.containsKey(s))
										map.put(s, map.get(s)+1);
																	
									else 
										map.put(s, 1);
								}
							}
							
						}
						
					}
					
				}
				System.out.println("Completed Test: db:"+db_name+" category:"+cat+" query:"+cat_query[i]);
				System.out.println("URLs: " + top4);
				
			}
			System.out.println("Completed Test"+db_name+" for "+cat);
			create_output_file(cat,db_name,map);
		}
		
	}
	
	public String[] processCat(String classified){
		String[] res=classified.split("/");
		String[] res2=new String[res.length];
		for(int i=0;i<res.length;i++)
		{
			res2[res.length-(i+1)]=res[i];
		}
		return res2;
	}
	
	public static void main(String args[]){
		
		String cat = "Root/Sports/Basketball";
		String db= "yahoo.com";
		
		String[] categoryArray1={"root", "computers", "health" , "sports"};
		for(String s : categoryArray1){
			create_query_set(s);
		}
		/*
		String[] categoryArray= {"Root", "Computers", "Health" , "Sports", "Hardware", "Programming" , "Fitness", "Diseases", "Basketball", "Soccer"};
		for(String s : categoryArray1){
			create_query_set(s);
		}
		//For test
		for (String s : qset.keySet()){
			System.out.println(s+" : "+qset.get(s));
		}*/
		ExtractSample p = new ExtractSample();
		p.create_summary(p.processCat(cat),db);
		//System.out.println("Function check : " +p.processCat(cat)[2]);
		/*Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("Basket", 3);
		map.put("Zone", 4);
		map.put("Jordan", 7);
		map.put("Michael", 8);*/
		
		//p.create_output_file("Basketball",db,map);
		
	}
}
