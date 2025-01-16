/* --------------------------Section de Code Utilisateur---------------------*/
package fr.usmb.compilation;
import java_cup.runtime.Symbol;

%%
/* -----------------Section des Declarations et Options----------------------*/
// nom de la class a generer
%class Lexer
%unicode
%line   // numerotation des lignes
%column // numerotation caracteres par ligne

// utilisation avec CUP
// nom de la classe generee par CUP qui contient les symboles terminaux
%cupsym ParserSym
// generation analyser lexical pour CUP
%cup

// code a ajouter dans la classe produite
%{

%}

/* definitions regulieres */
il      =   "Il"|"il"
elle    =   "Elle"|"elle"
pronom  =   {il}|{elle}
verbe   =   "est"|"boit"
adj     =   "vite"|"beau"|"belle"|"bien"|"chaud"
sep     =   \s
point   =   [.?!;]


%%
/* ------------------------Section des Regles Lexicales----------------------*/
/* regles */
{sep}+          { /* pas d'action */ }
{pronom}        { return new Symbol(ParserSym.PRONOM, yyline, yycolumn); }
{verbe}         { return new Symbol(ParserSym.VERBE, yyline, yycolumn); }
{adj}           { return new Symbol(ParserSym.ADJECTIF, yyline, yycolumn); }
{point}         { return new Symbol(ParserSym.POINT, yyline, yycolumn); }
.               { return new Symbol(ParserSym.error, yyline, yycolumn); }
