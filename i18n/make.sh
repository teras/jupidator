#!/bin/bash

#echo I18N disabled, please edit i18n/make.sh and enable it again.
#exit 0


PROJECT=jupidator
TARGET=../build/classes
SRC=../src/java
CLASSPATH=com.panayotis.jupidator.i18n
BUILD=build
PREFIX=Messages_

pushd . >/dev/null ; cd `dirname $0` ; SELF=`pwd` ; popd >/dev/null
CLASSDIR=`echo $CLASSPATH | sed -e 's/\./\//g'`

export PATH=$PATH:/opt/local/bin/


# Ignore if desired
if [ "$1" == "ignore" ] ; then
	exit
fi

# Cleanup if desired
if [ "$1" == "clean" ] ; then
	echo I18N Clean up
	rm -rf $BUILD
	rm -f *.mo
	rm -f "$SELF"/$TARGET/$CLASSDIR/${PREFIX}*.class
	exit
fi


# Create POT model file
cd "$SELF"
if [ ! -d $BUILD ] ; then
	mkdir $BUILD
	rm -f $PROJECT.pot
	cd "$SELF/$SRC" >/dev/null
	xgettext --sort-by-file --from-code=utf-8 -k_ `find . | grep '.java$'` -d . -o "$SELF/$PROJECT.pot_u"
	grep -v <"$SELF/$PROJECT.pot_u" >"$SELF/$PROJECT.pot" "POT-Creation-Date"
	rm "$SELF/$PROJECT.pot_u"
fi


# Make resource files
cd "$SELF"
for FILE in *.po ; do
	LNG=`echo $FILE | sed -e 's/\.po$//g'`
	if [ "$LNG.po" -ot "${PROJECT}.pot" ] ; then
		printf "Remaking po file for language \"$LNG\""
		msgmerge --no-fuzzy-matching --update --indent --sort-by-file --backup=none "$LNG.po" "$PROJECT.pot"
		if [ $? != 0 ] ; then exit 1; fi
		grep -v <"$LNG.po" >"$LNG.po_nd" "POT-Creation-Date"
		mv "$LNG.po_nd" "$LNG.po"
		
		JAVAC=javac msgfmt -d . --java2 --resource="$CLASSPATH.${PREFIX}$LNG" "$FILE"
		if [ $? != 0 ] ; then exit 1; fi
		mv $CLASSDIR/${PREFIX}*.class $BUILD
		rm -fr com
	fi
done

# Move build files to project directory
cd $BUILD
mkdir -p "$SELF/$TARGET/$CLASSDIR"
for FILE in *.class ; do
	DEST="$SELF/$TARGET/$CLASSDIR/$FILE"
	if [ "$DEST" -ot "$FILE" ] ; then
		cp "$FILE" "$DEST"
		echo Copying $FILE
	fi
done
