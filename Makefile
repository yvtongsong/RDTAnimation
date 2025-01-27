# Variables
SRC_DIR = src/main/java
BIN_DIR = bin
MAIN_CLASS = com.yutongsong.rdtanimation.app.Main
JAR_NAME = RDTAnimation.jar

# Find all .java files in the source directory
SOURCES = $(shell find $(SRC_DIR) -name "*.java")

# Default target
all: compile

# Compile all Java source files
compile:
	mkdir -p $(BIN_DIR)
	javac -d $(BIN_DIR) $(SOURCES)

# Create a runnable JAR file
jar: compile
	mkdir -p dist
	jar -cvfe dist/$(JAR_NAME) $(MAIN_CLASS) -C $(BIN_DIR) .

# Run the program
run: compile
	java -classpath $(BIN_DIR) $(MAIN_CLASS)

# Clean compiled files and output directories
clean:
	rm -rf $(BIN_DIR) dist

# Phony targets
.PHONY: all compile jar run clean
