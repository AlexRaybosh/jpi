#!/bin/bash


echo ROOT=$ROOT SRCDIR=$SRCDIR JAVA_BUILD=$JAVA_BUILD
export CLASSPATH="."

VERSION=$(git rev-parse HEAD)

echo $VERSION > $JAVA_BUILD/VERSION
echo $VERSION > $ROOT/classes/VERSION


mostrecent=$(find $SRCDIR -type f  \( -name '*java' -o -name Makefile -o -name build_java.sh \) \
	-printf "%T@\0%p\0" | \
	awk '{if ($0>max) {max=$0;getline mostrecent} else getline}END{print mostrecent}' RS='\0') \
	|| exit 1
echo Latest java file $mostrecent

mr_ts=$(stat -c %Y $mostrecent)
c_ts=$mr_ts-1
test -f $JAVA_BUILD/compiled && c_ts=$(stat -c %Y $JAVA_BUILD/compiled)


if [ $c_ts -gt $mr_ts ]; then
	echo "Latest java file $mostrecent timestamp=$mr_ts, but it was already compiled at $c_ts"
	exit 0	
else
	echo "Latest java file $mostrecent timestamp=$mr_ts, too new, comparing to compiled at $c_ts"
fi

unset CLASSPATH
CLASSPATH=$JAVA_BUILD
$JAVA_HOME/bin/java -version

if [ -d $ROOT/javalib ]; then 
for jar in $(find $ROOT/javalib -type f); do
	CLASSPATH="$CLASSPATH:$jar"
done
fi

$JAVA_HOME/bin/javac -source 1.7 -target 1.7 -O -cp $CLASSPATH \
	-d $JAVA_BUILD -sourcepath $SRCDIR \
	$(find . -name '*.java') \
	|| exit 1

#echo Copy SoLoader as a real java
/bin/cp $SRCDIR/jpi/utils/loader/SoLoader.fake-java $JAVA_BUILD/jpi/utils/loader/SoLoader.java || exit 1
echo Compile SoLoader
( cd $JAVA_BUILD && $JAVA_HOME/bin/javac -source 1.7 -target 1.7 -d . jpi/utils/loader/SoLoader.java  && /bin/rm jpi/utils/loader/SoLoader.java) || exit 1

echo cp -f $JAVA_BUILD/jpi/utils/loader/SoLoader.class $JAVA_BUILD/jpi/utils/loader/SoLoader.bytes 
cp -f $JAVA_BUILD/jpi/utils/loader/SoLoader.class $JAVA_BUILD/jpi/utils/loader/SoLoader.bytes || exit 1
echo cp -f $JAVA_BUILD/jpi/utils/loader/SoLoader.class $ROOT/classes/jpi/utils/loader/SoLoader.bytes || exit 1
mv -f $JAVA_BUILD/jpi/utils/loader/SoLoader.class $ROOT/classes/jpi/utils/loader/SoLoader.bytes || exit 1


echo Find and copy any resources 
for p in $(find $SRCDIR -name '*.properties'); do
	cp $p $JAVA_BUILD/$(dirname $p) || exit 1
	cp $p $ROOT/classes/$(dirname $p) || exit 1
done

touch $JAVA_BUILD/compiled
