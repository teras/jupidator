BIN=../../src/java/jupidator/launcher/JublerUpdate

all:
	gcc -Os -arch ppc -arch i386 -arch x86_64 -mmacosx-version-min=10.4 -framework Security -o "${BIN}" main.c
	strip "${BIN}"
