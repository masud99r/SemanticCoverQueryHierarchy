/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semanticcoverquery;

/**
 *
 * @author Masud
 */

/**
 * Java WordNet Library (JWNL)
 * See the documentation for copyright information.
 *
 * @version 1.1
 */

import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.list.PointerTargetNodeList;
import net.didion.jwnl.data.list.PointerTargetTree;
import net.didion.jwnl.dictionary.Dictionary;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.list.PointerTargetNode;
import net.didion.jwnl.data.list.PointerTargetTreeNodeList;

/** A class to demonstrate the functionality of the JWNL package. */
public class OntologyAnalysis {
        private static final String USAGE = "java Examples <properties file>";

  //      public static void main(String[] args) {
                /*if (args.length != 1) {
                        System.out.println(USAGE);
                        System.exit(-1);
                }*/

                //String propsFile = args[0];
                
      //  }

        private IndexWord ACCOMPLISH;
        private IndexWord DOG;
       // private IndexWord suNP;
        private IndexWord CAT;
        private IndexWord FUNNY;
        private IndexWord DROLL;
        private String MORPH_PHRASE = "running-away";
        
        public OntologyAnalysis() {
         
            String propsFile = "data/properties.xml";
            try {
                // initialize JWNL (this must be done before JWNL can be used)
                JWNL.initialize(new FileInputStream(propsFile));
                //new OntologyAnalysis().go();
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(-1);
            }
        
                
        }

