/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package semanticcoverquery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.Span;
import org.tartarus.snowball.ext.porterStemmer;

/**
 *
 * @author Masud
 */
public class NounePhrase {
      //String modelPath = "C:\\development\\models\\";
      private TokenizerModel tm ;
      private TokenizerME wordBreaker;
      private POSModel pm;
      private POSTaggerME posme;
      private InputStream modelIn;
      private ChunkerModel chunkerModel;
      private ChunkerME chunkerME;
      private InputStream modelIn_sent; 
      private SentenceModel sentenceModel;
      private SentenceDetector _sentenceDetector;
      private HashSet<String> stopwords = new HashSet<>();
      
      public NounePhrase(){
       String modelPath = "C:\\development\\models\\";
          try {
            tm = new TokenizerModel(new FileInputStream(new File(modelPath + "en-token.bin")));
            wordBreaker = new TokenizerME(tm);
            pm = new POSModel(new FileInputStream(new File(modelPath + "en-pos-maxent.bin")));
            posme = new POSTaggerME(pm);
            modelIn = new FileInputStream(modelPath + "en-chunker.bin");
            chunkerModel = new ChunkerModel(modelIn);
            chunkerME = new ChunkerME(chunkerModel);
            modelIn_sent = new FileInputStream(modelPath +"en-sent.bin");
            sentenceModel = new SentenceModel(modelIn_sent);
            _sentenceDetector = new SentenceDetectorME(sentenceModel);
            createStopwordList("data/stopwords.txt");
            
       } catch (IOException ex) {
              Logger.getLogger(NounePhrase.class.getName()).log(Level.SEVERE, null, ex);
          }
      
      }
    public String[] getTokens(String wholeString){
        String[] words = wordBreaker.tokenize(wholeString);
        return words;
    }
    public ArrayList<String> getNounPhrase(String query){
        ArrayList<String> sulist = new ArrayList<>();
        
        String sens[]= _sentenceDetector.sentDetect(query);
          for (String sentence : sens) {
              String[] words = wordBreaker.tokenize(sentence);
              String[] posTags = posme.tag(words);
              Span[] chunks = chunkerME.chunkAsSpans(words, posTags);
              String[] chunkStrings = Span.spansToStrings(chunks, words);
              for (int i = 0; i < chunks.length; i++) {
                  if (chunks[i].getType().equals("NP")) {
                     // System.out.println("NP: \n\t" + chunkStrings[i]);
                      String[] tokenWord = wordBreaker.tokenize(chunkStrings[i]);
                      String ps="";
                      for (String tokenw : tokenWord) {
                          //System.out.print("\n"+tokenWord[p]);
                          if (!checkStopList(tokenw)) {
                              String stemmedToken = PorterStemmingDemo(tokenw);
                           //   String stemmedToken = tokenw;
                              
                              if(stemmedToken.isEmpty()){
                                  continue;//skip white space after stemming
                              }
                              if(ps==null){
                                  ps =ps+stemmedToken;
                              }
                              else{
                                  ps =ps+" "+stemmedToken;
                              } 
                          }
                      }
                      if(ps!=null){
                          if(!ps.isEmpty()){
                             sulist.add(ps); 
                          }
                          
                      }   
                  }
              }
          }
        return sulist;
      }
    public String PorterStemmingDemo(String token) {
            porterStemmer stemmer = new porterStemmer();
            stemmer.setCurrent(token);
            if (stemmer.stem())
                    return stemmer.getCurrent();
            else
                    return token;
    }
    private boolean checkStopList(String word){
        return stopwords.contains(word);
    }
    private void createStopwordList(String filePath){
        FileReader fr;
          try {
              fr = new FileReader(filePath);
          
        BufferedReader br= new BufferedReader(fr);
        String sCurrentLine; 
        while ((sCurrentLine = br.readLine()) != null){
            stopwords.add(sCurrentLine);
            }
        } catch (Exception ex) {
              Logger.getLogger(NounePhrase.class.getName()).log(Level.SEVERE, null, ex);
        }
        //System.out.println("Stopword length: "+stopwords.size());
        
    }
}
