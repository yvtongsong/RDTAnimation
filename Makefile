default:
	javac -d out *.java

run:
	javac -d out *.java
	java -cp out Main
