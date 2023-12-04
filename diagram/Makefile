.PHONY: clean all
PLANTUML=plantuml

all: domain-model.png system-sequence-diagram-2.png

domain-model.png: domain-model.txt
	$(PLANTUML) domain-model.txt

system-sequence-diagram-2.png: system-sequence-diagram-2.txt
	$(PLANTUML) system-sequence-diagram-2.txt

clean:
	rm *.png
