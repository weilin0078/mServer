@echo off
@title 疯神冒险岛079V3版 群论坛：mxd.592uc.com QQ群号：542123915
Color 0B
set path=jre7\bin;%SystemRoot%\system32;%SystemRoot%;%SystemRoot%
set JRE_HOME=jre7
set JAVA_HOME=\jre7\bin
set CLASSPATH=.;dist\*
java  -Xms512m -Xmx512m -Xss128k -XX:ReservedCodeCacheSize=128m -Dnet.sf.odinms.wzpath=wz gui.KinMS
pause