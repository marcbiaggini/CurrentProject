#!/bin/bash

cd ../svg/flags/
for i in *.svg
do
	echo "Doing $i"
	FNAME=`basename $i`
	echo "	Doing MDPI"
	convert -geometry 32x32   $i ../../src/main/res/drawable-mdpi/`echo country_${FNAME%.*}.png | tr '[:upper:]' '[:lower:]'`
	echo "	Doing HDPI"
	convert -geometry 48x48   $i ../../src/main/res/drawable-hdpi/`echo country_${FNAME%.*}.png | tr '[:upper:]' '[:lower:]'`
	echo "	Doing XHDPI"
	convert -geometry 64x64   $i ../../src/main/res/drawable-xhdpi/`echo country_${FNAME%.*}.png | tr '[:upper:]' '[:lower:]'`
	echo "	Doing XXHDPI"
	convert -geometry 96x96   $i ../../src/main/res/drawable-xxhdpi/`echo country_${FNAME%.*}.png | tr '[:upper:]' '[:lower:]'`
	echo "	Doing XXXHDPI"
	convert -geometry 128x128 $i ../../src/main/res/drawable-xxxhdpi/`echo country_${FNAME%.*}.png | tr '[:upper:]' '[:lower:]'`
done
