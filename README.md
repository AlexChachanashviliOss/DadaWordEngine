DaDa Word Engine
===

Minimum JDK: 11


What is it?
---
Generic word-aware parsing, construction and processing tool for generating sentences based on word types. You can generate random sentences based on defined structure by parsing or constructing a sentence based on word types and generating
random output using the parsed structure.

Phonemix transformer is included to convert a word to the way it sounds (based on various algorithms) to allow better search term matching.


Phonemix algorithm
---

* Original compacting algorithm is very loosely based on ideas in Russell Soundex (1918)
* Compacting algorithm designed in 1993 by Alex Chachanashvili for Dada Wallpaper Poem BBS art project (updated in 2004)
* Aggressive algorithm designed in 2017 by Alex Chachanashvili for Dada poem generator and Discord bot, wider search results
* Enhanced algorithm designed in 2021 by Alex Chachanashvili for better rhyming and more accurate search results

Where is it?
---
**Source maintained at:** https://github.com/AlexChachanashviliOss/DadaWordEngine

**Maven Repository:** https://mvnrepository.com/artifact/io.github.achacha/DadaWordEngine

Releases at Maven Repository:

    <groupId>io.github.achacha</groupId>
    <artifactId>DadaWordEngine</artifactId>
    <version>[latest version]</version>


Features
---

- Support for multiple word sets (WordData)
- Parsing will detect word and its possible type (Sentence)
- Word hyphenation (HyphenData)
- Transformation to phonetic form useful for search and match based on what it sounds like
- Integrated as custom JSP tags for easier dynamic content generation

Examples
---
See /src/main/java/org/achacha/dada/examples

Display phonetic representation forward and reverse:

    mvn compile exec:java -Dexec.mainClass="io.github.achacha.dada.examples.PhonemixExample" 

Parse sentence, try to determine parts and replace with other random words of same type:

    mvn compile exec:java -Dexec.mainClass="io.github.achacha.dada.examples.ParseAndRegenerateSentenceExample" 

Reads word/sentence and tries to find a rhyme:

    mvn compile exec:java -Dexec.mainClass="io.github.achacha.dada.examples.FindWordsThatRhymeExample" 
    mvn compile exec:java -Dexec.mainClass="io.github.achacha.dada.examples.RhymeWithSentenceExample" 

JSP integration
---

1. Copy TLD file from {this jar}/WEB-INF/tlds to {your webapp resources}/WEB-INF/tlds
2. Initialize TagSingleton with WordData and HypenData during container/context initialization
   - TagSingleton.setWordData(new WordData("resource:/data/extended2018"));
   - TagSingleton.setHypenData(new HyphenData("resource:data/hyphen"));
3. Add tags to your JSP page (see provided examples in {this jar}/WEB-INF/*.jsp)

Word Data
---

1. All data is converted to lower case
2. Leading # is used as a comment
3. Whitespace is trimmed front and back
