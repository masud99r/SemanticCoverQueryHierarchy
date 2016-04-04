
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
import net.didion.jwnl.data.Synset;

import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.dictionary.Dictionary;

import java.io.FileInputStream;
/** A class to demonstrate the functionality of the JWNL package. */
public class Examples {
        private static final String USAGE = "java Examples <properties file>";

       public static void main(String[] args) throws JWNLException {
                Examples ex = new Examples();
                ex.go();
        }
        private IndexWord DOG;

        
        public Examples() {
            String propsFile = "data/properties.xml";
            try {
                // initialize JWNL (this must be done before JWNL can be used)
                JWNL.initialize(new FileInputStream(propsFile));
            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(-1);
            }   
        }

    public void go() throws JWNLException {
         IndexWord suNP =Dictionary.getInstance().getIndexWord(POS.ADJECTIVE, "funny");
         /*
         IndexWord suNP =Dictionary.getInstance().getIndexWord(POS.NOUN, "funny"); 
         will give you noun synonym
         similarly you can change any POS. change word to get syn for different word
         */
         Synset[] syn = suNP.getSenses();
         for(int i=0;i<syn.length;i++){
             System.out.println("Synom: "+syn[i].toString());
         }
         
    }
   



}
