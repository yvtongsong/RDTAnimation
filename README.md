# Reliable Data Transfer Protocol Animation

![animation](https://github.com/user-attachments/assets/241cba73-0566-4082-9102-d0090348308f)

## Support

- Non-pipelined reliable data transfer
- GBN (Go-Back-N)
- SR (Selective Repeat)

## Project Structure

```txt
.
├── Makefile
├── dist
│   └── RDTAnimation.jar
└── src
    └── main
        └── java
            └── com
                └── yutongsong
                    └── rdtanimation
                        ├── README.md
                        ├── animation.gif
                        ├── app
                        │   ├── GBNAnimation.java
                        │   ├── Main.java
                        │   ├── RDTAnimation.java
                        │   ├── SRAnimation.java
                        │   └── StartMenu.java
                        ├── component
                        │   ├── ButtonActionListener.java
                        │   ├── Channel.java
                        │   ├── Direction.java
                        │   ├── GBNChannel.java
                        │   ├── GUI.java
                        │   ├── NotificationBoard.java
                        │   ├── SRChannel.java
                        │   └── State.java
                        └── util
                            ├── ChannelUtil.java
                            └── Constant.java
```

## Run

**Make sure you have installed java >= 17.**

### Method 1

Just run `dist/RDTAnimation.jar` file.

On Linux/macOS:

```bash
java -jar ./dist/RDTAnimation.jar
```

On Windows:

```powershell
java -jar .\dist\RDTAnimation.jar
```

### Method 2

If you use an IDE like intelliJ, run `app::Main`.

### Method 3

If you are not in Windows, use `make`.

```
make run
```
