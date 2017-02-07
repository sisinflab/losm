LOSM Linked Open Street Map 
============================
A middleware to query OSM via SPARQL queries
---------------

Geographical data is gaining momentum in scientific and industrial communities thanks to the high level and quality of information and knowledge it encodes. The most recent representation of the Linked Open Data cloud shows GeoNames competing with DBpedia as the largest and most linked dataset available in the Web. In the “normal” Web, Open Street Map (OSM) has reached, in the last years, a maturity stage thus allowing the users to exploit its data for a daily use. We developed LOSM (Linked Open Street Map), a SPARQL end-point able to query the data available in OSM by an on-line translation form SPARQL syntax to a sequence of calls to the overpass turbo API. The end-point comes together with a Web interface useful to guide the user during the formulation of a query. 

A live demo is available at http://sisinflab.poliba.it/semanticweb/lod/losm/

The subset of the Sparql 1.1 grammar used to develop this demo is available at https://github.com/sisinflab/losm/blob/master/WebContent/losm_grammar.html
The subset of the Sparql 1.1 grammar used to develop the PEG parser is available at https://github.com/sisinflab/losm/blob/master/WebContent/losm_peg_grammar.html
The lexer and the parser were built using java CUP tool, for details you can see the .cup and .lex sources at https://github.com/sisinflab/losm/tree/master/src/it/poliba/sisinflab/semanticweb/lod/losm/sparl

If you consider LOSM an interesting work please cite us, and if you are interested in knowing more about LOSM and you can have a look at this paper:

> @inproceedings{losm-ieaaie,    
>  author    = {Vito Walter Anelli and Andrea Cal{\`{\i}} and Tommaso {Di Noia} and Matteo Palmonari and Azzurra Ragone}, 
>  title     = {Exposing Open Street Map in the Linked Data Cloud},  
>  booktitle = {Trends in Applied Knowledge-Based Systems and Data Science - 29th International Conference on Industrial Engineering and Other Applications of Applied Intelligent Systems, {IEA/AIE} 2016, Morioka, Japan, August 2-4, 2016, Proceedings},    
>  pages     = {344--355},    
>  year      = {2016},  
>  doi       = {10.1007/978-3-319-42007-3_29},  
> } 

Contacts
-------

   Tommaso Di Noia, tommaso [dot] dinoia [at] poliba [dot] it  
   
   Vito Walter Anelli, vitowalter [dot] anelli [at] poliba [dot] it 
   
   Pasquale Galeone, p [dot] galeone [at] studenti [dot] poliba [dot] it  
