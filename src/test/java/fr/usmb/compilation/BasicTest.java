package fr.usmb.compilation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertLinesMatch;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class BasicTest {
    private PipedOutputStream toIn;
    private ByteArrayOutputStream err;
    private ByteArrayOutputStream out;
    
    
    @BeforeEach
    void init() throws Exception {
        toIn = new PipedOutputStream();
        err = new ByteArrayOutputStream();
        out = new ByteArrayOutputStream();
        System.setIn(new PipedInputStream(toIn));
        System.setOut(new PrintStream(out, true));
        System.setErr(new PrintStream(err, true));
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS) // java11: bugix for https://github.com/junit-team/junit5/issues/1229
    class testValidphrases {
        @ParameterizedTest
        @MethodSource("phraseProvider")
        void testWithAllowedPhrases(String phrase) throws Exception {
            String expectedOutput = "OK\n";
            toIn.write(phrase.getBytes());
            toIn.close();
            
            assertDoesNotThrow(()-> Main.main(new String[0]));
            // use java 11 methods
            // assertLinesMatch(expectedOutput.lines(), out.toString().lines());
            assertLinesMatch(Arrays.asList(expectedOutput.toString().split("\\R")), Arrays.asList(out.toString().split("\\R")));
            assertEquals(0, err.size());
        }
        
        List<String> phraseProvider() {
            LinkedList<String> res = new LinkedList<>();
            for (String sujet: Arrays.asList("il", "elle", "Il", "Elle")) {
                for (String verbe: Arrays.asList("est", "boit")) {
                    for (String complement: Arrays.asList("beau", "belle", "vite", "chaud", "bien")) {
                        for (String point: Arrays.asList(".", "!", "?", ";")) {
                            res.push(sujet+" "+verbe+" "+complement+" "+point);
                        }
                    }
                }
                
            }
            return res;
        }
        
        @Test
        void testWithSeveralValidSentences() throws Exception {
            String expectedOutput = "OK\nOK\nOK\nOK\n";
            String input = "Il est beau ! Elle boit vite;   \n   elle est belle. \tIl est chaud ?  ";
            toIn.write(input.getBytes());
            toIn.close();
            
            assertDoesNotThrow(()-> Main.main(new String[0]));
            // use java 11 methods
            // assertLinesMatch(expectedOutput.lines(), out.toString().lines());
            assertLinesMatch(Arrays.asList(expectedOutput.toString().split("\\R")), Arrays.asList(out.toString().split("\\R")));
            assertEquals(0, err.size());
        }
    }

    @Nested
    class testInvalidPhrase {
        @Test
        void testWithInvaldSubject() throws Exception {
            toIn.write("Papa est beau.".getBytes());
            toIn.close();
            
            assertDoesNotThrow(()-> Main.main(new String[0]));
            assertTrue( out.toString().contains("Erreur de syntaxe"));
        }
    
        @Test
        void testWithInvaldVerb() throws Exception {
            toIn.write("Il mange vite.".getBytes());
            toIn.close();
            
            assertDoesNotThrow(()-> Main.main(new String[0]));
            assertTrue( out.toString().contains("Erreur de syntaxe"));
        }
    
        @Test
        void testWithInvaldComplement() throws Exception {
            toIn.write("Elle est bonne.".getBytes());
            toIn.close();
            
            assertDoesNotThrow(()-> Main.main(new String[0]));
            assertTrue( out.toString().contains("Erreur de syntaxe"));
        }
    
        @Test
        void testWithInvaldPoint() throws Exception {
            toIn.write("Elle est belle :".getBytes());
            toIn.close();
            
            assertThrows(Exception.class, ()-> Main.main(new String[0]));
            assertTrue(out.toString().contains("Erreur de syntaxe"));
        }
    }
    
    @Nested
    class testerrorRecovery {
        @Test
        void testWithOneerror() throws Exception {
            String input = "Il est beau ! Elle mange vite;   \n   elle est belle. \tIl est chaud ?  ";
            toIn.write(input.getBytes());
            toIn.close();
            
            assertDoesNotThrow(()-> Main.main(new String[0]));
            // use java 11 and java 17 methods
            // List<String> result = out.toString().lines().toList();
            List<String> result = Arrays.asList(out.toString().split("\\R"));
           
            assertEquals(result.get(0), "OK");
            assertTrue(result.get(1).contains("Erreur de syntaxe"));
            for(int i = 1; i < result.size()-2; i++) {
                assertFalse(result.get(i).contains("OK"));
            }
            assertEquals(result.get(result.size()-2), "OK");
            assertEquals(result.get(result.size()-1), "OK");
            assertEquals(0, err.size());
        }
    }
}
