clean:
	rm build/distributions/* && rm -rf genetika-*

distZip:
	./gradlew distZip

extract:
	unzip build/distributions/*.zip && chmod u+x genetika-*/*.sh

all: clean distZip extract

runCustom:
	cd genetika-2.8.1 && ./genetika.sh -dlcsv ~/Downloads/Monkeypox-genomes-NCBI.csv -t out