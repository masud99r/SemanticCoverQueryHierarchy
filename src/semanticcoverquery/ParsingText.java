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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import opennlp.tools.chunker.ChunkerME;
import opennlp.tools.chunker.ChunkerModel;
import opennlp.tools.cmdline.PerformanceMonitor;

import opennlp.tools.cmdline.parser.ParserTool;
import opennlp.tools.cmdline.postag.POSModelLoader;
import opennlp.tools.parser.Parse;
import opennlp.tools.parser.Parser;
import opennlp.tools.parser.ParserFactory;
import opennlp.tools.parser.ParserModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;

//extract noun phrases from a single sentence using OpenNLP
public class ParsingText {

	//static String sentence = "Who is the author of The Call of the Wild?";
	static String sentence = "Programcreek is a very huge and useful website";
	//public static List<Parse> nounPhrases;

	//static Set<String> nounPhrases = new HashSet<>();
	
	public static void main(String[] args) {

		InputStream modelInParse = null;
		try {
                    chunk_np();
                        chunk();
			//load chunking model
			modelInParse = new FileInputStream("data/models/en-parser-chunking.bin"); //from http://opennlp.sourceforge.net/models-1.5/
			ParserModel model = new ParserModel(modelInParse);
			
			//create parse tree
			Parser parser = ParserFactory.create(model);
			Parse topParses[] = ParserTool.parseLine(sentence, parser, 1);
			
			//call subroutine to extract noun phrases
                        System.out.println("Parsing satrt");
			for (Parse p : topParses)
                        {   
                           p.show();
                            System.out.println("CT: "+ p.getText());
				getNounPhrases(p);
                        }
                        //getNounPhrases(p);
                        System.out.println("Parsing end");
			
			
			//print noun phrases
			//for (Parse s : nounPhrases)
			 //   System.out.println(s);
			
			//The Call
			//the Wild?
			//The Call of the Wild? //punctuation remains on the end of sentence
			//the author of The Call of the Wild?
			//the author
		}
		catch (IOException e) {
		  e.printStackTrace();
		}
		finally {
		  if (modelInParse != null) {
		    try {
		    	modelInParse.close();
		    }
		    catch (IOException e) {
		    }
		  }
		}
	}
	
	//recursively loop through tree, extracting noun phrases
        public static void getNounPhrases(Parse p) {
            if (p.getType().equals("NP")) {
                // nounPhrases.add(p);
                p.show();
            }
            for (Parse child : p.getChildren()) {
                 getNounPhrases(child);
            }
        }

    public static void chunk() throws IOException {
	POSModel model = new POSModelLoader().load(new File("data/models/en-pos-maxent.bin"));
	PerformanceMonitor perfMon = new PerformanceMonitor(System.err, "sent");
	POSTaggerME tagger = new POSTaggerME(model);
 
	String input = "Hi. How are you? This is Mike.";
	ObjectStream<String> lineStream = new PlainTextByLineStream(new StringReader(input));
 
	perfMon.start();
	String line;
	String whitespaceTokenizerLine[] = null;
 
	String[] tags = null;
	while ((line = lineStream.read()) != null) {
		whitespaceTokenizerLine = WhitespaceTokenizer.INSTANCE.tokenize(line);
		tags = tagger.tag(whitespaceTokenizerLine);
 
		POSSample sample = new POSSample(whitespaceTokenizerLine, tags);
		System.out.println(sample.toString());
			perfMon.incrementCounter();
	}
	perfMon.stopAndPrintFinalResult();
 
	// chunker
	InputStream is = new FileInputStream("data/models/en-chunker.bin");
	ChunkerModel cModel = new ChunkerModel(is);
 
	ChunkerME chunkerME = new ChunkerME(cModel);
	String result[] = chunkerME.chunk(whitespaceTokenizerLine, tags);
 
	for (String s : result)
		System.out.println("e33e3e3"+s);
 
	String[] span = chunkerME.chunk(whitespaceTokenizerLine, tags);
	for (String s : span)
		System.out.println(s.toString());
}
    public static void chunk_np() {
    InputStream modelIn = null;
    ChunkerModel model = null;

    try {
      modelIn = new FileInputStream("data/models/en-chunker.bin");
      model = new ChunkerModel(modelIn);
    }
    catch (IOException e) {
      // Model loading failed, handle the error
      e.printStackTrace();
    }
    finally {
      if (modelIn != null) {
        try {
          modelIn.close();
        }
        catch (IOException e) {
        }
      }
    }

//After the model is loaded a Chunker can be instantiated.


    ChunkerME chunker = new ChunkerME(model);



    String sent[] = new String[]{"Rockwell", "International", "Corp.", "'s",
      "Tulsa", "unit", "said", "it", "signed", "a", "tentative", "agreement",
      "extending", "its", "contract", "with", "Boeing", "Co.", "to",
      "provide", "structural", "parts", "for", "Boeing", "'s", "747",
      "jetliners", "."};

    String pos[] = new String[]{"NNP", "NNP", "NNP", "POS", "NNP", "NN",
      "VBD", "PRP", "VBD", "DT", "JJ", "NN", "VBG", "PRP$", "NN", "IN",
      "NNP", "NNP", "TO", "VB", "JJ", "NNS", "IN", "NNP", "POS", "CD", "NNS",
      "."};

    String tag[] = chunker.chunk(sent, pos);
    
    for(int k=0;k<tag.length;k++){
        System.out.println("Tag: "+tag[k]);
    }
  }
}

