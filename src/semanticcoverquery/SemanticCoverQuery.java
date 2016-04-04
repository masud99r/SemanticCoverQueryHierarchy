/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semanticcoverquery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.didion.jwnl.JWNLException;



/**
 *
 * @author Masud
 */
public class SemanticCoverQuery {
        OntologyAnalysis ontology = new OntologyAnalysis();
        NounePhrase nounPhrase = new NounePhrase();
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, JWNLException {
        // TODO code application logic here
        SemanticCoverQuery sc = new SemanticCoverQuery();
        //OntologyAnalysis ontology = new OntologyAnalysis();
        //NounePhrase nounPhrase = new NounePhrase();
        //String str = "Pierre Vinken, 61 years old, will join the board as a nonexecutive director Nov. 29. Mr. Vinken is chairman of Elsevier N.V., the Dutch publishing group. Rudolph Agnew, 55 years old and former chairman of Consolidated Gold Fields PLC, was named a director of this British industrial conglomerate.";
        //String str= "how to make rice. pagul dance. dog. hello this is another sentences. some water sports in currently icecream.";
      String str = "dog";
      String concept = sc.getConcept(str);
      ArrayList<String> coverquery = sc.getCoverQueries(concept, 1, 3);
      for(int i=0;i<coverquery.size();i++){
          System.out.println("Q "+i+" : "+coverquery.get(i));
      }
       
    }
    public String getConcept(String npQuery){
        String concept="";
        //SemanticCoverQuery sc = new SemanticCoverQuery();
        HashMap<String, Double> semanticUnit = new HashMap<>();
        String str= npQuery;
        System.out.println("AL size: checking ");
        ArrayList<String> al = nounPhrase.getNounPhrase(str);
         System.out.println("AL size: checking end");
        System.out.println("AL size: "+al.size());
        for(int i=0;i<al.size();i++){
            String oneUnit = al.get(i);
            String[] allWordsUnit = nounPhrase.getTokens(oneUnit);
            String activeUnit = al.get(i);
            double icVal=-1;
            for(int p=0;p<allWordsUnit.length;p++){
                //create active unit
                activeUnit="";
                for(int r=p;r<allWordsUnit.length;r++){
                    activeUnit=activeUnit+" "+allWordsUnit[r];
                    
                }
                System.out.println("New unit: "+activeUnit);
                System.out.println("Token size: "+allWordsUnit.length);
                System.out.println("Semantic Unit: "+i+" = "+activeUnit);
                icVal = ontology.calculateIC(activeUnit);
                //check minimizing some value from left
                if(icVal!=16.83616133491964){
                    //default value
                    System.out.println("found value");
                    System.out.println("ic: "+icVal);
                    break;
                }
               
                
            }

            double dvalue =0;
            Double n = semanticUnit.get(activeUnit);
            if(n==null){
                dvalue=0;
            }else{
                dvalue=n;
            }
            if(icVal>dvalue){
                dvalue=icVal;
            }
            if(dvalue==16.83616133491964){
                //default value
                dvalue=-1;
            }
            semanticUnit.put(activeUnit, dvalue);
        }
        System.out.println("Hashmap content");
        for (String name: semanticUnit.keySet()){
          String key =name;
          String value = semanticUnit.get(name).toString();  
          System.out.println(key + " " + value);
        }
        semanticUnit=sortByComparatorDouble(semanticUnit,false);
        System.out.println("Hashmap sorted content");
        for (String name: semanticUnit.keySet()){
          String key =name;
          String value = semanticUnit.get(name).toString();  
          System.out.println(key + " " + value);
        }
        for (String name: semanticUnit.keySet()){
          String key =name;
          //String value = semanticUnit.get(name).toString();  
          //System.out.println(key + " " + value);
          concept=key;
          break;
        }
        System.out.println("MU_SU: "+concept);
        return concept;
    }
    public ArrayList<String> getCoverQueries(String queryConcept,double sem_dist, double k){
         ArrayList<String> coverQ = new ArrayList<>() ;
         ArrayList<String> arl =ontology.getRelatedConcept(queryConcept, sem_dist,k);
         
         //generate k random queries
         if(arl.isEmpty()){
             coverQ.add(queryConcept);//remian the same
         }
         else if(k>=arl.size()){
            for(int i=0;i<arl.size();i++){
                System.out.println("Concept: k>size "+arl.get(i));
                coverQ.add(arl.get(i));
            }
         }
         else{//k<arl.size()
           //pick random k queries
           ArrayList<Integer> indexList = getRandom(arl.size());
             for(int h=0;h<k;h++){
                 System.out.println("K is: "+indexList.get(h));
                 coverQ.add(arl.get(indexList.get(h)));
             }
         }
         
        return coverQ;
    }
    private ArrayList<Integer> getRandom(double uptoNum){
         ArrayList<Integer> list = new ArrayList<>();
        for (int i=0; i<uptoNum; i++) {
            list.add(i);
        }
        Collections.shuffle(list);
        return list;
    }
    private static HashMap<String, Double> sortByComparatorDouble(HashMap<String, Double> unsortMap, final boolean order)
    {

        List<Map.Entry<String, Double>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, (Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) -> {
            if (order)
            {
                return o1.getValue().compareTo(o2.getValue());
            }
            else
            {
                return o2.getValue().compareTo(o1.getValue());
                
            }
        });

        // Maintaining insertion order with the help of LinkedList
        HashMap<String, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }

    
}
