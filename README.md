LOSM: Linked Open Street Map 
============================
A middleware to query OSM via SPARQL queries
---------------

Geographical data is gaining momentum in scientific and industrial communities thanks to the high level and quality of information and knowledge it encodes. The most recent representation of the Linked Open Data cloud shows GeoNames competing with DBpedia as the largest and most linked dataset available in the Web. In the “normal” Web, Open Street Map (OSM) has reached, in the last years, a maturity stage thus allowing the users to exploit its data for a daily use. We developed LOSM (Linked Open Street Map), a SPARQL end-point able to query the data available in OSM by an on-line translation form SPARQL syntax to a sequence of calls to the overpass turbo API. The end-point comes together with a Web interface useful to guide the user during the formulation of a query. 

A live demo is available at http://sisinflab.poliba.it/semanticweb/lod/losm/

The subset of the Sparql 1.1 grammar used to develop this demo is available at https://github.com/sisinflab/losm/blob/master/WebContent/losm_grammar.html
The subset of the Sparql 1.1 grammar used to develop the PEG parser is available at https://github.com/sisinflab/losm/blob/master/WebContent/losm_peg_grammar.html
The lexer and the parser were built using java CUP tool, for details you can see the .cup and .lex sources at https://github.com/sisinflab/losm/tree/master/src/it/poliba/sisinflab/semanticweb/lod/losm/sparl

If you consider LOSM an interesting work please cite us, and if you are interested in knowing more about LOSM you can take a look at the paper [Exposing Open Street Map in the Linked Data Cloud](http://link.springer.com/chapter/10.1007/978-3-319-42007-3_29)

## Reference
If you publish research that uses LOSM please use the following two works:
~~~
@inproceedings{DBLP:conf/ieaaie/AnelliCNPR16,
  author    = {Vito Walter Anelli and
               Andrea Cal{\`{\i}} and
               Tommaso Di Noia and
               Matteo Palmonari and
               Azzurra Ragone},
  title     = {Exposing Open Street Map in the Linked Data Cloud},
  booktitle = {Trends in Applied Knowledge-Based Systems and Data Science - 29th
               International Conference on Industrial Engineering and Other Applications
               of Applied Intelligent Systems, {IEA/AIE} 2016, Morioka, Japan, August
               2-4, 2016, Proceedings},
  pages     = {344--355},
  year      = {2016},
  crossref  = {DBLP:conf/ieaaie/2016},
  url       = {https://doi.org/10.1007/978-3-319-42007-3\_29},
  doi       = {10.1007/978-3-319-42007-3\_29},
  timestamp = {Wed, 14 Nov 2018 10:56:21 +0100},
  biburl    = {https://dblp.org/rec/bib/conf/ieaaie/AnelliCNPR16},
  bibsource = {dblp computer science bibliography, https://dblp.org}
}
~~~
The full paper describing the overall approach is available here [PDF](http://link.springer.com/chapter/10.1007/978-3-319-42007-3_29)

~~~
@inproceedings{Anelli2015LOSM,
  author    = {Anelli, Vito Walter and Di Noia, Tommaso and Galeone, Pasquale and Nocera, Francesco and Rosati, Jessica and Tomeo, Paolo and Di Sciascio, Eugenio},
  title     = {{LOSM:} a {SPARQL} Endpoint to Query Open Street Map},
  booktitle = {Proceedings of the {ISWC} 2015 Posters {\&} Demonstrations Track
               co-located with the 14th International Semantic Web Conference (ISWC-2015),
               Bethlehem, PA, USA, October 11, 2015.},
  year      = {2015},
  url       = {http://ceur-ws.org/Vol-1486/paper\_126.pdf}
}
~~~
The full paper describing the overall approach is available here [PDF](https://github.com/vitowalteranelli/losm/blob/master/Anelli2015LOSM.pdf)

## Credits
This algorithm has been developed by Vito Walter Anelli and Pasquale Galeone while working at [SisInf Lab](http://sisinflab.poliba.it) under the supervision of Tommaso Di Noia.  

## Contacts

   Tommaso Di Noia, tommaso [dot] dinoia [at] poliba [dot] it  
   
   Vito Walter Anelli, vitowalter [dot] anelli [at] poliba [dot] it 
   
   Pasquale Galeone, p [dot] galeone [at] studenti [dot] poliba [dot] it  
