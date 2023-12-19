.PHONY: clean all
PLANTUML=plantuml

all: domain-model.png system-sequence-diagram-1.png system-sequence-diagram-2.png class-diagram.png sequence-diagram.png

domain-model.png: domain-model.txt
	$(PLANTUML) domain-model.txt

system-sequence-diagram-1.png: system-sequence-diagram-1.txt
	$(PLANTUML) system-sequence-diagram-1.txt

system-sequence-diagram-2.png: system-sequence-diagram-2.txt
	$(PLANTUML) system-sequence-diagram-2.txt

class-diagram.png: class-diagram.txt
	$(PLANTUML) class-diagram.txt

sequence-diagram.png: sequence-diagram.txt
	$(PLANTUML) sequence-diagram.txt

clean:
	rm *.png
