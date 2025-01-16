package fr.usmb.compilation;

import java.io.FileReader;
import java.io.InputStreamReader;

public class Main {
    public static void main(String[] args) throws Exception {
        Lexer lexer;
        if (args.length > 0)
            lexer = new Lexer(new FileReader(args[0]));
        else
            lexer = new Lexer(new InputStreamReader(System.in));
        @SuppressWarnings("deprecation")
        Parser p = new Parser(lexer);
        p.parse();
    }
}
