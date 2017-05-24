#!/bin/bash

#echo I18N disabled, please edit i18n/make.sh and enable it again.
#exit 0


PROJECT=jupidator
PROJECT_LOC=../core
TARGET=${PROJECT_LOC}/target/classes
SRC=${PROJECT_LOC}/src/main/java
CLASSPATH=com.panayotis.jupidator.i18n
BUILD=build
PREFIX=Messages_
FUNC=_t

cd `dirname $0`
SELF=`pwd`

CLASSDIR=`echo $CLASSPATH | sed -e 's/\./\//g'`

export PATH=$PATH:/opt/local/bin/:/usr/local/opt/gettext/bin


# Create POT model file
rm -rf "$BUILD"
rm -f $PROJECT.pot
mkdir $BUILD
cd "$SELF/$SRC"
xgettext --sort-by-file --from-code=utf-8 -k${FUNC} `find . | grep '.java$'` -d . -o "$SELF/$PROJECT.pot_u"
grep -v <"$SELF/$PROJECT.pot_u" >"$SELF/$PROJECT.pot" "POT-Creation-Date"
rm "$SELF/$PROJECT.pot_u"


# Make resource files
cd "$SELF"
for FILE in *.po ; do
	LNG=`echo $FILE | sed -e 's/\.po$//g'`
	if [ "$LNG.po" -ot "${PROJECT}.pot" ] ; then
		printf "Remaking po file for language \"$LNG\""
		msgmerge --no-fuzzy-matching --update --indent --sort-by-file --backup=none "$LNG.po" "$PROJECT.pot" || exit 1
		grep -v <"$LNG.po" >"$LNG.po_nd" "POT-Creation-Date"
		mv "$LNG.po_nd" "$LNG.po"
		
		JAVAC=javac msgfmt -d $BUILD --java2 --resource="$CLASSPATH.${PREFIX}$LNG" "$FILE" || exit 1
	fi
done

