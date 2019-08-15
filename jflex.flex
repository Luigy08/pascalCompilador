%%
%unicode
%class PascalFlexer
%column
%line
%int
%public
%standalone


Comentario          =   \{{Caracter}*\}  |  \(\*{Caracter}*\*\) 



//Operadores
OperadorIgual               =	=
OperadorDiferente           =   <>
OperadorMayor               =   >
OperadorMenor               =   <
OperadorMayorIgual          =   >=
OperadorAnd                 =   and
OperadorMenorIgual          =   <=
OperadorNot                 =   not
OperadorOr                  =   or
OperadorResta               =   -
OperadorSuma                =   \+
OperadorMod                 =   mod
OperadorDivision            =   \/
OperadorMultiplicacion      =   \* 
OperadorDivisionSpecial     =   div
//Tipos de datos
Of                          =	of
Array                       =	array
TipoBoolean                 =   boolean
TipoChar                    =	char
Var                         =	var
TipoString                  =	string
TipoInteger                 =	integer

/*Literales*/
LiteralCaracter             =	'[^']'
LiteralEntero               =	{Digito}+
LiteralString               =	'[^']*'
LiteralBoolean              =	true | false
//Funciones
Write                       =	write | writeln
Read                        =   read | readln


//Estructuras de control
Else                        =   else
Then                        =   then
Begin                       =   begin
If                          =   if
To                          =   to
End                         =   end
While                       =   while
Do                          =   do
Until                       =   until
Repeat                      =   repeat
For                         =   for

//Otros

BracketCerrar               =	\]
Coma                        =	,
ComillaDentro               =   \'
Digito                      =	[0-9]
Letra                       =   [a-zA-Z_]
DosPuntosIgual              =	:=
DosPuntos                   =	:
PuntoPunto                  =   \.\.
Punto                       =   \.

Procedure                   =   procedure
Program                     =	program
Identificador               =	{Letra}({Letra}|{Digito})*
Function                    =   function
WhiteSpace                  =	{LineTerminator} | [ \t\f]
PuntoComa                   =	;
ParentesisAbrir             =	\(
LineTerminator              =	\r|\n|\r\n
LlaveAbrir                  =	\{
ParentesisCerrar            =	\)
BracketAbrir                =	\[
LlaveCerrar                 =	\}
ComillaSimple               =   '

%%
<YYINITIAL> {
    {ParentesisAbrir}           {System.out.println("Token ((): " + yytext());}   
    {ParentesisCerrar}          {System.out.println("Token ()): " + yytext());}   
    {Var}                       {System.out.println("Token (var): " + yytext());}   
    {Array}                     {System.out.println("Token (Array): " + yytext());}   
    {Of}                        {System.out.println("Token (Of): " + yytext());}   
    {Begin}                     {System.out.println("Token (Begin): " + yytext());}   
    {End}                       {System.out.println("Token (End): " + yytext());}   
    {Write}                     {System.out.println("Token (Write): " + yytext());}   
    {Read}                      {System.out.println("Token (Read): " + yytext());}   
    {Identificador}             {System.out.println("Token (Identificador): " + yytext());}   
    {WhiteSpace}                {System.out.println("Token (whitespace): " + yytext());}    
    {LlaveAbrir}                {System.out.println("Token ({): " + yytext());}   
    {ComillaSimple}             {System.out.println("Token ('): " + yytext());}   
    {LlaveCerrar}               {System.out.println("Token (}): " + yytext());}   
    {Program}                   {System.out.println("Token (program): " + yytext());}   
    {Procedure}                 {System.out.println("Token (Procedure): " + yytext());}   
    {Function}                  {System.out.println("Token (Function): " + yytext());}   
    {If}                        {System.out.println("Token (If): " + yytext());}   
    {Else}                      {System.out.println("Token (Else): " + yytext());}   
    {Then}                      {System.out.println("Token (Then): " + yytext());}   
    {For}                       {System.out.println("Token (For): " + yytext());}   
    {To}                        {System.out.println("Token (To): " + yytext());}   
    {Do}                        {System.out.println("Token (Do): " + yytext());}   
    {While}                     {System.out.println("Token (While): " + yytext());}   
    {Repeat}                    {System.out.println("Token (Repeat): " + yytext());}   
    {Until}                     {System.out.println("Token (Until): " + yytext());}   
    {Coma}                      {System.out.println("Token (,): " + yytext());}   
    {PuntoPunto}                {System.out.println("Token (..): " + yytext());}   
    {Punto}                     {System.out.println("Token (.): " + yytext());}   
    {PuntoComa}                 {System.out.println("Token (;): " + yytext());}   
    {DosPuntosIgual}            {System.out.println("Token (:=): " + yytext());}   
    {DosPuntos}                 {System.out.println("Token (: ): " + yytext());}   
    {BracketAbrir}              {System.out.println("Token ([): " + yytext());}   
    {BracketCerrar}             {System.out.println("Token (]): " + yytext());}   
    {OperadorDiferente}         {System.out.println("Token (<>): " + yytext());}   
    {OperadorMayorIgual}        {System.out.println("Token (>=): " + yytext());}   
    {OperadorMenorIgual}        {System.out.println("Token (<=): " + yytext());}   
    {OperadorIgual}             {System.out.println("Token (=): " + yytext());}   
    {OperadorMayor}             {System.out.println("Token (>): " + yytext());}   
    {OperadorMenor}             {System.out.println("Token (<): " + yytext());}   
    {OperadorAnd}               {System.out.println("Token (and): " + yytext());}   
    {OperadorOr}                {System.out.println("Token (or): " + yytext());}   
    {OperadorNot}               {System.out.println("Token (not): " + yytext());}   
    {OperadorSuma}              {System.out.println("Token (+): " + yytext());}   
    {OperadorResta}             {System.out.println("Token (-): " + yytext());}   
    {OperadorMultiplicacion}    {System.out.println("Token (*): " + yytext());}   
    {OperadorMod}               {System.out.println("Token (mod): " + yytext());}   
    {OperadorDivision}          {System.out.println("Token (/): " + yytext());}   
    {OperadorDivisionSpecial}   {System.out.println("Token (div): " + yytext());}   
    {TipoChar}                  {System.out.println("Token (char): " + yytext());}   
    {TipoInteger}               {System.out.println("Token (integer ): " + yytext());}   
    {TipoBoolean}               {System.out.println("Token (boolean): " + yytext());}   
    {TipoString}                {System.out.println("Token (string): " + yytext());}   
    {LiteralCaracter}           {System.out.println("Token (literal caracter): " + yytext());}   
    {LiteralEntero}             {System.out.println("Token (literal entero): " + yytext());}   
    {LiteralBoolean}            {System.out.println("Token (literal boolean): " + yytext());}   
    .                           {System.out.println("Error lexico: caracter inesperado: "+yytext()+" linea "+String.valueOf(yyline+1)+", columna "+String.valueOf(yycolumn+1));}
}
