# DNA analyzer

## This is a fork of https://bitbucket.org/kolomaznik/genetika/src/master/ for keeping up with some newer versions! I am just a contributor.

## Online version

Online version of the software is available at http://bioinformatics.ibp.cz

## Local installation

Run `genetika.bat` or `genetika.sh` through command line. You should be able to access
the user interface via your web browser on http://localhost:8080.

You can tweak some settings in `config.properties` file:

- `server.port = 8080` - set local port
- `palindrome.analyse.limit = 5000` - set limit for palindrome detection

Current version of Java JRE is required.

## Server installation

Basically same as local, adjust the port and mail sender in `config.properties` file.

## Command line usage

Run `genetika.bat --help` or `genetika.sh --help` to list commands. 

### Bulk analysis of fasta files in a folder

Point the program to folder that contains files `*.fasta` and it will output `results`
folder (inside that folder) with CSV files containing palindromes.

### Downloading sequences from NCBI

Create a CSV file with sequence name and NCBI ID on each row and point the program to this file.
Remember to set output folder using `target` parameter.

Example of CSV structure:

	Chara vulgaris	NC_005255.1
	Chlorokybus atmophyticus SAG 48.80	NC_009630.1
	Closterium baillyanum	NC_022860.1
	Alkalitalea saponilacus strain	NZ_CP021904.1
	tyrosine-protein kinase	NP_001098858.1
	
Structure of CSV files (it is actually TSV file):
	
	Title<-- TAB -->NCBI ID 

More info for command line usage is in program's help.

----
# Version 2.8.2
1. Fix NCBI column position

# Version 2.8.1
1. Support NCBI id without underscore in CSV files

# Version 2.8.0
1. Added command line options for IR spacer (min, max), mismatch (accepted as string with a dash e.g. "0,1") and dinucleotide filter (filter ATATAT) to reflect web server possibilities. 

# Version 2.7.4
1. Extended NCBI ID matching in CSV files. Current mask is now: one or two letters + underscore + mix of letters and numbers + optional dot followed by numbers

# Version 2.7
1. Command line interface for bulk analysis. 

# Version 2.6
1. Implemented p53 predictor.
2. Code maintenance.
3. NCBI redirect handling.

# Version: 2.5
1. Extends genome energy calculation.

# Version: 2.4
1. Circular genome analysis support.
2. JasDB for storage of user data, genomes and results.
4. User accounts.

# Version: 2.3
1. Přidání p53 binding predictor
2. Možnost zadat genom jako text
3. Odstranění přebytečných závislostí
4. Omezení výstupu + nastavení v konfiguraci

# Version: 2.2
1. Implementace importu ve FASTA formátu
    - Znaky mimo ACTG při porovnávání palindromu vždy generují mismatches
2. Opravy chyb
    - [#14]: implementace pro Plain a Fasta
    - [#15]: odstranění mezer se provádí při importech
    - [#17]: odstraněno genumu z paměti, načítá se při každém dotazu.
    - [#18]: náš program odstraňuje palindromy, které jsou součástí jiných palindromů.

# Version: 2.1
1. Vylepšeno užiatelské rozhraní
    - Filtrování 
    - Oprava chyb při změnách filtrů
    - Opraveno odesilání genomu pod chome
2. Implementece odesílá plain DNA s automatickýcm odstraňováním prázných znaků.
3. První verze implamnece importu FASTA podle kodovaní: Amino Acid Code

# Version 2.0
* Totální refaktoring jak rozgraní tak samotné logiky aplikace.