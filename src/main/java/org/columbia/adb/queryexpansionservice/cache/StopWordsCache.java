package org.columbia.adb.queryexpansionservice.cache;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Component;

/***
 * Initializes stop words cache
 * @author gauravmishra
 *
 */
@Component
public class StopWordsCache {

    private static final Set<String> STOP_WORDS_SET = new HashSet<>();

    @PostConstruct
    public void init() throws FileNotFoundException, IOException {

        String fileName = "src/main/resources/stopwords.txt";
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                STOP_WORDS_SET.add(line.toLowerCase());
            }
        }
    }

    /***
     * Returns if the passed word is a stop word or not
     * @param word
     * @return
     */
    public boolean isStopWord(String word) {
        Validate.notEmpty(word, "Word is blank");
        return STOP_WORDS_SET.contains(word.toLowerCase());
    }

}