    public void go() throws JWNLException {
         IndexWord suNP =Dictionary.getInstance().getIndexWord(POS.NOUN, "dog");
            //demonstrateMorphologicalAnalysis(MORPH_PHRASE);
            //demonstrateListOperation(ACCOMPLISH);
            //demonstrateTreeOperation(DOG);
            int dist = pathToRoot("dog");
            System.out.println("Distance to root is: "+dist);
            int leaveCount = countLeave("dog");
            System.out.println("Total leaves: "+leaveCount);
            //demonstrateAsymmetricRelationshipOperation(DOG, CAT);
            //demonstrateSymmetricRelationshipOperation(FUNNY, DROLL);
    }
    public double calculateIC(String semanticUnit){
        double icValue;
        double subsumerValue = pathToRoot(semanticUnit);
        //treatment if not found in ontology or not expected value
        if(subsumerValue==-1){
            subsumerValue=0;
        }
        
        double leavesValue = countLeave(semanticUnit);
        if(leavesValue==-1){
            leavesValue=0;
        }
        double maxLeave = 117000.0; //take max leave as all the nodes in wordnet impact on ranking
        double value = ((leavesValue+1)/(subsumerValue+1))/(maxLeave+1);
        icValue = (-1)*(Math.log10(value)/Math.log10(2));
        return icValue;
    }
    private int pathToRoot(String nodeString) {
        IndexWord word;
        int j=0;
        int distj=-1;
        Synset[] sarray;
        try {
            word =Dictionary.getInstance().getIndexWord(POS.NOUN, nodeString);
            sarray = word.getSenses();
            if(sarray.length<1){
                //System.out.println("No synset found for word = "+word.getLemma());
                return -1;
            }
            PointerUtils pu = PointerUtils.getInstance();
            PointerTargetTree syn = pu.getHypernymTree(sarray[0]);
            java.util.List MGListsList  =syn.toList();
            for(Object aMGListsList:MGListsList){
                j=0;
                PointerTargetNodeList MGList = (PointerTargetNodeList) aMGListsList;
                for(Object aMGList : MGList){
                    j++;
                    Synset toAdd = ((PointerTargetNode) aMGList).getSynset();
                }
                if(j>distj){
                    distj = j;
                }
        }
   } catch (Exception ex) {
        //Logger.getLogger(OntologyAnalysis.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return distj;
    }
private int countLeave(String nodeString) {
    IndexWord word;
    int leavesCount=0;
    Synset[] sarray;
   try {
    word =Dictionary.getInstance().getIndexWord(POS.NOUN, nodeString);
    sarray = word.getSenses();
    PointerUtils pu = PointerUtils.getInstance();
    PointerTargetTree syn = pu.getHyponymTree(sarray[0]);
    //syn.print();
    java.util.List MGListsList  =syn.toList();
    for(Object aMGListsList:MGListsList){
      PointerTargetNodeList MGList = (PointerTargetNodeList) aMGListsList;
      for(Object aMGList : MGList){
          Synset toAdd = ((PointerTargetNode) aMGList).getSynset();
          PointerTargetTree syn2 = PointerUtils.getInstance().getHyponymTree(toAdd);
          PointerTargetTreeNodeList pfalg=syn2.getRootNode().getChildTreeList();
          int countChild = pfalg.size();
          if(countChild==0){
              leavesCount++;
          }
      }
    }
    } catch (Exception ex) {
       // Logger.getLogger(OntologyAnalysis.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
        return leavesCount;
    }
public ArrayList<String> getRelatedConcept(String nodeString,double sim_dist,double k) {
    ArrayList<String> concept = new ArrayList();
    IndexWord word;
    Synset[] sarray;
    try{
        word =Dictionary.getInstance().getIndexWord(POS.NOUN, nodeString);
        sarray = word.getSenses();
    }catch(Exception ne){
        //ne.printStackTrace();
        concept.add(nodeString);
        return concept;
    }       
    HashMap<String, Integer> listConcept;
    listConcept = downwardConcept(nodeString, sim_dist);
   
   double topCount = k*5;//random 5, for each cover query 
   double tcount = 0;
    for(String key:listConcept.keySet()) {
        concept.add(key);
        tcount++;
        if(tcount>=topCount){
            break;
        } 
    }
    listConcept = upwardConcept(nodeString, sim_dist);
    tcount = 0;
    for(String key:listConcept.keySet()) {
        concept.add(key);
        tcount++;
        if(tcount>=topCount){
            break;
        } 
    }
    for(int i=0;i<concept.size();i++){
        System.out.println(i+" : "+concept.get(i));
    }
    return concept;
}

private HashMap<String, Integer> downwardConcept(String nodeString, double sim_dist){
    HashMap<String, Integer> downConcepts = new HashMap<>();
    IndexWord word;
    Synset[] sarray;
   try {
    word =Dictionary.getInstance().getIndexWord(POS.NOUN, nodeString);
    sarray = word.getSenses();
    PointerUtils pu = PointerUtils.getInstance();
    PointerTargetTree syn = pu.getHyponymTree(sarray[0]);//lower tree specialization
   // PointerTargetTree syn = pu.;//lower tree specialization
    
//syn.
  //  syn.print();
    PointerTargetTree syn1=syn;
    java.util.List MGListsList  =syn.toList();
    for(Object aMGListsList:MGListsList){
      PointerTargetNodeList MGList = (PointerTargetNodeList) aMGListsList;
      int cCount=0;
      for(Object aMGList : MGList){
          Synset toAdd = ((PointerTargetNode) aMGList).getSynset();
          if(cCount==sim_dist){
            // System.out.println("Child: "+toAdd);
             String synword = toAdd.getWord(0).getLemma();
           //  System.out.println("Lemma: "+synword);
             Integer val = downConcepts.get(synword);
             if(val==null){
                 val=1;
             }
             else{
                 val +=1;
             }
             downConcepts.put(synword, val);
          }
          cCount++;
      }
      //System.out.println("\n\t New Chunk Child: ");
    }
 
     //  System.out.println("Down size Concept size: "+downConcepts.size());
  

    } catch (Exception ex) {
        //Logger.getLogger(OntologyAnalysis.class.getName()).log(Level.SEVERE, null, ex);
           // return -1;
            downConcepts.put(nodeString, 1);
          return sortByComparator(downConcepts,false);
        }
        return sortByComparator(downConcepts,false);
}
private HashMap<String, Integer> upwardConcept(String nodeString, double sim_dist){
    HashMap<String, Integer> upConcepts = new HashMap<>();
    IndexWord word;
    Synset[] sarray;
   try {
    word =Dictionary.getInstance().getIndexWord(POS.NOUN, nodeString);
    sarray = word.getSenses();
    PointerUtils pu = PointerUtils.getInstance();
       System.out.println("Sallry: "+sarray[0]);
    PointerTargetTree syn = pu.getHypernymTree(sarray[0]);//upper portion parent tree generalization
    //syn.
    syn.print();
   
    java.util.List MGListsList  =syn.toList();
    for(Object aMGListsList:MGListsList){
      PointerTargetNodeList MGList = (PointerTargetNodeList) aMGListsList;
      int cCount=0;
      for(Object aMGList : MGList){
          Synset toAdd = ((PointerTargetNode) aMGList).getSynset();
          if(cCount==sim_dist){
             //System.out.println("Child: "+toAdd);
             String synword = toAdd.getWord(0).getLemma();
            // System.out.println("Lemma: "+synword);
             Integer val = upConcepts.get(synword);
             if(val==null){
                 val=1;
             }
             else{
                 val +=1;
             }
             upConcepts.put(synword, val);
          }
          cCount++;
      }
      //System.out.println("\n\t New Chunk Child: ");
    }
 
       //System.out.println("Up side Concept size: "+upConcepts.size());
  

    } catch (Exception ex) {
        //Logger.getLogger(OntologyAnalysis.class.getName()).log(Level.SEVERE, null, ex);
           // return -1;
           upConcepts.put(nodeString, 1);
          return sortByComparator(upConcepts,false);
        }
        return sortByComparator(upConcepts,false);
}
private static HashMap<String, Integer> sortByComparator(HashMap<String, Integer> unsortMap, final boolean order)
    {

        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, (Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) -> {
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
        HashMap<String, Integer> sortedMap = new LinkedHashMap<>();
        list.stream().forEach((entry) -> {
            sortedMap.put(entry.getKey(), entry.getValue());
            });

        return sortedMap;
    }


}
