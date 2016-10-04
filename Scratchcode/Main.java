import java.util.List;
import java.util.Scanner;

public class Main {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		int counter=0;
		String query="stark";//args[0];
		double prec=0;
		double target=0.9;//Double.parseDouble(args[1]);
		List <String> newquery1;
		//start looping
		while(prec<target)
		{
			counter++;
			SearchQuery qr = new SearchQuery(query);
			Resultdoc[] res=qr.resultReturn();
			prec=getFeedback(res);
			if(prec==0)
				break;
			QueryExpansion newQuery = new QueryExpansion(query.split(" "), res);
            newquery1 = newQuery.getNewQuery();
            StringBuilder sb = new StringBuilder();
            for (String s : newquery1)
            {
                sb.append(s);
                sb.append(" ");
            }
            query = sb.toString();
            System.out.println("Updated query: " + query);
			
		}
		
		if(prec>=target)
			System.out.println("Precision of "+ prec+" achieved");
		

	}
	
	public static double getFeedback(Resultdoc[] results) {
        Scanner in = new Scanner(System.in);
        int count = 0;
        for (Resultdoc res : results) {
            System.out.println(res);
            System.out.print("Is this relevant (Y/N)? ");
            String relevance = in.nextLine();
            while (!relevance.equalsIgnoreCase("Y") && !relevance.equalsIgnoreCase("N")) {
                System.out.print("Invalid relevance feedback. Please enter Y or N: ");
                relevance = in.nextLine();
            }
            if (relevance.equalsIgnoreCase("Y")) {		// Relevant
                count = count + 1;
                res.setRelevance(true);
            }
        }
        return (double) count / 10;
    }
	
	

}
